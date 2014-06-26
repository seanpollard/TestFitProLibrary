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

        FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));
        FecpCommand readModeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),this, 100, 1000);//every 1 second

        //Set Mode to Idle
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        startResults += "Status of changing mode to Idle:\t" + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        //print out the current mode
        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        mSFitSysCntrl.getFitProCntrl().addCmd(readModeCommand);
        Thread.sleep(1000);

        startResults += "The current mode is " + ((ModeConverter)this.mSFitSysCntrl.getFitProCntrl().getSysDev().getCurrentSystemData().get(BitFieldId.WORKOUT_MODE)).getMode().getDescription()
        + "\n";

        //Set Mode to Running
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

        startResults += "Status of changing mode to Running:\t" + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        //print out the current mode
       // ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        //mSFitSysCntrl.getFitProCntrl().addCmd(readModeCommand);
        //Thread.sleep(1000);
        startResults += "The current mode is " + ((ModeConverter)this.mSFitSysCntrl.getFitProCntrl().getSysDev().getCurrentSystemData().get(BitFieldId.WORKOUT_MODE)).getMode().getDescription()
                + "\n";

        FecpCommand readSpeedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),this, 100, 1000);//every 1 second
        ((WriteReadDataCmd)readSpeedCommand.getCommand()).addReadBitField(BitFieldId.KPH);
        mSFitSysCntrl.getFitProCntrl().addCmd(readSpeedCommand);

        Thread.sleep(1000);

        //Check status of the command to receive the speed
        startResults += "Status of reading current speed: " + (readSpeedCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        currentSpeed = ((SpeedConverter)this.mSFitSysCntrl.getFitProCntrl().getSysDev().getCurrentSystemData().get(BitFieldId.KPH)).getSpeed();

       // mFecpController.removeCmd(readSpeedCommand);

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

        return startResults;
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
    @Override
    public void onCommandReceived(Command cmd) {
    HandleCmd hCmd = new HandleCmd(this.mAct, cmd);
    this.mAct.runOnUiThread(hCmd);
    }

    private class HandleCmd implements Runnable
    {
        private Command mCmd;
        private Context mContext;
        private  double mMaxSpeed = 0.0;
        private  double mMinSpeed = 0.0;
        private  ModeId mResultMode;
        private  double mSpeed = 0.0;
        private  double mActualSpeed = 0.0;
        private  double mIncline = 0.0;
        private  double mActualIncline = 0.0;
        private  double mMaxIncline = 0.0;
        private  double mMinIncline = 0.0;
        private  int mTransMax = 0;
        private  int mDistance = 0;
        private  int mRunTime = 0;
        private  double mCalories = 0;
        private  double mWeight = 0;
        private  int mAge = 0;
        private  int mFanSpeed = 0;
        private  int mIdleTimeout = 0;
        private  int mPauseTimeout = 0;
        private  KeyObject mKey;


        private HandleCmd(Context context, Command cmd) {
                this.mCmd = cmd;
                this.mContext = context;
            }
        @Override
        public void run() {
            //check command type
            TreeMap<BitFieldId, BitfieldDataConverter> commandData;

            if(mCmd.getStatus().getStsId() == StatusId.FAILED)
            {
                return;
            }
            //if running mode, just join the party
            if(mCmd.getCmdId() == CommandId.WRITE_READ_DATA) // || mCmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)
            {
                commandData = ((WriteReadDataSts)mCmd.getStatus()).getResultData();
                /*
                if(mCmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)
                {
                    commandData = ((PortalDeviceSts)mCmd.getStatus()).getmSysDev().getCurrentSystemData();
                }
                else {
                    WriteReadDataSts sts = (WriteReadDataSts) mCmd.getStatus();
                    commandData = sts.getResultData();
                };
            */
                //Read the KPH value off of the Brainboard
                if(commandData.containsKey(BitFieldId.KPH)) {

                    try {
                        mSpeed = ((SpeedConverter)commandData.get(BitFieldId.KPH).getData()).getSpeed();
                        //System.out.println("Current Speed (TestHandleInfo): " + mSpeed);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Actual KPH value off of the Brainboard
                if(commandData.containsKey(BitFieldId.ACTUAL_KPH)) {

                    try {
                        mActualSpeed = ((SpeedConverter)commandData.get(BitFieldId.ACTUAL_KPH).getData()).getSpeed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Max KPH value off of the Brainboard
                if(commandData.containsKey(BitFieldId.MAX_KPH)) {
                    try{
                        //currently gets a null pointer assigned as of 3/3/2014
                        mMaxSpeed = ((SpeedConverter)commandData.get(BitFieldId.MAX_KPH).getData()).getSpeed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Min KPH value off of the Brainboard
                if(commandData.containsKey(BitFieldId.MIN_KPH)) {
                    try{
                        //currently gets a null pointer assigned as of 3/3/2014
                        mMinSpeed = ((SpeedConverter)commandData.get(BitFieldId.MIN_KPH).getData()).getSpeed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Incline value off of the Brainboard
                if(commandData.containsKey(BitFieldId.GRADE)) {
                    try {
                        mIncline = ((GradeConverter)commandData.get(BitFieldId.GRADE).getData()).getIncline();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Actual Incline value off of the Brainboard
                if(commandData.containsKey(BitFieldId.ACTUAL_INCLINE)) {
                    try {
                        mActualIncline = ((GradeConverter)commandData.get(BitFieldId.ACTUAL_INCLINE).getData()).getIncline();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Max Incline value off of the Brainboard
                if(commandData.containsKey(BitFieldId.MAX_GRADE)) {
                    try {
                        mMaxIncline = ((GradeConverter)commandData.get(BitFieldId.MAX_GRADE).getData()).getIncline();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Min Incline value off of the Brainboard
                if(commandData.containsKey(BitFieldId.MIN_GRADE)) {
                    try {
                        mMinIncline = ((GradeConverter)commandData.get(BitFieldId.MIN_GRADE).getData()).getIncline();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the TransMax value off of the Brainboard
                if(commandData.containsKey(BitFieldId.TRANS_MAX)) {
                    try {
                        mTransMax = ((ShortConverter)commandData.get(BitFieldId.TRANS_MAX).getData()).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Workout Mode off of the Brainboard
                if(commandData.containsKey(BitFieldId.WORKOUT_MODE)) {

                    try {
                        mResultMode = ((ModeConverter)commandData.get(BitFieldId.WORKOUT_MODE)).getMode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Distance value off of the Brainboard
                if(commandData.containsKey(BitFieldId.DISTANCE)) {
                    try{
                        mDistance = ((LongConverter)commandData.get(BitFieldId.DISTANCE).getData()).getValue();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                //Read the Running Time value off of the Brainboard
                if(commandData.containsKey(BitFieldId.RUNNING_TIME)) {
                    try {
                        mRunTime = ((LongConverter) commandData.get(BitFieldId.RUNNING_TIME).getData()).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Calories value off of the Brainboard
                if(commandData.containsKey(BitFieldId.CALORIES)) {
                    try {
                        //currently returns the bitfield id of 13 only as of 3/4/2014
                        mCalories = ((CaloriesConverter) commandData.get(BitFieldId.CALORIES).getData()).getCalories();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Weight value off of the Brainboard
                if(commandData.containsKey(BitFieldId.WEIGHT)) {
                    try {
                        mWeight = ((WeightConverter) commandData.get(BitFieldId.WEIGHT).getData()).getWeight();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Age value off of the Brainboard
                if(commandData.containsKey(BitFieldId.AGE)) {
                    try {
                        mAge = ((ByteConverter) commandData.get(BitFieldId.AGE).getData()).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Fan Speed off of the Brainboard
                if(commandData.containsKey(BitFieldId.FAN_SPEED)) {
                    try{
                        mFanSpeed = ((ByteConverter)commandData.get(BitFieldId.FAN_SPEED).getData()).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Idle Timeout value off of the Brainboard
                if(commandData.containsKey(BitFieldId.IDLE_TIMEOUT)) {
                    try {
                        mIdleTimeout = ((ShortConverter) commandData.get(BitFieldId.IDLE_TIMEOUT).getData()).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Pause Timeout value off of the Brainboard
                if(commandData.containsKey(BitFieldId.PAUSE_TIMEOUT)) {
                    try {
                        mIdleTimeout = ((ShortConverter) commandData.get(BitFieldId.PAUSE_TIMEOUT).getData()).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Read the Object Key value off of the Brainboard
                if(commandData.containsKey(BitFieldId.KEY_OBJECT)) {
                    try {
                        mKey = ((KeyObjectConverter) commandData.get(BitFieldId.KEY_OBJECT).getData()).getKeyObject();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }
       

    }
}
