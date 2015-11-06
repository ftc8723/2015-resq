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

    // power to the wheels
    float rPower, lPower;

    // hardware
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
            armServo.setDirection(Servo.Direction.FORWARD);
            if (armServo == null) {
                hardwareErrors.put("error armServo", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("error armServo", e.getMessage());
        }

        try {
            // servo 2
            bucketServo = hardwareMap.servo.get("bucket servo");
            bucketServo.setDirection(Servo.Direction.FORWARD);
            if (bucketServo == null) {
                hardwareErrors.put("error bucketServo", "not found");
            }

        } catch (Exception e) {
            hardwareErrors.put("error bucketServo", e.getMessage());
        }

        if (hardwareErrors.isEmpty()) hardwareErrors.put("errors", "none");

        // try this
        // DcMotorController motorController = hardwareMap.dcMotorController.get("motorController");
        // motorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_WRITE);
    }

    @Override
    public void loop() {
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

        for (Iterator<String> it = hardwareErrors.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String error = hardwareErrors.get(key);
            telemetry.addData(key, error);
        }

    }

    public abstract void innerLoop();

    /**
     * Reset the left drive wheel encoder.
     */
    public void reset_left_drive_encoder()

    {
        if (motorLeft != null) {
            motorLeft.setChannelMode
                    (DcMotorController.RunMode.RESET_ENCODERS
                    );
        }

    } // reset_left_drive_encoder

    /**
     * Reset the right drive wheel encoder.
     */
    public void reset_right_drive_encoder()

    {
        if (motorRight != null) {
            motorRight.setChannelMode
                    (DcMotorController.RunMode.RESET_ENCODERS
                    );
        }

    } // reset_right_drive_encoder

    /**
     * Reset both drive wheel encoders.
     */
    public void reset_drive_encoders()

    {
        //
        // Reset the motor encoders on the drive wheels.
        //
        reset_left_drive_encoder();
        reset_right_drive_encoder();

    } // reset_drive_encoders

    public void run_using_left_drive_encoder()
    {
        if (motorLeft != null) {
            motorLeft.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

    }

    /**
     * Set the right drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_using_right_drive_encoder()
    {
        if (motorRight != null) {
            motorRight.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

    }

    /**
     * Set both drive wheel encoders to run, if the mode is appropriate.
     */
    public void run_using_encoders()

    {
        //
        // Call other members to perform the action on both motors.
        //
        run_using_left_drive_encoder();
        run_using_right_drive_encoder();

    } // run_using_encoders

    /**
     * Scale the joystick input using a nonlinear algorithm.
     */
    void set_drive_power(double p_left_power, double p_right_power)

    {
        if (motorLeft != null) {
            motorLeft.setPower(p_left_power);
        }
        if (motorRight != null) {
            motorRight.setPower(p_right_power);
        }

    } // set_drive_power

    /**
     * Indicate whether the left drive motor's encoder has reached a value.
     */
    boolean has_left_drive_encoder_reached(double p_count)

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        if (motorLeft != null) {
            //
            // Has the encoder reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs(motorLeft.getCurrentPosition()) > p_count) {
                //
                // Set the status to a positive indication.
                //
                l_return = true;
            }
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_left_drive_encoder_reached

    /**
     * Indicate whether the right drive motor's encoder has reached a value.
     */
    boolean has_right_drive_encoder_reached(double p_count)

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        if (motorRight != null) {
            //
            // Have the encoders reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs(motorRight.getCurrentPosition()) > p_count) {
                //
                // Set the status to a positive indication.
                //
                l_return = true;
            }
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_right_drive_encoder_reached

    /**
     * Access the left encoder's count.
     */
    int a_left_encoder_count() {
        int l_return = 0;

        if (motorLeft != null) {
            l_return = motorLeft.getCurrentPosition();
        }

        return l_return;

    } // a_left_encoder_count

    /**
     * Access the right encoder's count.
     */
    int a_right_encoder_count()

    {
        int l_return = 0;

        if (motorRight != null) {
            l_return = motorRight.getCurrentPosition();
        }

        return l_return;

    } // a_right_encoder_count

    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean have_drive_encoders_reached(double p_left_count, double p_right_count) {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Have the encoders reached the specified values?
        //
        if (has_left_drive_encoder_reached(p_left_count) &&
                has_right_drive_encoder_reached(p_right_count)) {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // have_encoders_reached

    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_left_drive_encoder_reset() {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Has the left encoder reached zero?
        //
        if (a_left_encoder_count() == 0) {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_left_drive_encoder_reset

    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_right_drive_encoder_reset() {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Has the right encoder reached zero?
        //
        if (a_right_encoder_count() == 0) {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_right_drive_encoder_reset

    /**
     * Indicate whether the encoders have been completely reset.
     */
    boolean have_drive_encoders_reset() {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Have the encoders reached zero?
        //
        if (has_left_drive_encoder_reset() && has_right_drive_encoder_reset()) {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // have_drive_encoders_reset

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
