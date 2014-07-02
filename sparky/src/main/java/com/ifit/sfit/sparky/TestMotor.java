package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.CaloriesConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.GradeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.KeyObjectConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.LongConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ShortConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WeightConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;
import com.ifit.sparky.fecp.FitProUsb;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.SystemStatusListener;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.status.PortalDeviceSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import android.content.Context;
import android.content.Intent;

import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import java.util.Calendar;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;


/**
 * Created by jc.almonte on 6/25/14.
 * Class for motor type tests
 * Used sean's TestMotor code and adapted it to work with SparkyAndroidLib 0.0.9
 */
public class TestMotor {
    private final double MAX_SPEED = 16; //hardcode the value until we can read it
    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;
    private double currentSpeed; // Current motor speed

    //To hold time lapsed
    private long stopTimer = 0;
    private long startTimer = 0;

    private FecpCommand sendKeyCmd;

    //TestMotor constructor. Receive needed parameters from main activity(TestApp) to initialize controller
    public TestMotor(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            hCmd = new HandleCmd(this.mAct);// Init handlers
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //--------------------------------------------//
    //                                            //
    //             Testing Start Speed            //
    //                                            //
    //--------------------------------------------//
    //The testStartSpeed is planned to automate #16 of the software
    //checklist to make sure that the machine starts at 1.0mph or 2.0kph

    public String testStartSpeed() throws Exception {
        //outline for code support #958 **first task to automate**
        //send basic start command to start motor at on position
        //request actual speed from device to make sure it is connected and moving
        //read speed received into this code which should be target speed
        //check against constant variable of 1.0 mph
        //make sure formatting is right for verification for english or metric units
        String startResults;

        startResults = "\n\n----------------------START SPEED TEST RESULTS----------------------\n\n";
        startResults += Calendar.getInstance().getTime() + "\n\n";

        //Declare command
        FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);

        //Set command to read from WORKOUT_MODE byte of the controller
        ((WriteReadDataCmd) modeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand); //send command to the Brainboard
        Thread.sleep(1000); //give time for the command to be done

        startResults += "Status of changing mode to Idle:\t" + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        //print out the current mode
        startResults += "The current mode is " + hCmd.getMode() + "\n";

        ((WriteReadDataCmd) modeCommand.getCommand()).addReadBitField(BitFieldId.KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);
        startResults += "Status of reading the speed:\t" + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";
        currentSpeed = hCmd.getSpeed();
        startResults += "The current speed after setting mode to IDLE is: " + currentSpeed + "\n";


        //Set Mode to Running
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        startResults += "Status of changing mode to Running:\t" + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        startResults += "The current mode is " + hCmd.getMode() + "\n";

        Thread.sleep(5000); // Give the motor 5 secs to reach the desired speed

        ((WriteReadDataCmd) modeCommand.getCommand()).addReadBitField(BitFieldId.KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        //Check status of the command to receive the speed
        startResults += "Status of reading current speed: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        currentSpeed = hCmd.getSpeed();

        startResults += "The current speed after setting mode to running is: " + currentSpeed + "\n";

        if (currentSpeed == 1.0) {
            startResults += "\n* PASS *\n\n";
            startResults += "Speed correctly started at 1.0 mph (2.0 kph)\n";
        } else {
            startResults += "\n* FAIL *\n\n";
            startResults += "Speed should have started at 1.0 mph (2.0 kph), but is actually set at " + currentSpeed + " kph\n";
        }

        //Set Mode to Idle
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        //Remove all commands from the device that have a command ID = "WRITE_READ_DATA"
//        mSFitSysCntrl.getFitProCntrl().removeCmd(MainDevice.getInfo().getDevId(), CommandId.WRITE_READ_DATA);


        return startResults;
    }

    //--------------------------------------------//
    //                                            //
    //              Testing Distance              //
    //                                            //
    //--------------------------------------------//
    public String testDistance() throws Exception {
        //outline for code support #929 in redmine
        //start timer stopwatch
        //send a speed of 10 kph for a 1.5 min/km pace
        //wait 1.5 minutes
        //read distance value
        //verify distance is 250 meters
        String distanceResults;

        distanceResults = "\n----------------------------DISTANCE TEST---------------------------\n\n";
        distanceResults += Calendar.getInstance().getTime() + "\n\n";
        FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);

        //Set to read the WORKOUT_MODE value from the brainboard
        ((WriteReadDataCmd) modeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);

        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand); //Send command to brainboard
        Thread.sleep(1000);

