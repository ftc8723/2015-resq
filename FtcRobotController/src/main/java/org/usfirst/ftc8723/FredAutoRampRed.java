package org.usfirst.ftc8723;

public class FredAutoRampRed extends FredHardware {

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
				stopAndReset();
				nextStep();
				break;
			case 1:
				// drive forward
				if (resetCompleted()) {
					runUsingEncoders();
					setDrivePower(0.4f, 0.4f);
					nextStep();
				}
				break;
			case 2:
				if (haveDriveEncodersReached(7250, 7250)) {
					stopAndReset();
					nextStep();
				}
				break;
			case 3:
				if (resetCompleted()) {
					runUsingEncoders();
					setDrivePower(-0.2f, 0.2f);
					nextStep();
				}
				break;
			case 4:
				// stop at 225 degrees heading (but don't get tricked by 0 or 1
				if (gyroHeading() <= 226 && gyroHeading() >= 10) {
					stopAndReset();
					nextStep(); // todo move this further down as we get things working
				}
				break;
			case 5:
				if (resetCompleted()) {
					runUsingEncoders();
					setDrivePower(0.4f, 0.4f);
					nextStep();
				}
				break;
			case 6:
				if (haveDriveEncodersReached(7000, 7000)){
					stopAndReset();
					nextStep();
				}
				break;
			default:
				// final state - autonomous actions have been accomplished
				step = STOP;
				if (runtime == 0) runtime = timeSince(startTime);
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
			telemetry.addData("Autonomous Time ", String.format("%.2f", ((double) timeSince(startTime))/1000));
		}
	}

	// constant used for end of program
	static final int STOP = Integer.MAX_VALUE;
}