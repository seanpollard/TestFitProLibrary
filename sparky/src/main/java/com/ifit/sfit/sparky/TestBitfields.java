package com.ifit.sfit.sparky;

import android.widget.Switch;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InvalidCommandException;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

/**************************************************************************************
 * Created by jc.almonte on 7/2/14.                                                    *
 * Test class for the BitFields                                                        *
 * Tests to perform:                                                                   *
 *      1. Test read/write operation on each bitfield - testBitfieldRdWr()             *
 *                                                                                     *
 *                                               *
 *          - If read only, verify writing operation to bitfield fails                 *
 *          - If bitfield is not supported, verify exception is thrown                 *
 *                                                                                     *
 *      2. Test valid input values for each bitfield - testBitfieldValuesValidation()  *
 *          - Send invalid values to each bitfield and verify exception is thrown      *
 *          - Send valid values                                                                            *
 *                                                                                     *
 **************************************************************************************/

public class TestBitfields {
    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;
    private String bitfieldRdWrResults = "\n\n----------------------------BITFIELDS TEST RESULTS----------------------------\n\n"; //to store results of test
    private  FecpCommand wrCmd;
    private  FecpCommand rdCmd;

    public TestBitfields(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            hCmd = new HandleCmd(this.mAct);// Init handlers
            ByteBuffer secretKey = ByteBuffer.allocate(32);
            for(int i = 0; i < 32; i++)
            {
                secretKey.put((byte)i);
            }
            try {
                //unlock the system
                this.mSFitSysCntrl.getFitProCntrl().unlockSystem(secretKey);
                Thread.sleep(1000);
                //Get current system device
                MainDevice = this.mFecpController.getSysDev();
                this.wrCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
                this.rdCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd,0,100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //---------------------------------------------------------------------//
    //  Test read/write access on supported read-only - testBitfieldRdWr() //
    //                                                                     //
    //         -  verify writing operation to bitfield fails for read only //
    //         - If bitfield is not supported, verify exception is thrown  //
    //---------------------------------------------------------------------//
//Future test include
//TODO: Verify read operation on all read-only bitfields by reading default values
    //TODO: Do same tests for new and future supported commands

    public String testBitfieldRdWr() throws Exception {
        System.out.println("NOW RUNNING READ ACCESS & UNSUPPORTED BITFIELDS TEST...\n");
        Object valueToWrite = 10;

        bitfieldRdWrResults+= "------Testing Unsupported Bitfields------\n\n"; //to store results of test

        ArrayList<BitFieldId> supportedBitFields = new ArrayList<BitFieldId>(MainDevice.getInfo().getSupportedBitfields());
        ArrayList<BitFieldId> supportedRdBitFields = new ArrayList<BitFieldId>(MainDevice.getInfo().getSupportedReadOnlyBitfields());

        //loop through all bitfields, try to read unsupported ones, verify exception is thrown

        for (BitFieldId bf: BitFieldId.values())
        {
            if(!supportedBitFields.contains(bf))
            {
                //  unsupportedBitFields.add(bf);
                //Try to read a value from this bitfeld and verify that it throws exception
                try{
                    bitfieldRdWrResults += "current bitfield: "+ bf.name()+"\n";

                    ((WriteReadDataCmd)wrCmd.getCommand()).addReadBitField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(1000);
                    bitfieldRdWrResults += "Status trying to read unsupported bitfield: "+ bf.name() +" " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

                    bitfieldRdWrResults+="* FAIL * NO Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "\n";
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    bitfieldRdWrResults += "Status trying to read unsupported bitfield: "+ bf.name() +" "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                    bitfieldRdWrResults+="* PASS * Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "\n";
                    bitfieldRdWrResults+="Details: " + ex.toString() +"\n\n";
                    //Remove bitfield so system can throw exception for next invalid bitfiled
                    ((WriteReadDataCmd)wrCmd.getCommand()).removeReadDataField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(1000);
                }
            }
        }

        bitfieldRdWrResults+= "------Testing Read/Write Access for Supported READ-ONLY Bitfields------\n\n"; //to store results of test

        //Loop through all readonly supported fields
        for(BitFieldId b: supportedRdBitFields)
        {
            //if it's readonly, try to write to it and verify exception is thrown

            try {
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(b, 10);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(1000);
                bitfieldRdWrResults += "* FAIL * NO Exception thrown when trying to write read-only bitfield:  " + b.name() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                bitfieldRdWrResults+="* PASS * Exception thrown when trying to write read-only bitfield:  "+b.name()+ "\n";
                bitfieldRdWrResults+=" Details: " + ex.getMessage() +"\n\n";
            }
        }
        //set mode back to idle to stop the test
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        return bitfieldRdWrResults;
    }
    //-------------------------------------------------------------------------------//
    //   Test input values for each bitfield - testBitfieldValuesValidation()        //
    //                                                                               //
    //      - Send invalid values to each bitfield and verify exception is thrown    //
    //      - Send valid values and then read them to verify it                      //
    //-------------------------------------------------------------------------------//
//Future tests Include
    //TODO: Add validation for new and future supported commands
    //TODO: Test max and min limits and check out of range values
    public String testBitfieldValuesValidation() throws Exception
    {
        System.out.println("NOW RUNNING READ/WRITE ACCESS FOR SUPPORTED BITFIELDS...\n");
        bitfieldRdWrResults+= "------Testing Read/Write Access with valid values for Supported WRITE/READ Bitfields------\n\n"; //to store results of test
        ArrayList<BitFieldId> supportedWrBitFields = new ArrayList<BitFieldId>(MainDevice.getInfo().getSupportedWriteBitfields());

        FecpCommand fanSpeedcmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand kphCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand ageCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand weightCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand gradeCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand workoutModeCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand idleTimeOutCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand pauseTimeOutCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);


        Object valueToWrite;
        Object defaultValue;
        Object temp;

        //Loop through all read/write supported fields, write invalid value and verify read value from brainboard is the default value
        // Then write a valid value and verify it by reading it from brainboard

        for (BitFieldId bf : supportedWrBitFields) {
            //All Write/Read bitfields are: KPH, GRADE, RESISTANCE, FAN_SPEED,VOLUME, WORKOUT_MODE, AUDIO_SOURCE, WORKOUT, AGE, WEIGHT, GEARS
            //TRANS_MAX, BV_VOLUME, BV_FREQUENCY IDLE_TIMEOUT, PAUSE_TIMEOUT, SYSTEM_UNITS, GENDER, FIRST_NAME, LAST_NAME, IFIT_USER_NAME,
            //HEIGHT, KPH_GOAL, GRADE_GOAL, RESISTANCE_GOAL, WATT_GOAL, RPM_GOAL, DISTANCE_GOAL,PULSE_GOAL

            switch (bf.name()) {
                case "KPH":
                    valueToWrite = -5.0;//Invalid value
                    defaultValue = 1.0;
                    verifyBitfield(kphCmd,ModeId.RUNNING,bf,valueToWrite,false,defaultValue);
                    valueToWrite = 3.0;
                    verifyBitfield(kphCmd,ModeId.RUNNING,bf,valueToWrite,true,defaultValue);
                    break;
                case "GRADE":
                    defaultValue = 0.0;
                    valueToWrite = -32770;//Invalid value
                    verifyBitfield(gradeCmd,ModeId.IDLE,bf,valueToWrite,false,defaultValue);
                    valueToWrite = 5.0;
                    verifyBitfield(gradeCmd,ModeId.IDLE,bf,valueToWrite,true,defaultValue);
                    break;
                case "RESISTANCE":
                    break;
                case "FAN_SPEED":
                    defaultValue = 0.0;
                    valueToWrite = -3;//Invalid value
                    verifyBitfield(fanSpeedcmd,ModeId.RUNNING,bf,valueToWrite,false,defaultValue);
                    valueToWrite = 12.0; //set fan speed to 15% of max
                    verifyBitfield(fanSpeedcmd,ModeId.RUNNING,bf,valueToWrite,true,defaultValue);
                    break;
                case "VOLUME":
                    break;
                case "WORKOUT_MODE":
                    ((WriteReadDataCmd) workoutModeCmd.getCommand()).addReadBitField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(workoutModeCmd);
                    Thread.sleep(1000);
                    defaultValue = hCmd.getMode();//In this case the default is last mode
                    valueToWrite = 15.0;
                    verifyBitfield(workoutModeCmd,ModeId.RUNNING,bf,valueToWrite,false,defaultValue);
                    valueToWrite = ModeId.PAUSE; //Pause Mode
                    verifyBitfield(workoutModeCmd,ModeId.RUNNING,bf,valueToWrite,true,defaultValue);
                    break;
                case "AUDIO_SOURCE":
                    break;
                case "WORKOUT":
                    break;
                case "AGE":
                    valueToWrite = 2.0; //invalid value
                    defaultValue = 35.0;
                    verifyBitfield(ageCmd,ModeId.IDLE,bf,valueToWrite,false,defaultValue);
                    valueToWrite = 18.0; //set age to 20 years old
                    verifyBitfield(ageCmd,ModeId.IDLE,bf,valueToWrite,true,defaultValue);
                    break;
                case "WEIGHT":
                    valueToWrite = 20.0; //invalid value
                    defaultValue = 83.91;
                    verifyBitfield(weightCmd,ModeId.IDLE,bf,valueToWrite,false,defaultValue);
                    valueToWrite = 68.0; //set weight to 20 years old
                    verifyBitfield(weightCmd,ModeId.IDLE,bf,valueToWrite,true,defaultValue);
                    break;
                case "GEARS":
                    break;
                case "TRANS_MAX":
                    break;
                case "BV_VOLUME":
                    break;
                case "BV_FREQUENCY":
                    break;
                case "IDLE_TIMEOUT":
                    valueToWrite = -8.0; //set timeout to 9secs to go from pause to IDLE
                    defaultValue = 65530.0;
                    verifyBitfield(idleTimeOutCmd,ModeId.IDLE,bf,valueToWrite,false,defaultValue);
                    valueToWrite = 9.0; //set timeout to 9 secs to go from pause to IDLE
                    verifyBitfield(idleTimeOutCmd,ModeId.IDLE,bf,valueToWrite,true,defaultValue);
                    break;
                case "PAUSE_TIMEOUT":
                    valueToWrite = -6.0; //set timeout to 9secs to go from pause to IDLE
                    defaultValue = 65530.0;
                    verifyBitfield(pauseTimeOutCmd,ModeId.IDLE,bf,valueToWrite,false,defaultValue);
                    valueToWrite = 10.0; //set timeout to 5secs to go from pause to IDLE
                    defaultValue = 0.0;
                    verifyBitfield(pauseTimeOutCmd,ModeId.PAUSE,bf,valueToWrite,true,defaultValue);
                    break;
                case "SYSTEM_UNITS":
                    break;
                case "GENDER":
                    break;
                case "FIRST_NAME":
                    break;
                case "LAST_NAME":
                    break;
                case "HEIGHT":
                    break;
                case "KPH_GOAL":
                    break;
                case "GRADE_GOAL":
                    break;
                case "RESISTANCE_GOAL":
                    break;
                case "WATT_GOAL":
                    break;
                case "RPM_GOAL":
                    break;
                case "DISTANCE_GOAL":
                    break;
                case "PULSE_GOAL":
                    break;
                default:
                    break;

            }
            // mSFitSysCntrl.getFitProCntrl().removeCmd(cmd);
            // Thread.sleep(1000);
        }
//set mode back to idle to stop the test
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        return bitfieldRdWrResults;
    }
    //Helper function to test bitfields
    public void verifyBitfield(FecpCommand cmd, ModeId modeId,BitFieldId bitFieldId, Object valueToWrite, boolean validValue,Object defaultVal) throws InvalidCommandException, InvalidBitFieldException {
        long time=1000;
        if(bitFieldId.name() =="KPH" || bitFieldId.name() =="GRADE")
        {
            time = 5000;
        }
        try {
            ((WriteReadDataCmd) cmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, modeId);
            mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
            Thread.sleep(1000);
            ((WriteReadDataCmd) cmd.getCommand()).addWriteData(bitFieldId, valueToWrite);
            mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
            Thread.sleep(time);
            ((WriteReadDataCmd) cmd.getCommand()).addReadBitField(bitFieldId);
            mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
            Thread.sleep(time);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if(validValue) {
            bitfieldRdWrResults += "\nusing VALID value "+ valueToWrite;
            if (hCmd.toString().equals(String.valueOf(valueToWrite))) {
                bitfieldRdWrResults += "\n* PASS * value " + hCmd.toString() + " read from brainboard matches value " + valueToWrite + " written to bitfield " + bitFieldId.name() + "\n";
            } else {
                bitfieldRdWrResults += "\n* FAIL * value " + hCmd.toString() + " read from brainboard DOESN'T match value " + valueToWrite + " written to bitfield " + bitFieldId.name() + "\n";
            }
        }
        else
        { bitfieldRdWrResults += "\nusing INVALID value "+ valueToWrite;

            if (hCmd.toString().equals(String.valueOf(defaultVal))) {
                bitfieldRdWrResults += "\n* PASS * value " + hCmd.toString() + " read from brainboard matches value default value " + defaultVal + " for bitfield " + bitFieldId.name() + "\n";
            } else {
                bitfieldRdWrResults += "\n* FAIL * value " + hCmd.toString() + " read from brainboard DOESN'T match value " + defaultVal + " for bitfield " + bitFieldId.name() + "\n";
            }
        }
    }


}
