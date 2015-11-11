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
        // try this
        // DcMotorController motorController = hardwareMap.dcMotorController.get("motorController");
        // motorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_WRITE);

        // assign the starting position of the wrist and bucketServo
        armPosition = 0.2;
        bucketPosition = 0.2;
    }


    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
	@Override public void innerLoop () {

        // todo we should think about what to do if someone gets in our way... can we pause the program?

		switch (step)
		{
			//
			// Synchronize the state machine and hardware.
			//
			case 0:
				reset_drive_encoders();
				step++;
				setBucketPosition(bucketPosition);//change val
				setArmPosition(armPosition);//change val
				break;
			case 1:
				run_using_encoders ();

				set_drive_power (1.0f, 1.0f);

				if (have_drive_encoders_reached (2880, 2880))//change values
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			case 2:
				if (have_drive_encoders_reset ())
				{
					step++;
				}
				break;
			case 3:
				run_using_encoders ();
				set_drive_power (-0.5f, 0.5f);
				if (have_drive_encoders_reached (2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			case 4:
				if (have_drive_encoders_reset ())
				{
					step++;
				}
				break;
			case 5:
				run_using_encoders ();
				set_drive_power (1.0f, 1.0f);
				if (have_drive_encoders_reached (2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
            case 6:
                // wait
                if (have_drive_encoders_reset())
                {
                    step++;
                }
                break;
            case 7:
				run_using_encoders ();
				set_drive_power (-0.5f, 0.5f);
				if (have_drive_encoders_reached(2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
            case 8:
				// wait
				if (have_drive_encoders_reset())
				{
					step++;
				}
				break;
			case 9:
				armPosition = 0.6;
				setArmPosition(armPosition);//change val
				step++;
				break;
			case 10:
				run_using_encoders ();
				set_drive_power (1.0f, 1.0f);
				if (have_drive_encoders_reached(2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			case 11:
				// wait
				if (have_drive_encoders_reset())
				{
					step++;
				}
				break;
			case 12:
				bucketPosition = 0.5;
				setBucketPosition(bucketPosition);//change val
				step++;
				break;
			case 13:
				run_using_encoders();
				set_drive_power (-1.0f, -1.0f);
				if (have_drive_encoders_reached(2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			case 14:
				// wait
				if (have_drive_encoders_reset())
				{
					step++;
				}
				break;
			case 15:
				armPosition = 0.1;
				bucketPosition = 0.5;
				setBucketPosition(bucketPosition);//change val
				setArmPosition(armPosition);//change val
				step++;
				break;
			case 16:
				run_using_encoders ();
				set_drive_power (0.5f, -0.5f);
				if (have_drive_encoders_reached(2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			case 17:
				// wait
				if (have_drive_encoders_reset())
				{
					step++;
				}
				break;
			case 18:
				run_using_encoders();
				set_drive_power (-1.0f, -1.0f);
				if (have_drive_encoders_reached(2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			case 19:
				// wait
				if (have_drive_encoders_reset())
				{
					step++;
				}
				break;
			case 20:
				run_using_encoders ();
				set_drive_power (-0.5f, 0.5f);
				if (have_drive_encoders_reached(2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			case 21:
				// wait
				if (have_drive_encoders_reset())
				{
					step++;
				}
				break;
			case 22:
				run_using_encoders ();
				set_drive_power (1.0f, 1.0f);
				if (have_drive_encoders_reached(2880, 2880))//change val
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			case 23:
				// wait
				if (have_drive_encoders_reset())
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