        distanceResults += "The status of reading the initial mode is: " + modeCommand.getCommand().getStatus().getStsId().getDescription() + "\n";
        distanceResults += "Current Mode is: " + hCmd.getMode() + "\n";

        //set mode to running
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        distanceResults += "The status of changing mode to RUNNING is: " + modeCommand.getCommand().getStatus().getStsId().getDescription() + "\n";
        distanceResults += "Current Mode is: " + hCmd.getMode() + "\n";

        //Set the motor speed to 10 KPH
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.KPH, 10);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);

        distanceResults += "The status of setting speed to 10kph: " + modeCommand.getCommand().getStatus().getStsId().getDescription() + "\n";
        distanceResults += "Current Mode is: " + hCmd.getMode() + "\n";
        distanceResults += "Now wait 1.5 mins...\n";

        //wait 1.5 minutes
        Thread.sleep(90000);

        //set tor read distance
       ((WriteReadDataCmd) modeCommand.getCommand()).addReadBitField(BitFieldId.DISTANCE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
       Thread.sleep(1000);
       distanceResults += "The status of setting reading distance command: " + modeCommand.getCommand().getStatus().getStsId().getDescription() + "\n";
       distanceResults += "Current Mode is: " + hCmd.getMode() + "\n";
        //wait for command

        int distance = hCmd.getDistance();
        distanceResults += "The distance was " + distance + "\n";
        distanceResults += "The status of reading the distance is: " + modeCommand.getCommand().getStatus().getStsId().getDescription() + "\n";

        //set mode to back to idle to end the test.
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);
        distanceResults += "The status of changing mode to IDLE is: " + modeCommand.getCommand().getStatus().getStsId().getDescription() + "\n";
        distanceResults += "Current Mode is: " + hCmd.getMode() + "\n";

        //5% tolerance for passing: 250 meters = +/- 12.5
        if ((distance < 234.5) || (distance > 272.5)) {
            distanceResults += "\n * FAIL * \n\nThe distance was off by " + (distance - 250) + "\n\n";
        } else
            distanceResults += "\n * PASS * \n\nThe distance should be 250 meters and is " + distance + " meters which is within 5%\n\n";

        //Remove all commands from the device that have a command ID = "WRITE_READ_DATA"
        mSFitSysCntrl.getFitProCntrl().removeCmd(MainDevice.getInfo().getDevId(), CommandId.WRITE_READ_DATA);

        return distanceResults;
    }


    //--------------------------------------------//
    //                                            //
    //                Testing Mode                //
    //                                            //
    //--------------------------------------------//
    /*
    Future tests include
  * TODO: Testing commands supported on each mode
  * TODO: Testing transitions between modes
  * */
    public String testModeChange() throws Exception{
        //outline for code support #926 in redmine
        //change to mode 1
        //read mode status to verify mode 1
        //change to mode 2
        //read mode status to verify mode 2
        //go through all modes in above manner to validate mode changes have occurred
        String modeResults;

        modeResults = "\n\n----------------------------MODE TEST RESULTS----------------------------\n\n";
        modeResults += Calendar.getInstance().getTime() + "\n\n";

        FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        //FecpCommand readModeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), handleInfoCmd, 100, 1000);//every 1 second

        // Set to command to read WORKOUT_MODE value
        ((WriteReadDataCmd)modeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        //Loop through all modes and confirm they are matched up with what is recorded on Redmine
        for(int i = 0; i < 9; i++)
        {
            try
            {
                ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, i);
                mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
                Thread.sleep(1000);

                modeResults += "Status of changing mode: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";
                modeResults += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() +  "\n\n";
                if(hCmd.getMode().getValue() == i)
                    modeResults += "This mode matches : * PASS *\n\n";
                else modeResults += "This mode does not match : * FAIL *\n\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }

        return modeResults;
    }

    //--------------------------------------------//
    //                                            //
    //          Testing Pause/Resume Speed        //
    //                                            //
    //--------------------------------------------//
    /*
        Future tests include
    * TODO: Testing with different speeds
    * TODO: Test with English units and verify it does proper conversion
    * */

    public String testPauseResume() throws Exception{
        //Support #954 in Redmine
        //Turn mode to Running (mimics Start button press)
        //Set speed to 5 kph
        //set mode to Pause
        //Set mode to Running
        //Verify actual speed is 1.0 mph/2.0 kph (as of 3/12/14, the resume speed is 1.0 kph)
        String pauseResumeResults;

        pauseResumeResults = "\n\n----------------------PAUSE/RESUME SPEED TEST RESULTS----------------------\n\n";
        pauseResumeResults += Calendar.getInstance().getTime() + "\n\n";

        FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);

        //Set Mode to Running
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        //Set command to read WORKOUT_MODE value
        ((WriteReadDataCmd)modeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        pauseResumeResults += "Status of changing mode to Running: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";
        pauseResumeResults += "The current mode is " + hCmd.getMode() + "\n";


        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.KPH, 5);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(10000);// Give it time to motor to reach speed of 5 KPH
        pauseResumeResults += "Status of changing speed to 5 KPH: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";
        //Set command to read speed and verify its at 5 kph
        ((WriteReadDataCmd)modeCommand.getCommand()).addReadBitField(BitFieldId.KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        currentSpeed = hCmd.getSpeed(); //read current speed
        pauseResumeResults += "The speed is currently set at: " + currentSpeed + " kph\n";

        //Set Mode to Pause
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        //Set command to read WORKOUT_MODE
        // ((WriteReadDataCmd)modeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(10000); // Give time for motor to stop seconds


        pauseResumeResults += "Status of changing mode to Pause: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";
        //print out the current mode
        pauseResumeResults += "The current mode is " + hCmd.getMode() + "\n";
        //set command to read speed value (KPH)
        ((WriteReadDataCmd)modeCommand.getCommand()).addReadBitField(BitFieldId.KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000); // Give time for motor to stop seconds
        currentSpeed = hCmd.getSpeed(); //read current speed

        pauseResumeResults += "The speed during PAUSE is: " + currentSpeed + " kph\n";

        //Set Mode to Running
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(5000); // Run for 5 seconds

        pauseResumeResults += "Status of changing mode to Running: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        //print out the current mode
        pauseResumeResults += "The current mode is " + hCmd.getMode() + "\n";

        ((WriteReadDataCmd)modeCommand.getCommand()).addReadBitField(BitFieldId.KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        currentSpeed = hCmd.getSpeed();// Read speed again

        if(currentSpeed == 1.0){
            pauseResumeResults += "\n* PASS *\n\n";
            pauseResumeResults += "The speed should be 1.0 kph and it is currently set at: " + currentSpeed + " kph\n";
        }
        else{
            pauseResumeResults += "\n* FAIL *\n\n";
            pauseResumeResults += "The speed should be 1.0 kph, but it is currently set at: " + currentSpeed + " kph\n";
        }

        //Set Mode to Idle
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        //Set command to read WORKOUT_MODE
        // ((WriteReadDataCmd)modeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        pauseResumeResults += "\nThe current mode is " + hCmd.getMode() + "\n";

        return pauseResumeResults;
    }
    public void runMotor() {
        try {
            FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));

            ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
            Thread.sleep(5000);
            ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
