package frc.robot.utils.MotorTestingServer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj.DriverStation;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.utils.MotorTestingServer.Status.status;

import static frc.robot.utils.MotorTestingServer.Status.status;
import static frc.robot.utils.MotorTestingServer.Configuration.m_robotConfiguration;

public final class Server {
    private static final String m_robotName = m_robotConfiguration.robotName();
    private static final HashMap<Integer, String> m_motorNames = m_robotConfiguration.motorNames();
    private static final HashMap<Integer, String> m_testNames = m_robotConfiguration.testNames();

    private static final HashMap<Integer, String> m_graphNames = new HashMap<>();
    private static final HashMap<Integer, HashMap<Integer, ArrayList<Double>>> m_testResults = new HashMap<>();

    private static boolean m_running;
    private static int m_dartTestSignalListenerHandle;

    private static NetworkTableInstance m_networkTables;
    private static NetworkTable m_networkTable;

    private static IntegerSubscriber m_dartTestTypeSubscriber;
    private static BooleanSubscriber m_dartTestSignalSubscriber;

    private static StringPublisher m_robotTestTypesPublisher;
    private static StringPublisher m_robotTestStatusPublisher;

    private static StringPublisher m_robotNamePublisher;
    private static IntegerPublisher m_robotTestTypePublisher;

    private static StringPublisher m_robotMotorNamesPublisher;
    private static StringPublisher m_robotGraphNamesPublisher;
    private static StringPublisher m_robotTestResultsPublisher;

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
            m_robotTestTypesPublisher.set(m_testNames.toString()); // Sends the HashMap to the application as a String.
            m_robotTestStatusPublisher.set(status.WAITING.asString()); // Sets the default status to "Waiting".

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
        if (m_testNames.containsKey(testKey)) {
            m_robotTestStatusPublisher.set(status.RUNNING.asString());

            runTest(testKey);
        }

        // Incase it gets set to "Running" even when it isn't, it always gets set back to "Waiting".
        m_robotTestStatusPublisher.set(status.WAITING.asString());
    }

    private static void runTest(int testKey) {
        // Prints a message like "Running Test 1 (Swerve Test)." to the console.
        System.out.println("Running Test " + testKey + "(" + m_testNames.get(testKey) + ").");

        m_graphNames.clear();
        m_testResults.clear();

        // For some reason this doesn't immediately clear the results from the application.
        // I'm not sure why, but I think it's better that way, to wait until it gets the new results.
        m_robotGraphNamesPublisher.set("{}");
        m_robotTestResultsPublisher.set("{}");

        final HashMap<Integer, ArrayList<Double>> graph1Data = new HashMap<>();
        final HashMap<Integer, ArrayList<Double>> graph2Data = new HashMap<>();
        final HashMap<Integer, ArrayList<Double>> graph3Data = new HashMap<>();
        final HashMap<Integer, ArrayList<Double>> graph4Data = new HashMap<>();
        final HashMap<Integer, ArrayList<Double>> graph5Data = new HashMap<>();
        final HashMap<Integer, ArrayList<Double>> graph6Data = new HashMap<>();

        switch(testKey) {
            case 1:
                final ArrayList<Double> motor1Data = new ArrayList<>();
                final ArrayList<Double> motor2Data = new ArrayList<>();

                final ArrayList<Double> motor3Data = new ArrayList<>();
                final ArrayList<Double> motor4Data = new ArrayList<>();

                final ArrayList<Double> motor5Data = new ArrayList<>();
                final ArrayList<Double> motor6Data = new ArrayList<>();

                final ArrayList<Double> motor7Data = new ArrayList<>();
                final ArrayList<Double> motor8Data = new ArrayList<>();

                final ArrayList<Double> motor9Data = new ArrayList<>();
                
                final Random random = new Random();

                Commands.race(
                    new FunctionalCommand(
                        () -> {},

                        () -> {
                            motor1Data.add(-1 + 2 * random.nextDouble());
                            motor2Data.add(-1 + 2 * random.nextDouble());

                            motor3Data.add(-1 + 2 * random.nextDouble());
                            motor4Data.add(-1 + 2 * random.nextDouble());

                            motor5Data.add(-1 + 2 * random.nextDouble());
                            motor6Data.add(-1 + 2 * random.nextDouble());

                            motor7Data.add(-1 + 2 * random.nextDouble());
                            motor8Data.add(-1 + 2 * random.nextDouble());

                            motor9Data.add(-1 + 2 * random.nextDouble());
                        },

                        interrupted -> {},
                        () -> false
                    ),

                    new WaitCommand(15)
                ).andThen(
                        new InstantCommand(
                            () -> {
                                graph1Data.put(1, motor1Data);
                                graph1Data.put(2, motor2Data);

                                graph2Data.put(3, motor1Data);
                                graph2Data.put(4, motor2Data);

                                graph3Data.put(9, motor1Data);

                                graph4Data.put(5, motor1Data);
                                graph4Data.put(6, motor2Data);

                                graph5Data.put(7, motor1Data);
                                graph5Data.put(8, motor2Data);

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

                                testEnd(testKey);
                            }
                        )
                ).schedule();

            // default:
            //     DriverStation.reportWarning("Invalid test ID: " + testKey, false);
        }
    }

    private static final void testEnd(int testKey) {
        // The test results publisher is what actually tells the app to reload the graphs.
        // They should be updated at the same time, but just incase, everything else is set before setting the results.
        m_robotTestTypePublisher.set(testKey);

        m_robotGraphNamesPublisher.set(m_graphNames.toString());
        m_robotTestResultsPublisher.set(m_testResults.toString());
    }
}
