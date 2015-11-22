package org.usfirst.ftc8723;

public class FredAuto extends FredHardware {

    // used in the loop() method to track which step of autonomous we are in
    int step = 0;

    /*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init() {
		super.init();

        // assign the starting position of the wrist and bucketServo
        setArmPosition(0.2);
        setBucketPosition(0.2);
    }


    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
	@Override
    public void innerLoop () {

        // todo think about what to do if someone gets in our way - how can we pause and automatically resume?

		switch (step)
		{
			//
			// Synchronize the state machine and hardware.
			//
			case 0:
				resetDriveEncoders();
				step++;
				break;
			case 1:
				runUsingEncoders();

				setDrivePower(1.0f, 1.0f);

				if (haveDriveEncodersReached(2880, 2880))//change values
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
			case 2:
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;
			case 3:
				runUsingEncoders();
				setDrivePower(-0.5f, 0.5f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
			case 4:
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;
			case 5:
				runUsingEncoders();
				setDrivePower(1.0f, 1.0f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
            case 6:
                // wait
                if (haveDriveEncodersReset())
                {
                    step++;
                }
                break;
            case 7:
				runUsingEncoders();
				setDrivePower(-0.5f, 0.5f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
            case 8:
				// wait
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;
			case 9:
				setArmPosition(0.6);
				step++;
				break;
			case 10:
				runUsingEncoders();
				setDrivePower(1.0f, 1.0f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
			case 11:
				// wait
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;
			case 12:
				setBucketPosition(0.5);
				step++;
				break;
			case 13:
				runUsingEncoders();
				setDrivePower(-1.0f, -1.0f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
			case 14:
				// wait
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;
			case 15:
				setArmPosition(0.1);
				setBucketPosition(0.5);
				step++;
				break;
			case 16:
				runUsingEncoders();
				setDrivePower(0.5f, -0.5f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
			case 17:
				// wait
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;
			case 18:
				runUsingEncoders();
				setDrivePower(-1.0f, -1.0f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
			case 19:
				// wait
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;
			case 20:
				runUsingEncoders();
				setDrivePower(-0.5f, 0.5f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
			case 21:
				// wait
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;
			case 22:
				runUsingEncoders();
				setDrivePower(1.0f, 1.0f);
				if (haveDriveEncodersReached(2880, 2880))//change val
				{
					resetDriveEncoders();
					setDrivePower(0.0f, 0.0f);
					step++;
				}
				break;
			case 23:
				// wait
				if (haveDriveEncodersReset())
				{
					step++;
				}
				break;

			default:
				//
				// The autonomous actions have been accomplished (i.e. the state has
				// transitioned into its final state.
				//
				break;
		}

		//
		// Send telemetry data to the driver station.
		//
		//update_telemetry (); // Update common telemetry
		telemetry.addData ("18", "State: " + step);

	}
}
