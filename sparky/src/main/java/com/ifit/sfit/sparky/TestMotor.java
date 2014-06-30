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
public class TestMotor implements OnCommandReceivedListener {
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
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();
        }
        catch (Exception ex)
        {
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

    public String testStartSpeed() throws Exception{
        //outline for code support #958 **first task to automate**
        //send basic start command to start motor at on position
        //request actual speed from device to make sure it is connected and moving
        //read speed received into this code which should be target speed
        //check against constant variable of 1.0 mph
        //make sure formatting is right for verification for english or metric units
        String startResults;

        startResults = "\n\n----------------------START SPEED TEST RESULTS----------------------\n\n";
        startResults += Calendar.getInstance().getTime() + "\n\n";

        //Declare coomand
        FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));
        FecpCommand readModeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),this, 100, 1000);//every 1 second

        //Set Mode to Idle, command is writing here
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand); //send command to the Brainboard
        Thread.sleep(1000); //give time for the command to be done

        startResults += "Status of changing mode to Idle:\t" + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";


        //set command to read
        // specify data to be read (workout mode data)
        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        mSFitSysCntrl.getFitProCntrl().addCmd(readModeCommand); //send command to Brainboard
        Thread.sleep(1000);

        //print out the current mode
        startResults += "The current mode is " + hCmd.getMode()+ "\n";


        //Set Mode to Running
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        startResults += "Status of changing mode to Running:\t" + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        startResults += "The current mode is " + hCmd.getMode()+ "\n";

        Thread.sleep(5000);

        //set command to read speed
        FecpCommand readSpeedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),this, 100, 1000);//every 1 second
        ((WriteReadDataCmd) readSpeedCommand.getCommand()).addReadBitField(BitFieldId.KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(readSpeedCommand);

        Thread.sleep(1000);

        //Check status of the command to receive the speed
        startResults += "Status of reading current speed: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        currentSpeed = hCmd.getSpeed();

        startResults += "The current speed after setting mode to running is: " + currentSpeed + "\n";

        if(currentSpeed == 1.0){
            startResults += "\n* PASS *\n\n";
            startResults += "Speed correctly started at 1.0 mph (2.0 kph)\n";
        }
        else{
            startResults += "\n* FAIL *\n\n";
            startResults += "Speed should have started at 1.0 mph (2.0 kph), but is actually set at " + currentSpeed + " kph\n";
        }

        //Set Mode to Idle
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

       //Remove all commands from the device that have a command ID = "WRITE_READ_DATA"
        mSFitSysCntrl.getFitProCntrl().removeCmd(MainDevice.getInfo().getDevId(),CommandId.WRITE_READ_DATA);




        return startResults;
    }
    //--------------------------------------------//
    //                                            //
    //  Testing All Speeds (in decrements of 0.1) //
    //                                            //
    //--------------------------------------------//
    public String testSpeedController() throws Exception{
        //outline for code support #927 in redmine
        //run test for treadmill & incline trainers
        //send speed command
        //validate speed was sent
        //read speed (not actual speed)
        //validate speed response is what you sent originally
        //go through entire speed range 1-15mph, for example
        //TODO: read actual parameters from brainboard (when it becomes available on next SparkyAndroidLib release)
        String testResults;
        String currentWorkoutMode;
        String currentMode;
        final int NUM_TESTS = 1;
        double roundedJ;

        testResults = "\n--------------------------SPEED TEST RESULTS--------------------------\n\n";
        testResults += Calendar.getInstance().getTime() + "\n\n";

        FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));
        FecpCommand speedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));
        FecpCommand readModeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);//every 1 second
        FecpCommand readSpeedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);//every 1 second


        //Set the mode to idle
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);

        Thread.sleep(1000);

        testResults += "Status of setting Mode to Idle: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
       mSFitSysCntrl.getFitProCntrl().addCmd(readModeCommand);

        Thread.sleep(1000);

        //Check status
        testResults += "Status of reading mode command: " + (readModeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        currentMode = "Current Mode is: " + hCmd.getMode() + "\n";
        testResults += currentMode;

        //Set the Mode to Running Mode
        ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
       mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);

        Thread.sleep(1000);

        //Check status of changing the mode to running
        testResults += "Status of changing mode to running: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        /*
       ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
       mSFitSysCntrl.getFitProCntrl().addCmd(readModeCommand);
        Thread.sleep(1000);
       */
        Thread.sleep(1000);

        currentMode = "Current Mode is: " + hCmd.getMode() + "\n";
        testResults += currentMode;

        //TODO: read the min speed from the Brainboard (not implemented yet)
//        FecpCommand readMinSpeedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), handleInfoCmd, 0, 1000);
//        ((WriteReadDataCmd)readMinSpeedCommand.getCommand()).addReadBitField(BitFieldId.MIN_KPH);
//        fecpController.addCmd(readMinSpeedCommand);
//
//        minSpeed = hCmd.getMinSpeed();
//        Thread.sleep(1000);
//        System.out.println("The min speed is " + minSpeed);

        //TODO: read the max speed from the Brainboard (not implemented yet)
