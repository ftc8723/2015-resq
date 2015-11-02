package org.usfirst.ftc8723;

import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class FredTeleOp extends FredHardware {

	// amount to change the armServo and servo positions by
	double armDelta = 0.05;
	double bucketDelta = 0.05;

	/*
	 * Code to run when the op mode is first enabled goes here
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {
		super.init();

        // try this
        // DcMotorController motorController = hardwareMap.dcMotorController.get("motorController");
        // motorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_WRITE);

		// assign the starting position of the wrist and bucketServo
		armPosition = 0.5;
		bucketPosition = 0.5;
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void innerLoop() {

		/*
		 * Gamepad 1
		 * 
		 * Gamepad 1 controls the motors via the left stick, and it controls the
		 * wrist/bucketServo via the a,b, x, y buttons
		 */

		// throttle: left_stick_y ranges from -1 to 1, where -1 is full up, and
		// 1 is full down
		// direction: left_stick_x ranges from -1 to 1, where -1 is full left
		// and 1 is full right
		float throttle = -gamepad1.left_stick_y;
		float direction = gamepad1.left_stick_x;
		rPower = throttle - direction;
		lPower = throttle + direction;

		// clip the right/left values so that the values never exceed +/- 1
		rPower = Range.clip(rPower, -1, 1);
		lPower = Range.clip(lPower, -1, 1);

		// scale the joystick value to make it easier to control
		// the robot more precisely at slower speeds.
		rPower = (float)scaleInput(rPower);
		lPower =  (float)scaleInput(lPower);

		// write the values to the motors
		motorRight.setPower(rPower);
		motorLeft.setPower(lPower);

		// update the position of the armServo.
		if (gamepad1.a) {
			// if the A button is pushed on gamepad1, increment the position of
			// the armServo servo.
			armPosition += armDelta;
		}

		if (gamepad1.y) {
			// if the Y button is pushed on gamepad1, decrease the position of
			// the armServo servo.
			armPosition -= armDelta;
		}

		if (gamepad1.left_bumper) {
			colorSensor.enableLed(true);
		} else {
			colorSensor.enableLed(false);

		}

		// update the position of the bucketServo
		if (gamepad1.x) {
			bucketPosition += bucketDelta;
		}

		if (gamepad1.b) {
			bucketPosition -= bucketDelta;
		}

		// write position values to the wrist and bucketServo servo
		setArmPositionAndWait(armPosition,1000);
		setBucketPosition(bucketPosition);
	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

    	
	/*
	 * This method scales the joystick input so for low joystick values, the 
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };
		
		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);
		
		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale = 0.0;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}

}
