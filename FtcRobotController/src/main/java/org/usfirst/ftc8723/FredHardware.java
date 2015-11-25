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
import java.util.Iterator;
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
    private double lPower;
    private double rPower;

    // hardware
    DcMotor motorLeft;
    DcMotor motorRight;
    DcMotor newMotorLeft;
    DcMotor newMotorRight;
    Servo bucketServo;
    Servo armServo;
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

        try {
            motorLeft = hardwareMap.dcMotor.get("motorLeft");
            if (motorLeft == null) {
                hardwareErrors.put("motorLeft", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("motorLeft", e.getMessage());
        }

        try {
            motorRight = hardwareMap.dcMotor.get("motorRight");
            if (motorRight == null) {
                hardwareErrors.put("motorRight", "not found");
            } else {
                motorRight.setDirection(DcMotor.Direction.REVERSE);
            }
        } catch (Exception e) {
            hardwareErrors.put("motorRight", e.getMessage());
        }

        try {
            newMotorLeft = hardwareMap.dcMotor.get("newMotorLeft");
            if (newMotorLeft == null) {
                hardwareErrors.put("newMotorLeft", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("newMotorLeft", e.getMessage());
        }

        try {
            newMotorRight = hardwareMap.dcMotor.get("newMotorRight");
            if (newMotorRight == null) {
                hardwareErrors.put("newMotorRight", "not found");
            } else {
                newMotorRight.setDirection(DcMotor.Direction.REVERSE);
            }
        } catch (Exception e) {
            hardwareErrors.put("newMotorRight", e.getMessage());
        }

        try {
            bucketServo = hardwareMap.servo.get("bucket servo");
            if (bucketServo == null) {
                hardwareErrors.put("bucketServo", "not found");
            } else {
                bucketServo.setDirection(Servo.Direction.FORWARD);
            }

        } catch (Exception e) {
            hardwareErrors.put("bucketServo", e.getMessage());
        }

        try {
            armServo = hardwareMap.servo.get("arm servo");
            if (armServo == null) {
                hardwareErrors.put("armServo", "not found");
            } else {
                armServo.setDirection(Servo.Direction.FORWARD);
            }

        } catch (Exception e) {
            hardwareErrors.put("armServo", e.getMessage());
        }

        try {
            // legacy module port 5
            colorSensor = hardwareMap.colorSensor.get("color sensor");
            if (colorSensor == null) {
                hardwareErrors.put("colorSensor", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("colorSensor", e.getMessage());
        }

        try {
            // legacy module port 4
            lightSensor = hardwareMap.lightSensor.get("light sensor");
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
                // reset the gyro to zero
                gyroSensor.calibrate();
            }

        } catch (Exception e) {
            hardwareErrors.put("gyroSensor", e.getMessage());
        }



        // try this
        DcMotorController motorController = hardwareMap.dcMotorController.get("Motor Controller 1");
        motorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_WRITE);
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
     * send standard telemetry data back to driver station.  subclasses can override this to add
     * their own telemetry
     */
    protected void updateTelemetry() {
        updateTelemetry("colorSensor", String.format("rgb: %d,%d,%d", colorSensor.red(), colorSensor.green(), +colorSensor.blue()));
        updateTelemetry("lightSensor", String.format("%.2f", lightSensor.getLightDetected()));

        updateTelemetry("motorLeft", String.format("pwr: %.2f pos: %d", lPower, leftEncoder()));
        updateTelemetry("motorRight", String.format("pwr: %.2f pos: %d", rPower, rightEncoder()));

        updateTelemetry("armServo", String.format("%.2f", armPosition));
        updateTelemetry("bucketServo", String.format("%.2f", bucketPosition));

        updateTelemetry("gyroSensor", String.format("rotation: %.2f heading: %d", gyroRotation(), gyroHeading()));
    }

    private void updateTelemetry(String key, String value) {
        String error = hardwareErrors.get(key);
        if (error != null) {
            telemetry.addData(key,error);
        } else {
            telemetry.addData(key,value);
        }
    }

    /**
     * Reset both drive wheel encoders.
     */
    public void resetDriveEncoders() {
        // perform the action on both motors.
        if (newMotorLeft != null) {
            newMotorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

        if (newMotorRight != null) {
            newMotorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }
    }

    /**
     * Set both drive wheel encoders to run
     */
    public void runUsingEncoders() {
        // perform the action on both motors.
        if (newMotorLeft != null) {
            newMotorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

        if (newMotorRight != null) {
            newMotorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
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
            value = newMotorLeft.getCurrentPosition();
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
            value = newMotorRight.getCurrentPosition();
        } catch (RuntimeException e) {
            value = 0;
        }
        return value;
    }

    /**
     * access the gyro's current rate of rotation
     */
    double gyroRotation() {
        double value;
        try {
            value = gyroSensor.getRotation();
        } catch (RuntimeException e) {
            value = -1.0;
        }
        return value;
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
        return value;    }

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
     * @param pos the desired position
     */
    public void setArmPosition(double pos) {
        // clip the position values so that they never exceed their allowed range.
        armPosition = Range.clip(pos, ARM_MIN, ARM_MAX);

        // write position values to the wrist and bucketServo servo
        armServo.setPosition(armPosition);
    }

    /**
     * changes the arm position by the given amount, after clipping to hardcoded ranges
     * @param delta amount to change by
     */
    public void adjustArmPosition(double delta) {
        setArmPosition(armPosition+delta);
    }

    /**
     * sets the bucket position to a specific value, after clipping to hardcoded ranges
     * @param pos the desired position
     */
    public void setBucketPosition(double pos) {
        // clip the position values so that they never exceed their allowed range.
        bucketPosition = Range.clip(pos, BUCKET_MIN, BUCKET_MAX);

        // write position values to the wrist and bucketServo servo
        bucketServo.setPosition(bucketPosition);
    }

    /**
     * changes the bucket position by the given amount, after clipping to hardcoded ranges
     * @param delta amount to change by
     */
    public void adjustBucketPosition(double delta) {
        setBucketPosition(bucketPosition + delta);
    }

    final static double ARM_MIN = 0.10;
    final static double ARM_MAX = 0.90;
    final static double BUCKET_MIN = 0.20;
    final static double BUCKET_MAX = 0.99;
}
