package frc.robot.utils.MotorTestingServer;

public final class Status {
    // TODO: The status should use integers instead of strings.
    public static enum status {
        WAITING ("Waiting"),
        RUNNING ("Running");

        String statusLabel;

        status(String statusLabel) {
            this.statusLabel = statusLabel;
        }

        String asString() {
            return statusLabel;
        }
    }
}
