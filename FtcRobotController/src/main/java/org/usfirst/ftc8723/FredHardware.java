package org.usfirst.ftc8723;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.Range;

import java.util.HashMap;
import java.util.Map;

public abstract class FredHardware extends OpMode {
    /*
     * Note: the configuration of the servos is such that
     * as the elbowServo servo approaches 0, the elbowServo position moves up (away from the floor).
     * Also, as the bucketServo servo approaches 0, the bucketServo opens up (drops the game element).
     */

    // position of the servos & power to the wheels
    private double elbowPosition;
    private double bucketPosition;
    private double shieldPositionL;
    private double shieldPositionR;
    private double lPower;
    private double rPower;
    private int armPosition;

    // timers and counters for debugging
    long startTime;
    long loopTimer;
    long loopCount;

    // hardware
    DcMotor motorLeft;
    DcMotor motorRight;
    DcMotor armMotor;
    Servo bucketServo;
    Servo elbowServo;
    Servo shieldServoL;
    Servo shieldServoR;
    ColorSensor colorSensor;
    LightSensor lightSensor;
    GyroSensor gyroSensor;
    UltrasonicSensor ultraSensor;

    // hardware errors
    Map<String, String> hardwareErrors = new HashMap<String, String>();

    /*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init() {

		/*
         * Use the hardwareMap to get the dc motors, servos, and sensors by name.
		 * Note that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */

        try {
            motorLeft = hardwareMap.dcMotor.get("motorLeft");
            if (motorLeft == null) {
                hardwareErrors.put("motorLeft", "not found");
            } else {
                motorLeft.setDirection(DcMotor.Direction.REVERSE);
            }

        } catch (Exception e) {
            hardwareErrors.put("motorLeft", e.getMessage());
        }

        try {
            motorRight = hardwareMap.dcMotor.get("motorRight");
            if (motorRight == null) {
                hardwareErrors.put("motorRight", "not found");
            }
        } catch (Exception e) {
            hardwareErrors.put("motorRight", e.getMessage());
        }

