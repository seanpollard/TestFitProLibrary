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

import java.util.ArrayList;
import java.util.Collection;

/**************************************************************************************
 * Created by jc.almonte on 7/2/14.                                                    *
 * Test class for the BitFields                                                        *
 * Tests to perform:                                                                   *
 *      1. Test read/write operation on each bitfield - testBitfieldRdWr()              *
 *                                                                                     *
 *          - Send valid values to write to all the bitfields                          *
 *          - Read values written to verify them                                       *
 *          - If read only, verify writing operation to bitfield fails                 *
 *          - If bitfield is not supported, verify exception is thrown                 *
 *                                                                                     *
 *      2. Test valid input values for each bitfield - testBitfieldValuesValidation()  *
 *          - Send invalid values to each bitfield and verify exception is thrown      *
 *                                                                                     *
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

    public TestBitfields(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            hCmd = new HandleCmd(this.mAct);// Init handlers
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();

        }
        catch (Exception ex) {
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

        Object valueToWrite = 10;
        FecpCommand cmd= new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);

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

                    ((WriteReadDataCmd)cmd.getCommand()).addReadBitField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    bitfieldRdWrResults += "Status trying to read unsupported bitfield: "+ bf.name() +" " + (cmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

                    bitfieldRdWrResults+="* FAIL * NO Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "\n";

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    bitfieldRdWrResults += "Status trying to read unsupported bitfield: "+ bf.name() +" "+ (cmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                    bitfieldRdWrResults+="* PASS * Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "\n";
                    bitfieldRdWrResults+="Details: " + ex.toString() +"\n\n";
                }
            }
        }

         bitfieldRdWrResults+= "------Testing Read/Write Access for Supported READ-ONLY Bitfields------\n\n"; //to store results of test

            //Loop through all readonly supported fields
        for(BitFieldId b: supportedRdBitFields)
        {
            //if it's readonly, try to write to it and verify exception is thrown

                try {
                    ((WriteReadDataCmd) cmd.getCommand()).addWriteData(b, 10);
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
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
         mSFitSysCntrl.getFitProCntrl().removeCmd(cmd);
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
    public String testBitfieldValuesValidation() throws Exception
    {
        bitfieldRdWrResults+= "------Testing Read/Write Access with valid values for Supported WRITE/READ Bitfields------\n\n"; //to store results of test
        ArrayList<BitFieldId> supportedWrBitFields = new ArrayList<BitFieldId>(MainDevice.getInfo().getSupportedWriteBitfields());
//        mSFitSysCntrl.getFitProCntrl().removeCmd(MainDevice.getInfo().getDevId(),CommandId.WRITE_READ_DATA);

        FecpCommand fanSpeedcmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand kphCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand ageCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand weightCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand gradeCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        FecpCommand workoutModeCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);


        Object valueToWrite;
        Object temp;
/*
            ((WriteReadDataCmd) cmd.getCommand()).addReadBitField(bf);
            mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
            Thread.sleep(1000);


            ((WriteReadDataCmd) cmd.getCommand()).addWriteData(bf,valueToWrite );
            mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
            Thread.sleep(2000);

            bitfieldRdWrResults += "Status of setting the Age to " + valueToWrite + ": " + (cmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

            temp = hCmd.toString();

            //temp = ((ByteConverter) this.mSFitSysCntrl.getFitProCntrl().getSysDev().getCurrentSystemData().get(BitFieldId.AGE).getData()).getValue();

            if (hCmd.toString().equals(String.valueOf(valueToWrite))) {
                bitfieldRdWrResults += "\n* PASS * value " + hCmd.toString() + " read from brainboard matches value " + valueToWrite + " written to bitfield " + bf.name() + "\n";
            } else {
                bitfieldRdWrResults += "\n* FAIL * value " + hCmd.toString() + " read from brainboard DOESN'T match value " + valueToWrite + " written to bitfield " + bf.name() + "\n";
            }
            valueToWrite++;
*/
        //Loop through all read/write supported fields, write a value and verify it by reading it from brainbaord
    try {

        for (BitFieldId bf : supportedWrBitFields) {
            //All Write/Read bitfields are: KPH, GRADE, RESISTANCE, FAN_SPEED,VOLUME, WORKOUT_MODE, AUDIO_SOURCE, WORKOUT, AGE, WEIGHT, GEARS
            //TRANS_MAX, BV_VOLUME, BV_FREQUENCY IDLE_TIMEOUT, PAUSE_TIMEOUT, SYSTEM_UNITS, GENDER, FIRST_NAME, LAST_NAME, IFIT_USER_NAME,
            //HEIGHT, KPH_GOAL, GRADE_GOAL, RESISTANCE_GOAL, WATT_GOAL, RPM_GOAL, DISTANCE_GOAL,PULSE_GOAL

            switch (bf.name()) {
                case "KPH":
                    valueToWrite = 3.0;
                    verifyBitfield(kphCmd,ModeId.RUNNING,bf,valueToWrite);
                    break;
                case "GRADE":
                    valueToWrite = 5.0;
                    verifyBitfield(gradeCmd,ModeId.IDLE,bf,valueToWrite);
                    break;
                case "RESISTANCE":
                    break;
                case "FAN_SPEED":
                    valueToWrite = 12.0; //set fan speed to 15% of max
                    verifyBitfield(fanSpeedcmd,ModeId.RUNNING,bf,valueToWrite);
                    break;
                case "VOLUME":
                    break;
                case "WORKOUT_MODE":
                    valueToWrite = ModeId.PAUSE; //Pause Mode
                    verifyBitfield(workoutModeCmd,ModeId.RUNNING,bf,valueToWrite);
                    break;
                case "AUDIO_SOURCE":
                    break;
                case "WORKOUT":
                    break;
                case "AGE":
                    valueToWrite = 18.0; //set age to 20 years old
                    verifyBitfield(ageCmd,ModeId.IDLE,bf,valueToWrite);
                    break;
                case "WEIGHT":
                    valueToWrite = 50.0; //set weight to 20 years old
                    verifyBitfield(weightCmd,ModeId.IDLE,bf,valueToWrite);
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
                    /*
                    valueToWrite = 9; //set timeout to 9secs to go from pause to IDLE
                    ((WriteReadDataCmd)cmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.IDLE);
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    ((WriteReadDataCmd)cmd.getCommand()).addWriteData(bf,valueToWrite); //set workout to valueToWrite
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    ((WriteReadDataCmd)cmd.getCommand()).addReadBitField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    if(hCmd.toString().equals(String.valueOf(valueToWrite)))
                    {
                        bitfieldRdWrResults+="\n* PASS * value " + hCmd.toString() + " read from brainboard matches value "+ valueToWrite + " written to bitfield "+bf.name()+ "\n";
                    }
                    else
                    {
                        bitfieldRdWrResults+="\n* FAIL * value " + hCmd.toString() + " read from brainboard DOESN'T match value "+ valueToWrite + " written to bitfield "+bf.name()+ "\n";
                    }*/
                    break;
                case "PAUSE_TIMEOUT":
                    /*
                    valueToWrite = 10; //set timeout to 5secs to go from pause to IDLE
                    ((WriteReadDataCmd)cmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.PAUSE);
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    ((WriteReadDataCmd)cmd.getCommand()).addWriteData(bf,valueToWrite); //set workout to valueToWrite
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    ((WriteReadDataCmd)cmd.getCommand()).addReadBitField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    if(hCmd.toString().equals(String.valueOf(valueToWrite)))
                    {
                        bitfieldRdWrResults+="\n* PASS * value " + hCmd.toString() + " read from brainboard matches value "+ valueToWrite + " written to bitfield "+bf.name()+ "\n";
                    }
                    else
                    {
                        bitfieldRdWrResults+="\n* FAIL * value " + hCmd.toString() + " read from brainboard DOESN'T match value "+ valueToWrite + " written to bitfield "+bf.name()+ "\n";
                    }*/
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
    }
catch (Exception ex)
{
    ex.printStackTrace();
}

        return bitfieldRdWrResults;
    }
public void verifyBitfield(FecpCommand cmd, ModeId modeId,BitFieldId bitFieldId, Object valueToWrite) throws InvalidCommandException, InvalidBitFieldException {
    long time=1000;
    if(modeId.name() =="KPH" || modeId.name() =="GRADE")
    {
        time = 5000;
    }
    try {
        ((WriteReadDataCmd) cmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, modeId);
        mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
        Thread.sleep(1000);
        ((WriteReadDataCmd) cmd.getCommand()).addWriteData(bitFieldId, valueToWrite); //set speed to valueToWrite KPH
        mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
        Thread.sleep(time);
        ((WriteReadDataCmd) cmd.getCommand()).addReadBitField(bitFieldId);
        mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
        Thread.sleep(1000);
    }
    catch (Exception ex)
    {
        ex.printStackTrace();
    }
    if (hCmd.toString().equals(String.valueOf(valueToWrite))) {
        bitfieldRdWrResults += "\n* PASS * value " + hCmd.toString() + " read from brainboard matches value " + valueToWrite + " written to bitfield " + bitFieldId.name() + "\n";
    } else {
        bitfieldRdWrResults += "\n* FAIL * value " + hCmd.toString() + " read from brainboard DOESN'T match value " + valueToWrite + " written to bitfield " + bitFieldId.name() + "\n";
    }
}


}
