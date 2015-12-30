package org.usfirst.ftc8723;

import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */

public class FredTeleOp extends FredHardware {

	/*
	 * Code to run when the op mode is first enabled goes here
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#init()
	 */
	@Override
	public void init() {
		super.init();

		// assign the starting position of the wrist and bucketServo
		setElbowPosition(0.01);
		setBucketPosition(1.0);
		setShieldPositionR(1.0);
		setShieldPositionL(1.0);
	}

	public void start() {
		super.start();
		runUsingEncoders();
	}

	/*
	 * This method will be called repeatedly in a loop
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void innerLoop() {

		 // gamepad1 controls the motors via the left stick, and the
		 // bucketServo via the a,b, x, y buttons

		// throttle: left_stick_y ranges from -1 (full up) to 1 (full down)
		// direction: left_stick_x ranges from -1 (full left) to 1 (full right)
		float throttle = -gamepad1.left_stick_y;
		float direction = gamepad1.left_stick_x;
		double left = throttle - direction;
		double right = throttle + direction;

		// clip the right/left values so that the values never exceed +/- 1
		left = Range.clip(left, -1, 1);
		right = Range.clip(right, -1, 1);

		// scale the joystick value to make it easier to control
		// the robot more precisely at slower speeds.
		left = scaleInput(left);
		right =  scaleInput(right);

		// write the values to the motors
		setDrivePower(right, left);

		// check gamepad buttons and adjust servos accordingly
		if (gamepad1.a) {
			// if A is pushed, increase the position of the arm servo
			adjustElbowPosition(ELBOW_DELTA);
		}
		if (gamepad1.y) {
			// if Y is pushed, decrease the position of the arm servo
			adjustElbowPosition(-ELBOW_DELTA);
		}
		if (gamepad1.x) {
			// if X is pushed, increase the position of the bucket servo
			adjustBucketPosition(BUCKET_DELTA);
		}
		if (gamepad1.b) {
			// if B is pushed, decrease the position of the bucket servo
			adjustBucketPosition(-BUCKET_DELTA);
		}
		if (gamepad1.right_bumper && !rightBumperPressed){
			// if we pressed the right bumper and it wasn't pressed last time around
			shieldDirection = -shieldDirection;
		}
		rightBumperPressed = gamepad1.right_bumper;
		if (gamepad1.right_bumper){
			if (shieldDirection < 0) {
				adjustShieldPosition(-SHIELD_DELTA);
			}
			else {
				adjustShieldPosition(SHIELD_DELTA);
			}
		}
		if (gamepad1.right_trigger > 0) {
			adjustArmPosition(ARM_DELTA);
		}
		else if (gamepad1.left_trigger > 0) {
			adjustArmPosition(-ARM_DELTA);
		}
		else {
			adjustArmPosition(0);
		}
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

		// get value from the array
		double dScale = scaleArray[index];

		// flip the sign if input was negative
		if (dVal < 0) dScale = -dScale;

		// return scaled value.
		return dScale;
	}

	// amounts to change the elbowServo and bucket servo positions by
	final static double ELBOW_DELTA = 0.002;
	final static double BUCKET_DELTA = 0.002;
	final static double SHIELD_DELTA = 0.002;
	final static int ARM_DELTA = 4;
	private int shieldDirection = -1;
	private boolean rightBumperPressed = false;

}
