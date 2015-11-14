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
    int numOpLoops;
    int motorLeftPos;
    int motorRightPos;

    // intended power to the wheels
    float rPower, lPower;

    // hardware
    DcMotorController motorController;
    DcMotor motorRight;
    DcMotor motorLeft;
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
            motorController = hardwareMap.dcMotorController.get("motorController");
            if (motorController == null) {
                hardwareErrors.put("error motorController", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("error motorLeft", e.getMessage());
        }

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
            if (numOpLoops % 17 == 0){
                motorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
            }

            // Every 17 loops, switch to read mode so we can read data from the NXT device.
            // Only necessary on NXT devices.
            if (motorController.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY) {

                // Update the reads after some loops, when the command has successfully propagated through.
                motorLeftPos = motorLeft.getCurrentPosition();
                motorRightPos = motorRight.getCurrentPosition();
                //telemetry.addData("left motor", motorLeft.getPower());
                //telemetry.addData("right motor", motorRight.getPower());
                telemetry.addData("RunMode: ", motorLeft.getMode().toString());

                // Only needed on Nxt devices, but not on USB devices
                motorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);

                // Reset the loop
                numOpLoops = 0;
            }
            numOpLoops++;
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
     * Reset the left drive wheel encoder.
     */
    public void reset_left_drive_encoder()

    {
        if (motorLeft != null) {
            motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

    }

    /**
     * Reset the right drive wheel encoder.
     */
    public void reset_right_drive_encoder()

    {
        if (motorRight != null) {
            motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

    }

    /**
     * Reset both drive wheel encoders.
     */
    public boolean reset_drive_encoders() {

        // Reset the motor encoders on the drive wheels.
        if (motorController.getMotorControllerDeviceMode()== DcMotorController.DeviceMode.WRITE_ONLY) {
            reset_left_drive_encoder();
            reset_right_drive_encoder();
            return true;
        }
        else {
            return false;
        }
    }

    public void run_using_left_drive_encoder()
    {
        if (motorLeft != null) {
            motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

    }

    /**
     * Set the right drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_using_right_drive_encoder()
    {
        if (motorRight != null) {
            motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

    }

    /**
     * Set both drive wheel encoders to run, if the mode is appropriate.
     */
    public void run_using_encoders()

    {
        // perform the action on both motors.
        run_using_left_drive_encoder();
        run_using_right_drive_encoder();

    }

    /**
     * Scale the joystick input using a nonlinear algorithm.
     */
    void set_drive_power(double p_left_power, double p_right_power) {

        if (motorLeft != null) {
            motorLeft.setPower(p_left_power);
        }
        if (motorRight != null) {
            motorRight.setPower(p_right_power);
        }

    } // set_drive_power

    /**
     * Indicate whether the right drive motor's encoder has reached a value.
     */
    boolean has_right_drive_encoder_reached(double p_count)

    {
        boolean success = false;

        if (motorRight != null) {
            //
            // Have the encoders reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs(motorRightPos) > p_count) {
                success = true;
            }
        }

        return success;

    }

    /**
     * Indicate whether the left drive motor's encoder has reached a value.
     */
    boolean has_left_drive_encoder_reached(double p_count)

    {
        boolean success = false;

        if (motorLeft != null) {
            //
            // Has the encoder reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs(motorLeftPos) > p_count) {
                success = true;
            }
        }

        return success;

    }

    /**
     * Access the left encoder's count.
     */
    int leftEncoder() {
        int value = 0;

        if (motorLeft != null) {
            value = motorLeftPos;
        }

        return value;

    }

    /**
     * Access the right encoder's count.
     */
    int rightEncoder()

    {
        int value = 0;

        if (motorRight != null) {
            value = motorRightPos;
        }

        return value;

    }

    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean have_drive_encoders_reached(double leftCount, double rightCount) {

        return has_left_drive_encoder_reached(leftCount) && has_right_drive_encoder_reached(rightCount);

    }

    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_left_drive_encoder_reset() {
        boolean success = false; // assume failure

        // Has the left encoder reached zero?
        if (leftEncoder() == 0) {
            success = true;
        }

        return success;

    }

    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_right_drive_encoder_reset() {
        boolean success = false;  // Assume failure

        // Has the right encoder reached zero?
        if (rightEncoder() == 0) {
            success = true;
        }

        return success;

    }

    /**
     * Indicate whether the encoders have been completely reset.
     */
    boolean have_drive_encoders_reset() {
        return has_left_drive_encoder_reset() && has_right_drive_encoder_reset();

    }

    final static double ARM_MIN_RANGE = 0.10;
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

    public void driveForTime (double lspeed, double rspeed, int waitMS) {
        set_drive_power(lspeed, rspeed);
        long start = System.currentTimeMillis();
        if (System.currentTimeMillis() <= start + waitMS){
            set_drive_power(0f, 0f);
        }
    }

    final static double BUCKET_MIN_RANGE = 0.20;
    final static double BUCKET_MAX_RANGE = 0.99;

    public void setBucketPosition(double pos) {
        // clip the position values so that they never exceed their allowed range.
        bucketPosition = Range.clip(pos, BUCKET_MIN_RANGE, BUCKET_MAX_RANGE);

        // write position values to the wrist and bucketServo servo
        bucketServo.setPosition(bucketPosition);
    }
}
