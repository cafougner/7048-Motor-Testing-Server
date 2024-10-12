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

public class MotorTestingServer {
    // The robot name is used as a path and cannot contain any special characters.
    private final static String m_robotName = "2024 Duster";

    // The motor names are displayed below the graphs and should be short, the key is arbitrary but
    // for consistency should be the CAN ID of the motor, even though it is never used.
    private final static HashMap<Integer, String> m_motorNames = new HashMap<Integer, String>(){{
        put(1, "DriveFL"); put(2, "SwerveFL"); // Graph 1 (top left)
        put(3, "DriveFR"); put(4, "SwerveFR"); // Graph 2 (top middle)
        put(9, "Random Motor"); // Graph 3 (top right)

        put(5, "DriveBL"); put(6, "SwerveBL"); // Graph 4 (bottom left)
        put(7, "DriveBR"); put(8, "SwerveBR"); // Graph 5 (bottom middle)
        // Graph 6 (bottom right)
    }};

    // The test names are displayed in the order given and should also be short, the key is used
    // as part of the file name.
    private final static HashMap<Integer, String> m_testTypes = new HashMap<Integer, String>(){{
        put(1, "Swerve Test");
        put(2, "Another Test");
    }};

    // Everything else should not be changed unless the behavior is modified as well.
    private static boolean m_running = false;
    private static int m_dartTestSignalListenerHandle;

    private static HashMap<Integer, String> m_testStatuses = new HashMap<Integer, String>(){{
        put(1, "Waiting"); put(2, "Running");
    }};

    private static HashMap<Integer, String> m_graphNames = new HashMap<Integer, String>();
    private static HashMap<Integer, HashMap<Integer, ArrayList<Double>>> m_testResults = new HashMap<Integer, HashMap<Integer, ArrayList<Double>>>();

    private static IntegerSubscriber m_dartTestTypeSubscriber;
    private static BooleanSubscriber m_dartTestSignalSubscriber;

    private static StringPublisher m_robotTestTypesPublisher;
    private static StringPublisher m_robotTestStatusPublisher;

    private static StringPublisher m_robotNamePublisher;
    private static IntegerPublisher m_robotTestTypePublisher;

    private static StringPublisher m_robotMotorNamesPublisher;
    private static StringPublisher m_robotGraphNamesPublisher;
    private static StringPublisher m_robotTestResultsPublisher;

    private static NetworkTableInstance m_networkTables;
    private static NetworkTable m_networkTable;

    public static boolean isRunning() {
        return m_running;
    }

    public static void startIfNotRunning() {
        if (!m_running) {
            m_running = true;

            m_networkTables = NetworkTableInstance.getDefault();
            m_networkTable = m_networkTables.getTable("FRC7048MotorTesting"); 

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

            m_robotNamePublisher.set(m_robotName);

            m_robotMotorNamesPublisher.set(m_motorNames.toString());
            m_robotGraphNamesPublisher.set(m_graphNames.toString());
            m_robotTestResultsPublisher.set(m_testResults.toString());

            // Whenever the test signal is sent, we will schedule the selected test.
            m_dartTestSignalListenerHandle = m_networkTables.addListener(
                m_dartTestSignalSubscriber,
                EnumSet.of(NetworkTableEvent.Kind.kValueAll),

                event -> {
                    if (m_dartTestSignalSubscriber.get()) {
                        scheduleTest((int) m_dartTestTypeSubscriber.get());
                    }
                }
            );
        }
    }

    public static void stopIfRunning() {
        if (m_running) {
            m_running = false;

            m_networkTables.removeListener(m_dartTestSignalListenerHandle);

            m_dartTestTypeSubscriber.close();
            m_dartTestSignalSubscriber.close();

            m_robotNamePublisher.close();
            m_robotTestStatusPublisher.close();

            m_robotNamePublisher.close();
            m_robotTestTypePublisher.close();

            m_robotMotorNamesPublisher.close();
            m_robotGraphNamesPublisher.close();
            m_robotTestResultsPublisher.close();
        }
    }

    private static void scheduleTest(int testKey) {
        // Notify the application that the test is running if the test is valid.
        if (m_testTypes.containsKey(testKey)) {
            m_robotTestStatusPublisher.set(m_testStatuses.get(2));

            runTest(testKey);
        }

        // Incase it gets set to "Running" even when it isn't, it always gets set back to "Waiting".
        m_robotTestStatusPublisher.set(m_testStatuses.get(1));
    }

    private static void runTest(int testKey) {
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
                // Tests should also cancel themselves when/if m_running turns to false.

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
    private static void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }

        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
