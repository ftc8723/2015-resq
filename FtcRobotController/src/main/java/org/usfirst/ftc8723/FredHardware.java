package org.usfirst.ftc8723;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.HashMap;
import java.util.Map;

public abstract class FredHardware extends OpMode {
    /*
     * Note: the configuration of the servos is such that
     * as the armServo servo approaches 0, the armServo position moves up (away from the floor).
     * Also, as the bucketServo servo approaches 0, the bucketServo opens up (drops the game element).
     */

    // position of the servos & power to the wheels
    private double armPosition;
    private double bucketPosition;
    private double shieldPositionL;
    private double shieldPositionR;
    private double lPower;
    private double rPower;
    private double armPower;

    // timer
    long start;

    // hardware
    DcMotor motorLeft;
    DcMotor motorRight;
    DcMotor armMotor;
    Servo bucketServo;
    Servo armServo;
    Servo shieldServoL;
    Servo shieldServoR;
    ColorSensor colorSensor;
    LightSensor lightSensor;
    GyroSensor gyroSensor;

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

        resetDriveEncoders();

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
            armServo = hardwareMap.servo.get("armServo");
            if (armServo == null) {
                hardwareErrors.put("armServo", "not found");
            } else {
                armServo.setDirection(Servo.Direction.FORWARD);
            }

        } catch (Exception e) {
            hardwareErrors.put("armServo", e.getMessage());
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
    }

    /**
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void start() {
        start = System.currentTimeMillis();
        long elapsed = 0;
        while (gyroSensor.isCalibrating() && gyroHeading() != 0 && elapsed < 200) {
            // wait up to 3 seconds to calibrate ... should NEVER take this long
            elapsed = System.currentTimeMillis() - start;
            if (elapsed > 3000) {
                hardwareErrors.put("gyroSensor", "calibration timeout after " + elapsed + "ms");
            }
        }
    }


    /**
     * FredAuto and FredTeleOp should override this to do the normal loop functions
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
            // perform the normal loop functions inside a try/catch to improve error handling
            innerLoop();

            // log the standard telemetry variables
            updateTelemetry();

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

        updateTelemetry("motorLeft", String.format("pwr: %.2f pos: %d", lPower, leftEncoder()));
        updateTelemetry("motorRight", String.format("pwr: %.2f pos: %d", rPower, rightEncoder()));
        updateTelemetry("armMotor", String.format("pwr: %.2f pos: %d", armPower, armEncoder()));

        updateTelemetry("armServo", String.format("%.2f / %.2f", armPosition, armServo.getPosition()));
        updateTelemetry("bucketServo", String.format("%.2f / %.2f", bucketPosition, bucketServo.getPosition()));
        updateTelemetry("shieldServoL", String.format("%.2f / %.2f", shieldPositionL, shieldServoL.getPosition()));
        updateTelemetry("shieldServoR", String.format("%.2f / %.2f", shieldPositionR, shieldServoR.getPosition()));

        updateTelemetry("gyroSensor", String.format("heading: %d", gyroHeading()));
        updateTelemetry("hardwareErrors", hardwareErrors.toString());
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
     * Reset both drive wheel encoders.
     */
    public void resetDriveEncoders() {
        // perform the action on both motors.
        if (motorLeft != null) {
            motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

        if (motorRight != null) {
            motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

        if (armMotor != null) {
            armMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
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
     * Access the left encoder's count.
     */
    int leftEncoder() {
        int value;
        try {
            value = motorLeft.getCurrentPosition();
        } catch (RuntimeException e) {
            value = 0;
        }
        return value;
    }

    /**
     * Access the right encoder's count.
     */
    int rightEncoder() {
        int value;
        try {
            value = motorRight.getCurrentPosition();
        } catch (RuntimeException e) {
            value = 0;
        }
        return value;
    }

    /**
     * Access the arm encoder's count.
     */
    int armEncoder() {
        int value;
        try {
            value = armMotor.getCurrentPosition();
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
     * @return the number of milliseconds since the start() method was called
     */
    long timeSinceStart() {
        return System.currentTimeMillis() - start;
    }

    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean haveDriveEncodersReached(double leftCount, double rightCount) {
        boolean leftSuccess = Math.abs(leftEncoder()) > leftCount;
        boolean rightSuccess = Math.abs(rightEncoder()) > rightCount;
        return leftSuccess && rightSuccess;
    }

    /**
     * Indicate whether the encoders have been completely reset.
     */
    boolean haveDriveEncodersReset() {
        // return true if both encoders are zero
        return (leftEncoder() == 0) && (rightEncoder() == 0);
    }

    /**
     * sets the arm position to a specific value, after clipping to hardcoded ranges
     *
     * @param pos the desired position
     */
    public void setElbowPosition(double pos) {
        // clip the position values so that they never exceed their allowed range.
        armPosition = Range.clip(pos, ARM_MIN, ARM_MAX);

        // write position values to the arm servo
        try {
            armServo.setPosition(armPosition);
        } catch (Exception e) {
            hardwareErrors.put("armServo", e.getMessage());
        }

    }

    /**
     * changes the arm position by the given amount, after clipping to hardcoded ranges
     *
     * @param delta amount to change by
     */
    public void adjustElbowPosition(double delta) {
        setElbowPosition(armPosition + delta);
    }

    /**
     * set the power of the arm motor
     * divide by two to give more control
     *
     * @param power the desired power
     */
    public void setArmMotorPower(double power){
        armPower = power;
        try {
            armMotor.setPower(power);
        } catch (Exception e) {
            hardwareErrors.put("armMotorPower", e.getMessage());
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

    final static double ARM_MIN = 0.01;
    final static double ARM_MAX = 0.75;
    final static double BUCKET_MIN = 0.20;
    final static double BUCKET_MAX = 0.99;
    final static double SHIELDS_L_MIN = 0.0;
    final static double SHIELDS_L_MAX = 0.7;
    final static double SHIELDS_R_MIN = 0.0;
    final static double SHIELDS_R_MAX = 1.0;
}
