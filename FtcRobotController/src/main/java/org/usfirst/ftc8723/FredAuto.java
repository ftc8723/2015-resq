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
				//
				// Reset the encoders to ensure they are at a known good value.
				//
				reset_drive_encoders ();

				//
				// Transition to the next state when this method is called again.
				//
				step=7; // should be step++;

				break;
			//
			// Drive forward until the encoders exceed the specified values.
			//
			case 1:
				//
				// Tell the system that motor encoders will be used.  This call MUST
				// be in this state and NOT the previous or the encoders will not
				// work.  It doesn't need to be in subsequent states.
				//
				run_using_encoders ();

				//
				// Start the drive wheel motors at full power.
				//
				set_drive_power (1.0f, 1.0f);

				//
				// Have the motor shafts turned the required amount?
				//
				// If they haven't, then the op-mode remains in this state (i.e this
				// block will be executed the next time this method is called).
				//
				if (have_drive_encoders_reached (2880, 2880))
				{
					//
					// Reset the encoders to ensure they are at a known good value.
					//
					reset_drive_encoders ();

					//
					// Stop the motors.
					//
					set_drive_power (0.0f, 0.0f);

					//
					// Transition to the next state when this method is called
					// again.
					//
					step++;
				}
				break;
			//
			// Wait...
			//
			case 2:
				if (have_drive_encoders_reset ())
				{
					step++;
				}
				break;
			//
			// Turn left until the encoders exceed the specified values.
			//
			case 3:
				run_using_encoders ();
				set_drive_power (-.5f, 0.5f);
				if (have_drive_encoders_reached (2880, 2880))
				{
					reset_drive_encoders ();
					set_drive_power (0.0f, 0.0f);
					step++;
				}
				break;
			//
			// Wait...
			//
			case 4:
				if (have_drive_encoders_reset ())
				{
					step++;
				}
				break;
			//
			// Turn right until the encoders exceed the specified values.
			//
			case 5:
				run_using_encoders ();
				set_drive_power (1.0f, -1.0f);
				if (have_drive_encoders_reached (2880, 2880))
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
                setArmPosition(0.5f);
                setBucketPosition(0.5f);
                break;
            case 8:
                // wait
                if (armServo.getPosition() == armPosition && bucketServo.getPosition() == bucketPosition)
                {
                    step++;
                }
                telemetry.addData("armServo", armServo.getPosition());
                telemetry.addData("bucketServo", bucketServo.getPosition());
                break;
			//
			// Perform no action - stay in this case until the OpMode is stopped.
			// This method will still be called regardless of the state machine.
			//
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
