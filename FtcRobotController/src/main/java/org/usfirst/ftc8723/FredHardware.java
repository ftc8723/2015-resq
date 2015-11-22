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

    // position of the servos.
    double armPosition, bucketPosition;

    // intended power to the wheels
    float rPower, lPower;

    // hardware
    DcMotor motorLeft;
    DcMotor motorRight;
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
        // DcMotorController motorController = hardwareMap.dcMotorController.get("motorController");
        // motorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_WRITE);
    }

    /**
     * FredAuto and FredTeleOp should override this to do the normal loop functions
     */
    public abstract void innerLoop();

    @Override
    public void loop() {
        // perform the normal loop functions inside a try/catch to improve error handling
        try {
            innerLoop();

            // Send telemetry data back to driver station. Note that if we are using
		    // a legacy NXT-compatible motor controller, then the getPower() method
		    // will return a null value. The legacy NXT-compatible motor controllers
            // are currently write only.
            telemetry.addData("color", "" + colorSensor.red() + "," + colorSensor.green() + "," + colorSensor.blue());
            telemetry.addData("light", "" + lightSensor.getLightDetected());

            // try to enable READ_WRITE above to add telemetry for motorLeft.getCurrentPosition()
            telemetry.addData("motor-L", "pwr: " + String.format("%.2f", lPower));// + "," + motorLeft.getCurrentPosition());
            telemetry.addData("motor-R", "pwr: " + String.format("%.2f", rPower));//+ "," + motorRight.getCurrentPosition());

            telemetry.addData("armPosition.set", armPosition);
            telemetry.addData("bucketPosition.set", bucketPosition);
            telemetry.addData("armPosition.get", armServo.getPosition());
            telemetry.addData("bucketPosition.get", bucketServo.getPosition());

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
     * Reset both drive wheel encoders.
     */
    public void resetDriveEncoders() {
        //
        // Reset the motor encoders on the drive wheels.
        //
        if (motorLeft != null) {
            motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

        if (motorRight != null) {
            motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }
    }

    /**
     * Set both drive wheel encoders to run, if the mode is appropriate.
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
    void setDrivePower(double p_left_power, double p_right_power)

    {
        if (motorLeft != null) {
            motorLeft.setPower(p_left_power);
        }
        if (motorRight != null) {
            motorRight.setPower(p_right_power);
        }

    }

    /**
     * Access the left encoder's count.
     */
    int leftEncoder() {
        int value = 0;

        if (motorLeft != null) {
            value = motorLeft.getCurrentPosition();
        }

        return value;
    }

    /**
     * Access the right encoder's count.
     */
    int rightEncoder() {
        int value = 0;

        if (motorRight != null) {
            value = motorRight.getCurrentPosition();
        }

        return value;
    }

    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean haveDriveEncodersReached(double leftCount, double rightCount) {
        boolean leftSuccess = Math.abs(leftEncoder()) > leftCount;
        boolean rightSuccess = Math.abs(leftEncoder()) > rightCount;
        return leftSuccess && rightSuccess;
    }

    /**
     * Indicate whether the encoders have been completely reset.
     */
    boolean haveDriveEncodersReset() {
        // return true if both encoders are zero
        return (leftEncoder() == 0) && (rightEncoder() == 0);
    }

    final static double ARM_MIN_RANGE = 0.20;
    final static double ARM_MAX_RANGE = 0.90;

    public void setArmPosition(double pos) {
        // clip the position values so that they never exceed their allowed range.
        armPosition = Range.clip(pos, ARM_MIN_RANGE, ARM_MAX_RANGE);

        // write position values to the wrist and bucketServo servo
        armServo.setPosition(armPosition);
    }

    public void setArmPositionAndWait(double pos, int waitMS) {
        long start = System.currentTimeMillis();
        setArmPosition(pos);
        while (System.currentTimeMillis() > start + waitMS) {
            try {
                Thread.sleep(waitMS / 10);
            } catch (InterruptedException e) {
                // ignore
            }
            if (armServo.getPosition() == pos) {
                break;
            }
        }
    }

    final static double BUCKET_MIN_RANGE = 0.20;
    final static double BUCKET_MAX_RANGE = 0.7;

    public void setBucketPosition(double pos) {
        // clip the position values so that they never exceed their allowed range.
        bucketPosition = Range.clip(pos, BUCKET_MIN_RANGE, BUCKET_MAX_RANGE);

        // write position values to the wrist and bucketServo servo
        bucketServo.setPosition(bucketPosition);
    }
}