        try {
            armMotor = hardwareMap.dcMotor.get("armMotor");
            if (motorLeft == null) {
                hardwareErrors.put("armMotor", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("armMotor", e.getMessage());
        }

        try {
            // servo port 1
            elbowServo = hardwareMap.servo.get("elbowServo");
            if (elbowServo == null) {
                hardwareErrors.put("elbowServo", "not found");
            } else {
                elbowServo.setDirection(Servo.Direction.FORWARD);
            }

        } catch (Exception e) {
            hardwareErrors.put("elbowServo", e.getMessage());
        }

        try {
            // servo port 2
            bucketServo = hardwareMap.servo.get("bucketServo");
            if (bucketServo == null) {
                hardwareErrors.put("bucketServo", "not found");
            } else {
                bucketServo.setDirection(Servo.Direction.FORWARD);
            }

        } catch (Exception e) {
            hardwareErrors.put("bucketServo", e.getMessage());
        }

        try {
            // servo port 3
            shieldServoL = hardwareMap.servo.get("shieldServoL");
            if (shieldServoL == null) {
                hardwareErrors.put("shieldServoL", "not found");
            } else {
                shieldServoL.setDirection(Servo.Direction.FORWARD);
            }

        } catch (Exception e) {
            hardwareErrors.put("shieldServoL", e.getMessage());
        }

        try {
            // servo port 4
            shieldServoR = hardwareMap.servo.get("shieldServoR");
            if (shieldServoR == null) {
                hardwareErrors.put("shieldServoR", "not found");
            } else {
                shieldServoR.setDirection(Servo.Direction.REVERSE);
            }

        } catch (Exception e) {
            hardwareErrors.put("shieldServoR", e.getMessage());
        }

        try {
            // legacy module port 5
            colorSensor = hardwareMap.colorSensor.get("colorSensor");
            if (colorSensor == null) {
                hardwareErrors.put("colorSensor", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("colorSensor", e.getMessage());
        }

        try {
            // legacy module port 4
            lightSensor = hardwareMap.lightSensor.get("lightSensor");
            if (lightSensor == null) {
                hardwareErrors.put("lightSensor", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("lightSensor", e.getMessage());
        }

        try {
            gyroSensor = hardwareMap.gyroSensor.get("gyro");
            if (gyroSensor == null) {
                hardwareErrors.put("gyroSensor", "not found");
            } else {
                // start with the gyro rotation to zero
                gyroCalibrate();
            }

        } catch (Exception e) {
            hardwareErrors.put("gyroSensor", e.getMessage());
        }

        try {
            ultraSensor = hardwareMap.ultrasonicSensor.get("ultraSensor");
            if (ultraSensor == null) {
                hardwareErrors.put("ultraSensor", "not found");
            }
            else {
                LegacyModule legacyModule = hardwareMap.legacyModule.get("legacyModule");
                legacyModule.enable9v(ULTRASONIC_PORT, true);
            }

        } catch (Exception e) {
            hardwareErrors.put("ultraSensor", e.getMessage());
        }
    }

    /**
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        loopTimer = System.currentTimeMillis();
        stopAndReset();
    }


    /**
     * FredAutoRampRed and FredTeleOp should override this to do the normal loop functions
     */
    public abstract void innerLoop();

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        try {
            // don't do anything while resetting... other than check to see if we're still resetting
            if (resetting) {
                if (resetCompleted()) {
                    // set the motors back to standard mode
                    runUsingEncoders();
                    resetting = false;
                }
                else return;
            }

            // check if the arm is in the target position & cut power
            checkArmPosition();

            // perform the normal loop functions inside a try/catch to improve error handling
            innerLoop();

            // update standard telemetry
            updateTelemetry();

            // update loop debug telemetry
            updateTelemetry("Loop Timer", String.format("%d ms", timeSince(loopTimer)));
            updateTelemetry("Loops/Sec ", String.format("%d", (loopCount++ / startTime)/1000));
            loopTimer = System.currentTimeMillis();

        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message == null) message = e.fillInStackTrace().toString();
            telemetry.addData("loopError", e.getClass().getSimpleName() + " - " + message);
        }
    }

    /**
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }


    /**
     * send standard telemetry data back to driver station.  subclasses can override this to add
     * their own telemetry
     */
    protected void updateTelemetry() {
        updateTelemetry("colorSensor", String.format("rgb: %d,%d,%d", colorSensor.red(), colorSensor.green(), +colorSensor.blue()));
        updateTelemetry("lightSensor", String.format("%.2f", lightSensor.getLightDetected()));
        updateTelemetry("ultraSensor", String.format("%.2f", getUltrasonicLevel()));

        updateTelemetry("motorLeft", String.format("pwr: %.2f pos: %d", lPower, getEncoder(motorLeft)));
        updateTelemetry("motorRight", String.format("pwr: %.2f pos: %d", rPower, getEncoder(motorRight)));
        updateTelemetry("armMotor", String.format("pos: %d / %d", armPosition, getEncoder(armMotor)));

        updateTelemetry("elbowServo", String.format("%.2f / %.2f", elbowPosition, getPosition(elbowServo)));
        updateTelemetry("bucketServo", String.format("%.2f / %.2f", bucketPosition, getPosition(bucketServo)));
        updateTelemetry("shieldServoL", String.format("%.2f / %.2f", shieldPositionL, getPosition(shieldServoL)));
        updateTelemetry("shieldServoR", String.format("%.2f / %.2f", shieldPositionR, getPosition(shieldServoR)));

        updateTelemetry("gyroSensor", String.format("heading: %d", gyroHeading()));
    }

    private void updateTelemetry(String key, String value) {
        String error = hardwareErrors.get(key);
        if (error != null) {
            telemetry.addData(key, error);
        } else {
            telemetry.addData(key, value);
        }
    }

    /**
     * Set both drive wheel encoders to run
     */
    public void runUsingEncoders() {
        // perform the action on both motors.
        if (motorLeft != null) {
            motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

        if (motorRight != null) {
            motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }
        if (armMotor != null) {
            armMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        }
    }

    /**
     * set power to the left and right motors
     */
    void setDrivePower(double left, double right) {
        lPower = left;
        rPower = right;
        if (motorLeft != null) motorLeft.setPower(left);
        if (motorRight != null) motorRight.setPower(right);
    }

    /**
     * @return the encoder count, or 0 if the motor doesn't exist or an error occurs
     */
    int getEncoder(DcMotor motor) {
        int value;
        try {
            value = motor.getCurrentPosition();
        } catch (RuntimeException e) {
            value = 0;
        }
        return value;
    }

    /**
     * @return the ultrasonic level, or -1 if the sensor does not exist or an error occurs
     */
    private double getUltrasonicLevel() {
        double value;
        try {
            value = ultraSensor.getUltrasonicLevel();
        } catch (RuntimeException e) {
            value = -1;
        }
        return value;
    }



    /**
     * @return the encoder count, or 0 if the motor doesn't exist or an error occurs
     */
    double getPosition(Servo servo) {
        double value;
        try {
            value = servo.getPosition();
        } catch (RuntimeException e) {
            value = 0;
        }
        return value;
    }

    /**
     * reset the gyro heading to zero
     */
    void gyroCalibrate() {
        if (gyroSensor != null) try {
            gyroSensor.calibrate();
            hardwareErrors.remove("gyroSensor");
        } catch (Exception e) {
            hardwareErrors.put("gyroSensor", e.getMessage());
        }
    }

    /**
     * access the gyro's current heading (from initial)
     */
    int gyroHeading() {
        int value;
        try {
            value = gyroSensor.getHeading();
        } catch (RuntimeException e) {
            value = -1;
        }
        return value;
    }

    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean haveDriveEncodersReached(double leftCount, double rightCount) {
        boolean leftSuccess = Math.abs(getEncoder(motorLeft)) > leftCount;
        boolean rightSuccess = Math.abs(getEncoder(motorRight)) > rightCount;
        return leftSuccess && rightSuccess;
    }

    /**
     * Indicate whether the encoders have been completely reset.
     */
    boolean haveDriveEncodersReset() {
        // return true if both encoders are zero
        return (getEncoder(motorLeft) == 0) && (getEncoder(motorRight) == 0);
    }

    /**
     * Stops the motors and resets all the encoders & gyro
     */
    void stopAndReset() {

        // stop the motors and reset both drive wheel encoders.
        if (motorLeft != null) {
            motorLeft.setPower(0);
            motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }
        if (motorRight != null) {
            motorRight.setPower(0);
            motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }
        if (armMotor != null) {
            armMotor.setPower(0);
            armMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

        // recalibrate
        gyroCalibrate();

        // set a variable so we know to check
        resetting = true;
    }

    /**
     * @return true if the drive encoders are at zero and the gyro is done calibrating
     */
    boolean resetCompleted() {
        return haveDriveEncodersReset() && !gyroSensor.isCalibrating();
    }

    /**
     * sets the arm position to a specific value, after clipping to hardcoded ranges
     *
     * @param pos the desired position
     */
    public void setElbowPosition(double pos) {
        // clip the position values so that they never exceed their allowed range.
        elbowPosition = Range.clip(pos, ELBOW_MIN, ELBOW_MAX);

        // write position values to the arm servo
        try {
            elbowServo.setPosition(elbowPosition);
        } catch (Exception e) {
            hardwareErrors.put("elbowServo", e.getMessage());
        }

    }

    /**
     * changes the arm position by the given amount, after clipping to hardcoded ranges
     *
     * @param delta amount to change by
     */
    public void adjustElbowPosition(double delta) {
        setElbowPosition(elbowPosition + delta);
    }

    public static int clip(int number, int min, int max) {
        return number < min?min:(number > max?max:number);
    }

    /**
     * set the power of the arm motor
     * divide by two to give more control
     *
     * @param pos the desired position
     */
    public void setArmMotorPosition(int pos){
        armPosition = clip(pos, ARM_MIN, ARM_MAX);
        try {
            armMotor.setTargetPosition(pos);
            armMotor.setPower(0.05);
        } catch (Exception e) {
            hardwareErrors.put("armMotor", e.getMessage());
        }
    }

    /**
     * changes the arm position by the given amount, after clipping to hardcoded ranges
     *
     * @param delta amount to change by
     */
    public void adjustArmPosition(int delta) {
        setArmMotorPosition(armPosition + delta);
    }

    /**
     * checks the arm position and if it has met the target location, cuts power
     */
    public void checkArmPosition() {
        int currentPosition = armMotor.getCurrentPosition();
        if (currentPosition == armPosition || currentPosition < 0) {
            armMotor.setPower(0.0);
        }
    }

    /**
     * sets the bucket position to a specific value, after clipping to hardcoded ranges
     *
     * @param pos the desired position
     */
    public void setBucketPosition(double pos) {
        // clip the position values so that they never exceed their allowed range.
        bucketPosition = Range.clip(pos, BUCKET_MIN, BUCKET_MAX);

        // write position values to the bucketServo servo
        try {
            bucketServo.setPosition(bucketPosition);
        } catch (Exception e) {
            hardwareErrors.put("bucketServo", e.getMessage());
        }

    }

    /**
     * changes the bucket position by the given amount, after clipping to hardcoded ranges
     *
     * @param delta amount to change by
     */
    public void adjustBucketPosition(double delta) {
        setBucketPosition(bucketPosition + delta);
    }

    /**
     * sets the bucket position to a specific value, after clipping to hardcoded ranges
     *
     * @param pos the desired position
     */
    public void setShieldPositionL(double pos) {
        // clip the position values so that they never exceed their allowed range.
        shieldPositionL = Range.clip(pos, SHIELDS_L_MIN, SHIELDS_L_MAX);

        // write position values to the bucketServo servo
        try {
            shieldServoL.setPosition(shieldPositionL);
        } catch (Exception e) {
            hardwareErrors.put("shieldServoL", e.getMessage());
        }
    }

    /**
     * sets the bucket position to a specific value, after clipping to hardcoded ranges
     *
     * @param pos the desired position
     */
    public void setShieldPositionR(double pos) {

        // clip the position values so that they never exceed their allowed range.
        shieldPositionR = Range.clip(pos, SHIELDS_R_MIN, SHIELDS_R_MAX);

        // write position values to the bucketServo servo
        try {
            shieldServoR.setPosition(shieldPositionR);
        } catch (Exception e) {
            hardwareErrors.put("shieldServoR", e.getMessage());
        }
    }

    /**
     * changes the bucket position by the given amount, after clipping to hardcoded ranges
     *
     * @param delta amount to change by
     */
    public void adjustShieldPosition(double delta){
        setShieldPositionL(shieldPositionL + delta);
        setShieldPositionR(shieldPositionR + delta);
    }

    // return the current time minus the start time
    long timeSince(long timeSinceValue) {
        return System.currentTimeMillis() - timeSinceValue;
    }

    final static double ELBOW_MIN = 0.0;
    final static double ELBOW_MAX = 0.7;
    final static double BUCKET_MIN = 0.10;
    final static double BUCKET_MAX = 0.85;
    final static double SHIELDS_L_MIN = 0.07;
    final static double SHIELDS_L_MAX = 0.60;
    final static double SHIELDS_R_MIN = 0.07;
    final static double SHIELDS_R_MAX = 1.0;
    final static int ARM_MIN = 0;
    final static int ARM_MAX = 850;
    final static int ULTRASONIC_PORT = 4;

    // private - for use when resetting the controllers and gyros
    private boolean resetting;
}
