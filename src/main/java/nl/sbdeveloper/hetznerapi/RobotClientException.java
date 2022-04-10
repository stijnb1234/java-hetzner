package nl.sbdeveloper.hetznerapi;

public class RobotClientException extends Exception {
    private final int code;

    public RobotClientException(String message) {
        super(message);
        this.code = 0;
    }

    public RobotClientException(String message, int code) {
        super(message);
        this.code = code;
    }

    @Override
    public String toString() {
        return "RobotClientException{" +
                "code=" + code +
                "} " + super.toString();
    }
}
