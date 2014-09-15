package com.ifit.sfit.sparky.helperclasses;

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
import com.ifit.sparky.fecp.interpreter.bitField.converter.ResistanceConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ShortConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WeightConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WorkoutId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;
import com.ifit.sparky.fecp.interpreter.status.PortalDeviceSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.TreeMap;

/**
 * Handles reading Bitfield values from brainboard through a command
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
    private  double mVolume = 0;
    private WorkoutId mWorkoutId;
    private  double mResistance = 0;
    private  double mActualResistance = 0;


    private KeyObject mKey;
    private String valueToString="none";

    /**
     * Constructor
     * @param act
     */
    public HandleCmd(BaseTest act) {

        this.mAct = act;
        this.valueToString = "No Value!";
    }

    /**
     * Handles the reply from the device
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(Command cmd) {
        //TODO: Add rest of bitfields (VOLUME, WORKOUT, etc...)
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

            //Read the Volume value off of the Brainboard
            if(commandData.containsKey(BitFieldId.VOLUME)) {

                try {
                    mVolume = ((ByteConverter)commandData.get(BitFieldId.VOLUME).getData()).getValue();
                    this.valueToString = String.valueOf(mVolume);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Read the Resistance value off of the Brainboard
            if(commandData.containsKey(BitFieldId.RESISTANCE)) {

                try {
                    mResistance = ((ResistanceConverter)commandData.get(BitFieldId.RESISTANCE).getData()).getResistance();
                    this.valueToString = String.valueOf(mResistance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Read the Actual Resistance value off of the Brainboard
            if(commandData.containsKey(BitFieldId.ACTUAL_RESISTANCE)) {

                try {
                    mActualResistance = ((ResistanceConverter)commandData.get(BitFieldId.ACTUAL_RESISTANCE).getData()).getResistance();
                    this.valueToString = String.valueOf(mActualResistance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            //Read the Workout value off of the Brainboard
//            if(commandData.containsKey(BitFieldId.WORKOUT)) {
//
//                try {
//                    mWorkoutId = ((WorkoutConverter)commandData.get(BitFieldId.WORKOUT).getData()).getWorkout();
//                    this.valueToString = String.valueOf(mWorkoutId);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

        }
    }

    //TODO: Add rest of bitfields (VOLUME, WORKOUT, etc...)

    /**
     *
     * @return the string value of last Bitfield read
     */
    @Override
    public String toString() {
        return this.valueToString;
    }

    /**
     * Gets the value of the bitfield specified by bitFieldName
     * @param bitfieldName the name of the Bitfield
     * @return the value of the Bitfield requested to be read
     */
    public double getValue(String bitfieldName) {
        double value = 0;
        switch (bitfieldName)
        {
            case "KPH":
                value = this.mSpeed;
                break;
            case "ACTUAL_KPH":
                value = this.mActualSpeed;
                break;
            case "GRADE":
                value = this.mIncline;
                break;
            case "MAX_GRADE":
                value = this.mMaxIncline;
                break;
            case "MIN_GRADE":
                value = this.mMinIncline;
                break;
            case "ACTUAL_INCLINE":
                value = this.mActualIncline;
                break;
            case "MAX_KPH":
                value = this.mMaxSpeed;
                break;
            case "MIN_KPH":
                value = this.mMinSpeed;
                break;
            case "DISTANCE":
                value = this.mDistance;
                break;
            case "RUNNING_TIME":
                value = this.mRunTime;
                break;
            case "PAUSE_TIMEOUT":
                value = this.mPauseTimeout;
                break;
            case "IDLE_TIMEOUT":
                value = this.mIdleTimeout;
                break;
            case "TRANS_MAX":
                value = this.mTransMax;
                break;
            case "CALORIES":
                value = this.mCalories;
                break;
            case "AGE":
                value = this.mAge;
                break;
            case "WEIGHT":
                value = this.mWeight;
                break;
            case "FAN_SPEED":
                value = this.mFanSpeed;
                break;
//            case "WORKOUT":
//                value = this.mWorkoutId.getValue();
//                break;
            case "RESISTANCE":
                value = this.mResistance;
                break;
            case "ACTUAL_RESISTANCE":
                value = this.mActualResistance;
                break;
            case "VOLUME":
                value = this.mVolume;
                break;
            case "WORKOUT_MODE":
                value = this.mResultMode.getValue();
                break;
        }
        this.valueToString = String.valueOf(value);
        return value;
    }

    /**
     * Overloaded function of {@link #getValue(String)}
     * @param bitFieldId the BitfieldId we want to access
     * @return Bitfield value
     */
    public double getValue(BitFieldId bitFieldId){

        return this.getValue(bitFieldId.name());
    }

    /**
     * Gets value current set speed from brainboard
     * @return Speed
     */
    public double getSpeed(){ return this.mSpeed; }

    /**
     * Gets actual speed value from brainboard
     * @return  Actual Speed
     */
    public double getActualSpeed(){ return this.mActualSpeed; }

    /**
     * Gets the console's Max speed from brainboard
     * @return  Max Speed
     */
    public double getMaxSpeed() { return  this.mMaxSpeed; }

    /**
     * Gets the console's Min Speed value from brainboard
     * @return Min Speed
     */
    public double getMinSpeed() { return this.mMinSpeed; }

    /**
     * Gets current set Incline value from brainboard
     * @return (double) Incline
     */
    public double getIncline(){ return this.mIncline; }

    /**
     * Gets Actual Incline value from brainboard
     * @return Actual Incline
     */
    public double getActualIncline(){ return this.mActualIncline; }

    /**
     * Gets the console's Max Incline value from brainboard
     * @return Max Incline
     */
    public double getMaxIncline(){ return this.mMaxIncline; }

    /**
     * Gets the console's Min Incline value from brainboard
     * @return Min Incline
     */
    public double getMinIncline(){ return this.mMinIncline; }

    /**
     * Gets the console's Trans Max value from brainboard
     * @return Trans Max
     */
    public double getTransMax(){ return this.mTransMax; }

    /**
     * Gets Distance value from brainboard
     * @return Distance
     */
    public double getDistance() { return this.mDistance; }

    /**
     * Gets the current workout running time
     * @return Running Time
     */
    public double getRunTime() { return this.mRunTime; }

    /**
     * Gets the calories burned on current workout form brainboard
     * @return Calories
     */
    public double getCalories() { return this.mCalories; }

    /**
     * Gets the current user set weight from brainboard
     * @return Weight
     */
    public double  getWeight() { return  this.mWeight; }

    /**
     * Gets current user set age from brainboard
     * @return Age
     */
    public double getAge() { return this.mAge; }

    /**
     * Gets the current set speed of the fan from brainbaord
     * @return Fan Speed
     */
    public double getFanSpeed() { return this.mFanSpeed; }

    /**
     * Gets from brainbaord the current Timeout it takes to go from Result to Idle
     * @return Idle Timeout
     */
    public double getIdleTimeout() { return this.mIdleTimeout; }

    /**
     * Gets from brainbaord the current Timeout it takes to go from Pause to Results
     * @return Pause Timeout
     */
    public double getPauseTimeout() { return this.mPauseTimeout; }

    /**
     * Gets the current set volume for the console's speaker
     * @return Volume
     */
    public double getVolume(){return this.mVolume;}

    /**
     * Gets the console's set resistance from brainboard
     * @return Resistance
     */
    public double getResistance(){return this.mResistance;}

    /**
     * Gets the actual Resistance value from brainboard
     * @return
     */
    public double getActualResistance(){return this.mActualResistance;}

    /**
     * Gets the ID of the current workout
     * @return Workout ID
     */
    public WorkoutId getWorkoutId(){return this.mWorkoutId;}

    /**
     * Gets the Key Object from the brainboard. This key object is used to simulate key-presses
     * @return Key Object
     */
    public KeyObject getKey() { return this.mKey;}

    /**
     * Gets the current Workout mode (RUNNING, IDLE, etc...)
     * @return Workout Mode
     */
    public ModeId getMode(){ return this.mResultMode; }
    }