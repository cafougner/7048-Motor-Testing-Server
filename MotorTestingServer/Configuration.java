package frc.robot.utils.MotorTestingServer;

import java.util.HashMap;

/**
 * The MotorTestingServer Configuration class contains all of the robot-specific configurations for
 * the server to use. More specifically, the static {@link #m_robotConfiguration} does. Information
 * about each of the configurations and what they are used for is provided in the constructor.
*/
public final class Configuration {
    public static final RobotConfiguration m_robotConfiguration = new RobotConfiguration(
        // The robot's name.
        // The name is used by the app to display what robot the test results are from. The name is
        // also used as a folder name to store baseline results, so should be a valid folder name.
        // It should follow a "[Year] [Name]" convention, but doesn't really matter what it is.
        "2024 Duster",

        // The motor's CAN IDs and names.
        // The CAN ID (key) isn't used anywhere in the app except to store motor-specific data. The
        // CAN ID does not need to actually match the motor's CAN ID, it just needs to be unique.

        // The motor name (value) is used by the app to generate a key for each of the graphs.
        // There is nothing to stop the text label from overflowing, so the name should be short.

        // The initial size of the HashMap can be specified and should match the number of motors.
        new HashMap<Integer, String>(9, 1.0f){{
            put(1, "DriveFL"); put(2, "SwerveFL");
            put(3, "DriveFR"); put(4, "SwerveFR");
            put(5, "DriveBL"); put(6, "SwerveBL");
            put(7, "DriveBR"); put(8, "SwerveBR");

            put(9, "Random Motor");
        }},

        // The test's ID and names.
        // The test ID (key) is used by the app as part of a folder name to store baseline results.
        // The test ID is also used by the app to send the selected test to the server.

        // The test name (value) is used by the app to give a dropdown menu for tests to select.
        // The test name is also used to display what test the test results are from.

        // The initial size of the HashMap can be specified and should match the number of tests.
        new HashMap<Integer, String>(2, 1.0f){{
            put(1, "Swerve Test"); put(2, "Another Test");
        }}
    );

    // Unless more robot-specific functionality is added, this should not need to be changed.
    // It does nothing except for store the configurations in a "struct" (a record, in java).
    static final record RobotConfiguration(
        String robotName,
        HashMap<Integer, String> motorNames,
        HashMap<Integer, String> testNames
    ) {}
}