//        FecpCommand readMaxSpeedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), handleInfoCmd, 0, 1000);
//        ((WriteReadDataCmd)readMaxSpeedCommand.getCommand()).addReadBitField(BitFieldId.MAX_KPH);
//        fecpController.addCmd(readMaxSpeedCommand);
//
//        maxSpeed = hCmd.getMaxSpeed();
//        Thread.sleep(1000);
//        System.out.println("The max speed is " + maxSpeed);

        //Set command to read the speed off of device
        ((WriteReadDataCmd)readSpeedCommand.getCommand()).addReadBitField(BitFieldId.KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(readSpeedCommand);
        Thread.sleep(1000);
/*
       ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        mSFitSysCntrl.getFitProCntrl().addCmd(readModeCommand);
*/
        Thread.sleep(1000);


        //Set NUM_TESTS to the number of times you want to run the test
        for(int i = 0; i < NUM_TESTS; i++)
        {
            for(double j = MAX_SPEED; j >= 0.5; j-=0.1)      //Set MAX_SPEED TO the maximum speed
            {
                roundedJ = ((double)Math.round(j*10)/10);
                testResults += "Sending a command for speed " + roundedJ + " to the FecpController\n";

                //Set value for the speed
               ((WriteReadDataCmd) speedCommand.getCommand()).addWriteData(BitFieldId.KPH, roundedJ);
               mSFitSysCntrl.getFitProCntrl().addCmd(speedCommand);

                if(roundedJ == 0)
                {
                    Thread.sleep(3000);
                }
                else if(roundedJ < MAX_SPEED && roundedJ > 0)
                {
                    Thread.sleep(2000);
                }
                else if(roundedJ == MAX_SPEED)
                {
                    Thread.sleep((long)(j)*1000*2); //Typecast j (speed) to a long to delay time for double the time based on the speed
                    //Ex: delay for 12 kph should be 24 seconds (24000 milliseconds)
                }

                //Check status of the command to send the speed
                testResults += "Status of sending speed " + roundedJ + ": " + (speedCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

                testResults += "Status of reading the Mode: " + (readModeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";
                Thread.sleep(1000);
                hCmd = new HandleCmd(this.mAct,readModeCommand.getCommand());
                currentWorkoutMode = "Workout mode of speed " + roundedJ + " is " + hCmd.getMode() + "\n";
                Thread.sleep(1000);

                //((ModeConverter)(((WriteReadDataSts)readModeCommand.getCommand().getStatus()).getResultData().get(BitFieldId.WORKOUT_MODE))).getMode()
                testResults += currentWorkoutMode;

             //  mSFitSysCntrl.getFitProCntrl().removeCmd(readModeCommand);


                //Check status of the command to receive the speed
                testResults += "Status of receiving speed " + roundedJ + ": " + (readSpeedCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

                currentSpeed = hCmd.getSpeed();

                Thread.sleep(1000);

                //TODO: Read the ACTUAL speed off of device (not yet implemented)
//                FecpCommand readActualSpeed = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), handleInfoCmd, 0, 1000);//every 1 second
//                ((WriteReadDataCmd)readActualSpeed.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
//               mSFitSysCntrl.getFitProCntrl().addCmd(readActualSpeed);
//
//                Thread.sleep(1000);
//
//                //Check status of the command to receive the incline
//                testResults += "Status of reading actual speed at " + j + "%: " + (readActualSpeed.getCommand()).getStatus().getStsId().getDescription() + "\n";
//
//                testResults += "THE ACTUAL SPEED IS CURRENTLY AT: " + hCmd.getActualSpeed() + " KPH\n";

                testResults += "\nFor Speed " + roundedJ + ":\n";

                //with double values the rounding does not always work with 0.1 values
                if((Math.abs(roundedJ - currentSpeed) < 0.1))
                {
                    testResults += "\n* PASS *\n\nThe speed was minimally off by " + (roundedJ - currentSpeed) + "\n\n";
                }
                else
                {
                    testResults += "\n* FAIL *\n\nThe speed is greatly off by " + (roundedJ - currentSpeed) + "\n\n";
                }

                System.out.println("Current Speed " + roundedJ + " (from Brainboard): " + currentSpeed);
            }
            //Set the mode to idle
            ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
           mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);

        }

        return testResults;
    }



    public void runMotor()
    {
        try {
            FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));

            ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
             mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
            Thread.sleep(5000);
            ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

//Implementation of method from OnCommandReceivedListener Interface
 //This method calls HandleCmd which is basically same as TestHandleInfo on version 0.0.8
    @Override
    public void onCommandReceived(Command cmd) {
    hCmd = new HandleCmd(this.mAct, cmd);
    }

}
