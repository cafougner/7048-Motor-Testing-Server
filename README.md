<h1 align = "center">7048 Motor Testing Server</h1>
<p align = "center">The NT4 server code for the robot side of the <a href = "https://www.github.com/cafougner/7048-Motor-Testing">7048 Motor Testing</a> application.</p>

## Usage
See the [`Frc7048MotorTesting.java`](Frc7048MotorTesting.java) documentation for more configuration details.

To initialize the server, add the following to your `Robot.java`:
```java
public void teleopInit() {
    // The rest of the teleop initialization code.

    // See the https://github.com/cafougner/7048-Motor-Testing-Server README.md.
    final boolean MOTOR_TESTING_ENABLED = true;

    // This will attempt to start the motor testing server once teleop starts, and only if motor
    // testing is enabled and the robot is not connected to an FMS. It should never start during
    // an actual match, and can be completely disabled if it is not really needed or could fail.
    if (MOTOR_TESTING_ENABLED && !DriverStation.isFMSAttached()) {
        new Frc7048MotorTesting();
    }

    // This will give a reason for why the server didn't start, it can also be logged for later.
    else {
        DriverStation.reportWarning(
            "The motor testing server was not started because" +
            (MOTOR_TESTING_ENABLED ? "an FMS is attached." : "it is not enabled in this build."),
            false
        );
    }
}
```

Note that the server **CANNOT** be used as is, except for testing with the app, and will need to be configured per robot. An example implementation of a test (which just generates random values) is all that is included. An actual test should use the command-based framework to run motors for specific times.

## Next Version
### TODO:
* Nothing! It's *perfect*.

### Known Bugs:
* None! The code is *flawless*.
