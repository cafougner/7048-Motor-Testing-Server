<h1 align = "center">7048 Motor Testing Server</h1>
<p align = "center">The NT4 server code for the robot side of the <a href = "https://www.github.com/cafougner/7048-Motor-Testing">7048 Motor Testing</a> application.</p>

## Usage
See the [`Frc7048MotorTesting.java`](Frc7048MotorTesting.java) documentation for more configuration details.

To initialize the server, add the following to your `Robot.java`:
```java
public void telelopInit() {
    // See the https://github.com/cafougner/7048-Motor-Testing-Server README.md for more information.
    MotorTestingServer.stopIfRunning();

    // The rest of the teleop initialization code.
}

public void testInit() {
    // The rest of the test initialization code.

    // See the https://github.com/cafougner/7048-Motor-Testing-Server README.md for more information.
    MotorTestingServer.startIfNotRunning();
}
```

The server as is **CANNOT** be used, except for testing with the app, and it will need to be configured per robot. For testing, the test will need a way to directly drive the motors to test. It should use command-based programming to run the motors at set speeds for amounts of time. Optionally, the test can enable brake mode for some amount of time at the start to ensure that they are not moving, but it shouldn't be necessary.

## Next Version
### TODO:
* Nothing! It's *perfect*.

### Known Bugs:
* None! The code is *flawless*.
