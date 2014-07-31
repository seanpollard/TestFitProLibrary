package com.ifit.sfit.sparky;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.CaloriesConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.GradeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.KeyObjectConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.LongConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ShortConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WeightConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;
import com.ifit.sparky.fecp.interpreter.status.PortalDeviceSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.TreeMap;

/**
 * Created by jc.almonte on 6/27/14.
 */
public class HandleCmd implements OnCommandReceivedListener
{
    private BaseTest mAct;
    private  double mMaxSpeed = 0.0;
    private  double mMinSpeed = 0.0;
    private ModeId mResultMode;

    private  double mSpeed = 0.0;
    private  double mActualSpeed = 0.0;
    private  double mIncline = 0.0;
    private  double mActualIncline = 0.0;
    private  double mMaxIncline = 0.0;
    private  double mMinIncline = 0.0;
    private  double mTransMax = 0;
    private  double mDistance = 0;
    private  double mRunTime = 0;
    private  double mCalories = 0;
    private  double mWeight = 0;
    private  double mAge = 0;
    private  double mFanSpeed = 0;
    private  double mIdleTimeout = 0;
    private  double mPauseTimeout = 0;
    private KeyObject mKey;
    private String valueToString="none";


    public HandleCmd(BaseTest act) {

        this.mAct = act;
        this.valueToString = "No Value!";
    }
    @Override
    public void onCommandReceived(Command cmd) {

        //check command type
        TreeMap<BitFieldId, BitfieldDataConverter> commandData;

        if(cmd.getStatus().getStsId() == StatusId.FAILED)
        {
            return;
        }
        //if running mode, just join the party
        if(cmd.getCmdId() == CommandId.WRITE_READ_DATA || cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)
        {
            commandData = ((WriteReadDataSts)cmd.getStatus()).getResultData();

            if(cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)
            {
                commandData = ((PortalDeviceSts)cmd.getStatus()).getmSysDev().getCurrentSystemData();
            }
            else {
                WriteReadDataSts sts = (WriteReadDataSts) cmd.getStatus();
                commandData = sts.getResultData();
            };

            //Read the KPH value off of the Brainboard
            if(commandData.containsKey(BitFieldId.KPH)) {

                try {
                    mSpeed = ((SpeedConverter)commandData.get(BitFieldId.KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mSpeed);
                    //System.out.println("Current Speed (TestHandleInfo): " + mSpeed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Actual KPH value off of the Brainboard
            if(commandData.containsKey(BitFieldId.ACTUAL_KPH)) {

                try {
                    mActualSpeed = ((SpeedConverter)commandData.get(BitFieldId.ACTUAL_KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mActualSpeed);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Max KPH value off of the Brainboard
            if(commandData.containsKey(BitFieldId.MAX_KPH)) {
                try{
                    //currently gets a null pointer assigned as of 3/3/2014
                    mMaxSpeed = ((SpeedConverter)commandData.get(BitFieldId.MAX_KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mMaxSpeed);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Min KPH value off of the Brainboard
            if(commandData.containsKey(BitFieldId.MIN_KPH)) {
                try{
                    //currently gets a null pointer assigned as of 3/3/2014
                    mMinSpeed = ((SpeedConverter)commandData.get(BitFieldId.MIN_KPH).getData()).getSpeed();
                    this.valueToString = String.valueOf(mMinSpeed);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Incline value off of the Brainboard
            if(commandData.containsKey(BitFieldId.GRADE)) {
                try {
                    mIncline = ((GradeConverter)commandData.get(BitFieldId.GRADE).getData()).getIncline();
                    this.valueToString = String.valueOf(mIncline);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Actual Incline value off of the Brainboard
            if(commandData.containsKey(BitFieldId.ACTUAL_INCLINE)) {
                try {
                    mActualIncline = ((GradeConverter)commandData.get(BitFieldId.ACTUAL_INCLINE).getData()).getIncline();
                    this.valueToString = String.valueOf(mActualIncline);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Max Incline value off of the Brainboard
            if(commandData.containsKey(BitFieldId.MAX_GRADE)) {
                try {
                    mMaxIncline = ((GradeConverter)commandData.get(BitFieldId.MAX_GRADE).getData()).getIncline();
                    this.valueToString = String.valueOf(mMaxIncline);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Min Incline value off of the Brainboard
            if(commandData.containsKey(BitFieldId.MIN_GRADE)) {
                try {
                    mMinIncline = ((GradeConverter)commandData.get(BitFieldId.MIN_GRADE).getData()).getIncline();
                    this.valueToString = String.valueOf(mMinIncline);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the TransMax value off of the Brainboard
            if(commandData.containsKey(BitFieldId.TRANS_MAX)) {
                try {
                    mTransMax = ((ShortConverter)commandData.get(BitFieldId.TRANS_MAX).getData()).getValue();
                    this.valueToString = String.valueOf(mTransMax);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Workout Mode off of the Brainboard
            if(commandData.containsKey(BitFieldId.WORKOUT_MODE)) {

                try {
                    mResultMode = ((ModeConverter)commandData.get(BitFieldId.WORKOUT_MODE)).getMode();
                    this.valueToString = String.valueOf(mResultMode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Distance value off of the Brainboard
            if(commandData.containsKey(BitFieldId.DISTANCE)) {
                try{
                    mDistance = ((LongConverter)commandData.get(BitFieldId.DISTANCE).getData()).getValue();
                    this.valueToString = String.valueOf(mDistance);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //Read the Running Time value off of the Brainboard
            if(commandData.containsKey(BitFieldId.RUNNING_TIME)) {
                try {
                    mRunTime = ((LongConverter) commandData.get(BitFieldId.RUNNING_TIME).getData()).getValue();
                    this.valueToString = String.valueOf(mRunTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Calories value off of the Brainboard
            if(commandData.containsKey(BitFieldId.CALORIES)) {
                try {
                    //currently returns the bitfield id of 13 only as of 3/4/2014
                    mCalories = ((CaloriesConverter) commandData.get(BitFieldId.CALORIES).getData()).getCalories();
                    this.valueToString = String.valueOf(mCalories);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Weight value off of the Brainboard
            if(commandData.containsKey(BitFieldId.WEIGHT)) {
                try {
                    mWeight = ((WeightConverter) commandData.get(BitFieldId.WEIGHT).getData()).getWeight();
                    this.valueToString = String.valueOf(mWeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Age value off of the Brainboard
            if(commandData.containsKey(BitFieldId.AGE)) {
                try {
                    mAge = ((ByteConverter) commandData.get(BitFieldId.AGE).getData()).getValue();
                    this.valueToString = String.valueOf(mAge);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Fan Speed off of the Brainboard
            if(commandData.containsKey(BitFieldId.FAN_SPEED)) {
                try{
                    mFanSpeed = ((ByteConverter)commandData.get(BitFieldId.FAN_SPEED).getData()).getValue();
                    this.valueToString = String.valueOf(mFanSpeed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Idle Timeout value off of the Brainboard
            if(commandData.containsKey(BitFieldId.IDLE_TIMEOUT)) {
                try {
                    mIdleTimeout = ((ShortConverter) commandData.get(BitFieldId.IDLE_TIMEOUT).getData()).getValue();
                    this.valueToString = String.valueOf(mIdleTimeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Pause Timeout value off of the Brainboard
            if(commandData.containsKey(BitFieldId.PAUSE_TIMEOUT)) {
                try {
                    mPauseTimeout = ((ShortConverter) commandData.get(BitFieldId.PAUSE_TIMEOUT).getData()).getValue();
                    this.valueToString = String.valueOf(mPauseTimeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Object Key value off of the Brainboard
            if(commandData.containsKey(BitFieldId.KEY_OBJECT)) {
                try {
                    mKey = ((KeyObjectConverter) commandData.get(BitFieldId.KEY_OBJECT).getData()).getKeyObject();
                    this.valueToString = String.valueOf(mKey);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    //returns a string version of any of the values assigned in this class
    @Override
    public String toString() {
        return this.valueToString;
    }

    public double getSpeed(){ return this.mSpeed; }
    public double getActualSpeed(){ return this.mActualSpeed; }
    public double getMaxSpeed() { return  this.mMaxSpeed; }
    public double getMinSpeed() { return this.mMinSpeed; }
    public double getIncline(){ return this.mIncline; }
    public double getActualIncline(){ return this.mActualIncline; }
    public double getMaxIncline(){ return this.mMaxIncline; }
    public double getMinIncline(){ return this.mMinIncline; }
    public double getTransMax(){ return this.mTransMax; }
    public ModeId getMode(){ return this.mResultMode; }
    public double getDistance() { return this.mDistance; }
    public double getRunTime() { return this.mRunTime; }
    public double getCalories() { return this.mCalories; }
    public double  getWeight() { return  this.mWeight; }
    public double getAge() { return this.mAge; }
    public double getFanSpeed() { return this.mFanSpeed; }
    public double getIdleTimeout() { return this.mIdleTimeout; }
    public double getPauseTimeout() { return this.mPauseTimeout; }
    public KeyObject getKey() { return this.mKey; }

    }