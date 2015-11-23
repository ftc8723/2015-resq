package org.usfirst.ftc8723;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
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
                hardwareErrors.put("error motorLeft", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("error motorLeft", e.getMessage());
        }

        try {
            motorRight = hardwareMap.dcMotor.get("motorRight");
            if (motorRight == null) {
                hardwareErrors.put("error motorRight", "not found");
            } else {
                motorRight.setDirection(DcMotor.Direction.REVERSE);
            }
        } catch (Exception e) {
            hardwareErrors.put("error motorRight", e.getMessage());
        }

        try {
            newMotorLeft = hardwareMap.dcMotor.get("newMotorLeft");
            if (newMotorLeft == null) {
                hardwareErrors.put("error newMotorLeft", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("error newMotorLeft", e.getMessage());
        }

        try {
            newMotorRight = hardwareMap.dcMotor.get("newMotorRight");
            if (newMotorRight == null) {
                hardwareErrors.put("error newMotorRight", "not found");
            } else {
                newMotorRight.setDirection(DcMotor.Direction.REVERSE);
            }
        } catch (Exception e) {
            hardwareErrors.put("error newMotorRight", e.getMessage());
        }

        try {
            // sensor 5
            colorSensor = hardwareMap.colorSensor.get("color sensor");
            if (colorSensor == null) {
                hardwareErrors.put("error colorSensor", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("error colorSensor", e.getMessage());
        }

        try {
            // sensor 4
            lightSensor = hardwareMap.lightSensor.get("light sensor");
            if (lightSensor == null) {
                hardwareErrors.put("error lightSensor", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("error lightSensor", e.getMessage());
        }

        try {
            // servo 1
            armServo = hardwareMap.servo.get("arm servo");
            if (armServo == null) {
                hardwareErrors.put("error armServo", "not found");
            } else {
                armServo.setDirection(Servo.Direction.FORWARD);
            }

        } catch (Exception e) {
            hardwareErrors.put("error armServo", e.getMessage());
        }

        try {
            // servo 2
            bucketServo = hardwareMap.servo.get("bucket servo");
            if (bucketServo == null) {
                hardwareErrors.put("error bucketServo", "not found");
            } else {
                bucketServo.setDirection(Servo.Direction.FORWARD);
            }

        } catch (Exception e) {
            hardwareErrors.put("error bucketServo", e.getMessage());
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

        // todo - we may not need to do this every time... check to see if we add telemetry directly in init
        for (Iterator<String> it = hardwareErrors.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String error = hardwareErrors.get(key);
            telemetry.addData(key, error);
        }

    }

    /**
     * send standard telemetry data back to driver station.  subclasses can override this to add
     * their own telemetry
     */
    protected void updateTelemetry() {
        telemetry.addData("color", "" + colorSensor.red() + "," + colorSensor.green() + "," + colorSensor.blue());
        telemetry.addData("light", "" + lightSensor.getLightDetected());

        telemetry.addData("motor-L", "pwr: " + String.format("pwr: %.2f pos: %d", lPower, leftEncoder()));
        telemetry.addData("motor-R", "pwr: " + String.format("pwr: %.2f pos: %d", rPower, rightEncoder()));

        telemetry.addData("armPosition.set/get", String.format("%.2f,%.2f", armPosition, armServo.getPosition()));
        telemetry.addData("bucketPosition.set/get", String.format("%.2f,%.2f", bucketPosition, bucketServo.getPosition()));
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
     * Scale the joystick input using a nonlinear algorithm.
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
