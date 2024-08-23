package frc.robot;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random; // TODO: Remove this for code on the actual robot.

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.StringPublisher;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Frc7048MotorTesting {
    private HashMap<Integer, String> m_testTypes = new HashMap<Integer, String>();
    private HashMap<Integer, String> m_testStatuses = new HashMap<Integer, String>();

    private HashMap<Integer, String> m_motorNames = new HashMap<Integer, String>();
    private HashMap<Integer, String> m_graphNames = new HashMap<Integer, String>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Double>>> m_testResults = new HashMap<Integer, HashMap<Integer, ArrayList<Double>>>();

    private IntegerSubscriber m_dartTestTypeSubscriber;
    private BooleanSubscriber m_dartTestSignalSubscriber;

    private StringPublisher m_robotTestTypesPublisher;
    private StringPublisher m_robotTestStatusPublisher;

    private StringPublisher m_robotNamePublisher;
    private IntegerPublisher m_robotTestTypePublisher;

    private StringPublisher m_robotMotorNamesPublisher;
    private StringPublisher m_robotGraphNamesPublisher;
    private StringPublisher m_robotTestResultsPublisher;

    private NetworkTableInstance m_networkTables;
    private NetworkTable m_networkTable;

    public Frc7048MotorTesting() {
        m_networkTables = NetworkTableInstance.getDefault();
        m_networkTable = m_networkTables.getTable("FRC7048MotorTesting");

        // These are used so the robot can communicate its status to the application. These should not be changed (unless the application is updated).
        m_testStatuses.put(1, "Waiting");
        m_testStatuses.put(2, "Running");

        // Robot configuration
        String robotName = "2024 Duster"; // This is used as a path to store the baselines and cannot contain special characters.

        m_testTypes.put(1, "Swerve Test"); // The key is used to store the baselines, and the value is what is selected.
        m_testTypes.put(2, "Another Test");

        m_motorNames.put(1, "DriveFL"); // These are used to generate the keys for the graphs. The key is the CAN ID.
        m_motorNames.put(2, "SwerveFL");
        m_motorNames.put(3, "DriveFR");
        m_motorNames.put(4, "SwerveFR");
        m_motorNames.put(5, "DriveBL");
        m_motorNames.put(6, "SwerveBL");
        m_motorNames.put(7, "DriveBR");
        m_motorNames.put(8, "SwerveBR");

        m_motorNames.put(9, "Random Motor");

        // Creating subscribers.
        m_dartTestTypeSubscriber = m_networkTable.getIntegerTopic(".dart/TestType").subscribe(1);
        m_dartTestSignalSubscriber = m_networkTable.getBooleanTopic(".dart/TestSignal").subscribe(false);

        // Creating publishers.
        m_robotTestTypesPublisher = m_networkTable.getStringTopic(".robot/TestTypes").publish();
        m_robotTestStatusPublisher = m_networkTable.getStringTopic(".robot/TestStatus").publish();

        m_robotNamePublisher = m_networkTable.getStringTopic(".robot/Name").publish();
        m_robotTestTypePublisher = m_networkTable.getIntegerTopic(".robot/.data/TestType").publish();

        m_robotMotorNamesPublisher = m_networkTable.getStringTopic(".robot/MotorNames").publish();
        m_robotGraphNamesPublisher = m_networkTable.getStringTopic(".robot/.data/GraphNames").publish();
        m_robotTestResultsPublisher = m_networkTable.getStringTopic(".robot/.data/TestResults").publish();

        // Setting the values for all of the publishers.
        m_robotTestTypesPublisher.set(m_testTypes.toString()); // Sends the HashMap to the application as a String.
        m_robotTestStatusPublisher.set(m_testStatuses.get(1)); // Sets the default status to "Waiting".

        m_robotNamePublisher.set(robotName);

        m_robotMotorNamesPublisher.set(m_motorNames.toString());
        m_robotGraphNamesPublisher.set(m_graphNames.toString());
        m_robotTestResultsPublisher.set(m_testResults.toString());

        // Whenever the test signal is sent, we will schedule the selected test.
        m_networkTables.addListener(
            m_dartTestSignalSubscriber,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),

            event -> {
                if (m_dartTestSignalSubscriber.get()) {
                    scheduleTest((int) m_dartTestTypeSubscriber.get());
                }
            }
        );
    }

    private void scheduleTest(int testKey) {
        // Notify the application that the test is running if the test is valid.
        if (m_testTypes.containsKey(testKey)) {
            m_robotTestStatusPublisher.set(m_testStatuses.get(2));

            runTest(testKey);
        }

        // Incase it gets set to "Running" even when it isn't, it always gets set back to "Waiting".
        m_robotTestStatusPublisher.set(m_testStatuses.get(1));
    }

    private void runTest(int testKey) {
        // Prints a message like "Running Test 1 (Swerve Test)." to the console.
        System.out.println("Running Test " + testKey + "(" + m_testTypes.get(testKey) + ").");

        m_graphNames.clear();
        m_testResults.clear();

        // For some reason this doesn't immediately clear the results from the application.
        // I'm not sure why, but I think it's better that way, to wait until it gets the new results.
        m_robotGraphNamesPublisher.set("{}");
        m_robotTestResultsPublisher.set("{}");

        switch(testKey) {
            case 1:
                wait(5000); // This just adds a delay since the test is not actually using commands.
                // Here, a function would run that would stop all other inputs to the motors, and then they would be set to their speeds.
                // A command could also change them to run at different speeds, and forward and backward throughout the test routine.

                // There might be a better way of doing this.
                var graph1Data = new HashMap<Integer, ArrayList<Double>>();
                var graph2Data = new HashMap<Integer, ArrayList<Double>>();
                var graph3Data = new HashMap<Integer, ArrayList<Double>>();
                var graph4Data = new HashMap<Integer, ArrayList<Double>>();
                var graph5Data = new HashMap<Integer, ArrayList<Double>>();
                var graph6Data = new HashMap<Integer, ArrayList<Double>>();

                var motor1Data = new ArrayList<Double>();
                var motor2Data = new ArrayList<Double>();

                var motor3Data = new ArrayList<Double>();
                var motor4Data = new ArrayList<Double>();

                var motor9Data = new ArrayList<Double>();

                var motor5Data = new ArrayList<Double>();
                var motor6Data = new ArrayList<Double>();

                var motor7Data = new ArrayList<Double>();
                var motor8Data = new ArrayList<Double>();

                Random random = new Random();

                // This would be a 15 second test.
                for (double i = 0; i < 15; i += 0.02) {
                    double randomValue = -1 + 2 * random.nextDouble();
                    motor1Data.add(randomValue);
                    randomValue = -1 + 2 * random.nextDouble();
                    motor2Data.add(randomValue);

                    randomValue = -1 + 2 * random.nextDouble();
                    motor3Data.add(randomValue);
                    randomValue = -1 + 2 * random.nextDouble();
                    motor4Data.add(randomValue);

                    randomValue = -1 + 2 * random.nextDouble();
                    motor5Data.add(randomValue);
                    randomValue = -1 + 2 * random.nextDouble();
                    motor6Data.add(randomValue);

                    randomValue = -1 + 2 * random.nextDouble();
                    motor7Data.add(randomValue);
                    randomValue = -1 + 2 * random.nextDouble();
                    motor8Data.add(randomValue);
                }

                // This would be a 5 second test.
                for (double i = 0; i < 5; i += 0.02) {
                    double randomValue = -1 + 2 * random.nextDouble();
                    motor9Data.add(randomValue);
                }

                // The top 3 graphs are 1, 2, 3, and the bottom 3 graphs are 4, 5, 6.
                graph1Data.put(1, motor1Data);
                graph1Data.put(2, motor2Data);

                graph2Data.put(3, motor3Data);
                graph2Data.put(4, motor4Data);

                graph3Data.put(9, motor9Data);

                graph4Data.put(5, motor5Data);
                graph4Data.put(6, motor6Data);

                graph5Data.put(7, motor7Data);
                graph5Data.put(8, motor8Data);
                
                m_graphNames.put(1, "SwerveFL");
                m_graphNames.put(2, "SwerveFR");
                m_graphNames.put(3, "Different Test Lengths");
                m_graphNames.put(4, "SwerveBL");
                m_graphNames.put(5, "SwerveBR");

                m_testResults.put(1, graph1Data);
                m_testResults.put(2, graph2Data);
                m_testResults.put(3, graph3Data);
                m_testResults.put(4, graph4Data);
                m_testResults.put(5, graph5Data);

            case 2:
                wait(5000); // See above.

            default:
                System.out.println("Invalid test ID: " + testKey);
        }

        // The test results publisher is what actually tells the app to reload the graphs.
        // They should be updated at the same time, but just incase, everything else is set before setting the results.
        m_robotTestTypePublisher.set(testKey);

        m_robotGraphNamesPublisher.set(m_graphNames.toString());
        m_robotTestResultsPublisher.set(m_testResults.toString());
    }

    // TODO: Remove this for code on the actual robot, this is just to make sure the random loop doesn't finish too quickly.
    private void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }

        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
