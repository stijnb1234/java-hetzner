package nl.sbdeveloper.hetznerapi;

public class HetznerApiException extends Exception {
    public enum HetznerApiExceptionCause {
        CONFIG_ERROR,
        RESOURCE_NOT_FOUND,
        BAD_PARAMETERS_ERROR,
        AUTH_ERROR,
        API_ERROR,
        INTERNAL_ERROR
    }

    private final HetznerApiExceptionCause cause;

    public HetznerApiException(String message, HetznerApiExceptionCause ovhCause) {
        super(message);
        this.cause = ovhCause;
    }

    public HetznerApiExceptionCause getApiCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "HetznerApiException{" +
                "cause=" + cause +
                '}';
    }
}
