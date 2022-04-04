package nl.sbdeveloper.hetznerapi;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Simple low level wrapper over the Hetzner Robot REST API.
 *
 * @author mbsk, SBDeveloper
 */
public class HetznerApi {
    private static final String endpoint = "https://robot-ws.your-server.de";
    private final String username;
    private final String password;

    public HetznerApi(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private void assertAllConfigNotNull() throws HetznerApiException {
        if (username == null || password == null) {
            throw new HetznerApiException("", HetznerApiException.HetznerApiExceptionCause.CONFIG_ERROR);
        }
    }

    public String get(String path) throws HetznerApiException {
        assertAllConfigNotNull();
        return get(path, "", true);
    }

    public String get(String path, boolean needAuth) throws HetznerApiException {
        assertAllConfigNotNull();
        return get(path, "", needAuth);
    }

    public String get(String path, String body, boolean needAuth) throws HetznerApiException {
        assertAllConfigNotNull();
        return call("GET", body, path, needAuth);
    }

    public String put(String path, String body, boolean needAuth) throws HetznerApiException {
        assertAllConfigNotNull();
        return call("PUT", body, path, needAuth);
    }

    public String post(String path, String body, boolean needAuth) throws HetznerApiException {
        assertAllConfigNotNull();
        return call("POST", body, path, needAuth);
    }

    public String delete(String path, String body, boolean needAuth) throws HetznerApiException {
        assertAllConfigNotNull();
        return call("DELETE", body, path, needAuth);
    }

    private String call(String method, String body, String path, boolean needAuth) throws HetznerApiException {
        try {
            URL url = new URL(endpoint + path);

            // prepare
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod(method);
            request.setReadTimeout(30000);
            request.setConnectTimeout(30000);
            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // handle authentification
            if (needAuth) {
				String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
                request.setRequestProperty("Authorization", "Basic " + encoding);
            }

            if (body != null && !body.isEmpty()) {
                request.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(request.getOutputStream());
                out.writeBytes(body);
                out.flush();
                out.close();
            }

            String inputLine;
            BufferedReader in;
            int responseCode = request.getResponseCode();
            if (responseCode == 200) {
                in = new BufferedReader(new InputStreamReader(request.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(request.getErrorStream()));
            }

            // build response
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode == 200 || responseCode == 201) {
                // return the raw JSON result
                return response.toString();
            } else if (responseCode == 400) {
                throw new HetznerApiException(response.toString(), HetznerApiException.HetznerApiExceptionCause.BAD_PARAMETERS_ERROR);
            } else if (responseCode == 403) {
                throw new HetznerApiException(response.toString(), HetznerApiException.HetznerApiExceptionCause.AUTH_ERROR);
            } else if (responseCode == 404) {
                throw new HetznerApiException(response.toString(), HetznerApiException.HetznerApiExceptionCause.RESOURCE_NOT_FOUND);
            } else {
                throw new HetznerApiException(response.toString(), HetznerApiException.HetznerApiExceptionCause.API_ERROR);
            }
        } catch (IOException e) {
            throw new HetznerApiException(e.getMessage(), HetznerApiException.HetznerApiExceptionCause.INTERNAL_ERROR);
        }
	}
}
