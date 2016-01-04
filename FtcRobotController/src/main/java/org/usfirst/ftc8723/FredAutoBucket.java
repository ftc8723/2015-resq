package org.usfirst.ftc8723;

public class FredAutoBucket extends FredHardware {

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
        setElbowPosition(0.1);
        setBucketPosition(0.5);
        setShieldPositionL(0.6);
        setShieldPositionR(0.6);
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
                // set initial positions (in case init didn't work first time... happens sometimes)
                setElbowPosition(0.1);
                setBucketPosition(0.5);
                setShieldPositionL(0.6);
                setShieldPositionR(0.6);

                // drive forward
                runUsingEncoders();
                setDrivePower(0.4f, 0.4f);
                nextStep();
                break;
            case 2:
                if (haveDriveEncodersReached(725, 725)) {
                    stopAndReset();
                    nextStep();
                }
                break;
            case 3:
                runUsingEncoders();
                setDrivePower(-0.2f, 0.2f);
                nextStep();
                break;
            case 4:
                // stop at 225 degrees heading (but don't get tricked by 0 or 1)
                if (gyroHeading() <= 225 && gyroHeading() >= 2) {
                    stopAndReset();
                    nextStep();
                }
                break;
            case 5:
                runUsingEncoders();
                setDrivePower(0.4f, 0.4f);
                nextStep();
                break;
            case 6:
                if (haveDriveEncodersReached(700, 700)) {
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
            telemetry.addData("Autonomous Complete ", String.format("%.2f sec", ((double) runtime) / 1000));
        } else {
            telemetry.addData("Autonomous Step ", step);
            telemetry.addData("Autonomous Step Time ", String.format("%.2f", ((double) timeSince(stepStart)) / 1000));
            telemetry.addData("Autonomous Time ", String.format("%.2f", ((double) timeSince(startTime)) / 1000));
        }
    }

    // constant used for end of program
    static final int STOP = Integer.MAX_VALUE;
}