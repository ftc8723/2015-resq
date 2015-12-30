package org.usfirst.ftc8723;

public class FredAuto extends FredHardware {

	// used in the loop() method to track which step of autonomous we are in and how long we have run
	private int step = 0;
	private long stepStart = 0;
	private long runtime = 0;

	/*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
	@Override
	public void init() {
		super.init();

		// assign the starting position of the wrist and bucketServo
		setElbowPosition(0.2);
		setBucketPosition(0.2);
	}


	/*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
	@Override
	public void innerLoop() {

		// todo think about what to do if someone gets in our way - how can we pause and automatically resume?

		switch (step) {
			//
			// Synchronize the state machine and hardware.
			//
			case 0:
				resetDriveEncoders();
				nextStep();
				break;
			case 1:
				// drive forward
				if (haveDriveEncodersReset() && !gyroSensor.isCalibrating()) {
					runUsingEncoders();
					setDrivePower(0.4f, 0.4f);
					nextStep();
				}
				break;
			case 2:
				if (haveDriveEncodersReached(7250, 7250)) {
					setDrivePower(0.0f, 0.0f);
					resetDriveEncoders();
					gyroCalibrate();
					//setElbowPosition(0.1);
					//setBucketPosition(0.5);
					nextStep();
				}
				break;
			case 3:
				if (haveDriveEncodersReset() && timeSince(stepStart) > 5 && !gyroSensor.isCalibrating()) {
					runUsingEncoders();
					setDrivePower(-0.1f, 0.1f);
					//setElbowPosition(0.5);
					//setBucketPosition(0.1);
					nextStep();
				}
				break;
			case 4:
				// stop at 225 degrees heading (but don't get tricked by 0 or 1
				if (gyroHeading() <= 226 && gyroHeading() >= 10) {
					setDrivePower(0.0f, 0.0f);
					resetDriveEncoders();
					nextStep(); // todo move this further down as we get things working
				}
				break;
			case 5:
				if (haveDriveEncodersReset()) {
					runUsingEncoders();
					nextStep();
				}
				step = STOP;
				break;
			case 6:
				if (haveDriveEncodersReached(2880, 2880)) { // todo change values
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					nextStep();
				}
				break;
			case 7:
				if (haveDriveEncodersReset()) {
					runUsingEncoders();
					setDrivePower(-0.5f, 0.5f);
					nextStep();
				}
				break;
			case 8:
				if (haveDriveEncodersReached(2880, 2880)) { // todo change values
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					nextStep();
				}
				break;
			case 9:
				if (haveDriveEncodersReset()) {
					runUsingEncoders();
					setDrivePower(1.0f, 1.0f);
					setElbowPosition(0.6);
					nextStep();
				}
				break;
			case 10:
				if (haveDriveEncodersReached(2880, 2880)) { //todo change values
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					nextStep();
				}
				break;
			case 11:
				if (haveDriveEncodersReset()) {
					runUsingEncoders();
					setDrivePower(-1.0f, -1.0f);
					setBucketPosition(0.5);
					nextStep();
				}
				break;
			case 12:
				if (haveDriveEncodersReached(2880, 2880)) { //todo change values
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					nextStep();
				}
				break;
			case 13:
				if (haveDriveEncodersReset()) {
					runUsingEncoders();
					setDrivePower(0.5f, -0.5f);
					setElbowPosition(0.1);
					setBucketPosition(0.5);
					nextStep();
				}
				break;
			case 14:
				if (haveDriveEncodersReached(2880, 2880)) { //todo change values
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					nextStep();
				}
				break;
			case 15:
				// wait
				if (haveDriveEncodersReset()) {
					runUsingEncoders();
					setDrivePower(-1.0f, -1.0f);
					nextStep();
				}
				break;
			case 16:
				if (haveDriveEncodersReached(2880, 2880)) { //todo change values
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					nextStep();
				}
				break;
			case 17:
				if (haveDriveEncodersReset()) {
					runUsingEncoders();
					setDrivePower(-0.5f, 0.5f);
					nextStep();
				}
				break;
			case 18:
				if (haveDriveEncodersReached(2880, 2880)) { //todo change values
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					nextStep();
				}
				break;
			case 19:
				if (haveDriveEncodersReset()) {
					runUsingEncoders();
					setDrivePower(1.0f, 1.0f);
					nextStep();
				}
				break;
			case 20:
				if (haveDriveEncodersReached(2880, 2880)) { //todo change values
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					nextStep();
				}
				break;
			case 21:
				if (haveDriveEncodersReset()) {
					nextStep();
				}
				break;
			default:
				// final state - autonomous actions have been accomplished
				step = STOP;
				if (runtime == 0) runtime = timeSince(start);
				break;
		}
	}

	private void nextStep() {
		step++;
		stepStart = System.currentTimeMillis();
	}

	@Override
	protected void updateTelemetry() {
		super.updateTelemetry();
		if (step == STOP) {
			telemetry.addData("Autonomous Complete ", String.format("%.2f sec", ((double)runtime)/1000));
		} else {
			telemetry.addData("Autonomous Step ", step);
			telemetry.addData("Autonomous Time ", String.format("%.2f", ((double) timeSince(start))/1000));
		}
	}

	// return the current time minus the start time
	private long timeSince(long timeSinceValue) {
		return System.currentTimeMillis() - timeSinceValue;
	}

	// constant used for end of program
	static final int STOP = Integer.MAX_VALUE;
}