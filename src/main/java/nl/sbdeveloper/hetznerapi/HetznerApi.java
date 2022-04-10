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
    private final String endpoint;
    private final String username;
    private final String password;

    public HetznerApi(String url, String username, String password) {
        endpoint = url;
        this.username = username;
        this.password = password;
    }

    public HetznerApi(String username, String password) {
        this("https://robot-ws.your-server.de", username, password);
    }

    private void assertAllConfigNotNull() throws RobotClientException {
        if (username == null || password == null) {
            throw new RobotClientException("API Client Configuration incomplete!");
        }
    }

    public String get(String path) throws RobotClientException {
        assertAllConfigNotNull();
        return get(path, "", true);
    }

    public String get(String path, boolean needAuth) throws RobotClientException {
        assertAllConfigNotNull();
        return get(path, "", needAuth);
    }

    public String get(String path, String body, boolean needAuth) throws RobotClientException {
        assertAllConfigNotNull();
        return call("GET", body, path, needAuth);
    }

    public String put(String path, String body, boolean needAuth) throws RobotClientException {
        assertAllConfigNotNull();
        return call("PUT", body, path, needAuth);
    }

    public String post(String path, String body, boolean needAuth) throws RobotClientException {
        assertAllConfigNotNull();
        return call("POST", body, path, needAuth);
    }

    public String delete(String path, String body, boolean needAuth) throws RobotClientException {
        assertAllConfigNotNull();
        return call("DELETE", body, path, needAuth);
    }

    private String call(String method, String body, String path, boolean needAuth) throws RobotClientException {
        try {
            URL url = new URL(endpoint + path);

            // prepare
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod(method);
            request.setReadTimeout(30000);
            request.setConnectTimeout(30000);
            request.setRequestProperty("Accept", "application/json");
            request.setRequestProperty("User-Agent", "HetznerAPI/" + getClass().getPackage().getImplementationVersion());
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

            BufferedReader in;
            int responseCode = request.getResponseCode();
            if (responseCode >= 400 && responseCode <= 503) {
                in = new BufferedReader(new InputStreamReader(request.getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(request.getInputStream()));
            }

            // build response
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode >= 400 && responseCode <= 503) {
                throw new RobotClientException(response.toString(), responseCode);
            } else {
                return response.toString();
            }
        } catch (IOException e) {
            throw new RobotClientException(e.getMessage());
        }
	}
}
