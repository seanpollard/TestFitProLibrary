package com.ifit.sfit.sparky.tests;

import com.ifit.sfit.sparky.helperclasses.CommonFeatures;
import com.ifit.sfit.sparky.helperclasses.HandleCmd;
import com.ifit.sfit.sparky.helperclasses.SFitSysCntrl;
import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InvalidCommandException;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**************************************************************************************
 * Created by jc.almonte on 7/2/14.                                                    *
 * Test class for the BitFields                                                        *
 * Tests to perform:                                                                   *
 *      1. Test read/write operation on each bitfield - testBitfieldRdWr()             *
 *                                                                                     *
 *                                                                                     *
 *          - If read only, verify writing operation to bitfield fails                 *
 *          - If bitfield is not supported, verify exception is thrown                 *
 *                                                                                     *
 *      2. Test valid input values for each bitfield - testBitfieldValuesValidation()  *
 *          - Send invalid values to each bitfield and verify exception is thrown      *
 *          - Send valid values                                                        *
 *                                                                                     *
 **************************************************************************************/

public class TestBitfields extends CommonFeatures {
    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private BaseTest mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;
    private  FecpCommand wrCmd;
    private String emailAddress;

    public TestBitfields(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            this.emailAddress = "jc.almonte@iconfitness.com";
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
                ((WriteReadDataCmd) wrCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test read/write access on supported read-only bit fields
     *  - verify writing operation to bitfield fails for read only
     *  - If bitfield is not supported, verify exception is thrown
     * @return text log of test Results
     * @throws Exception
     */
    public String testBitfieldRdWr() throws Exception {
        String results = "";
        System.out.println("NOW RUNNING READ ACCESS & UNSUPPORTED BITFIELDS TEST...<br>");
        Object valueToWrite = 10;

        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        appendMessage("<br><br>----------------------------BITFIELDS TEST RESULTS----------------------------<br><br>");
        appendMessage("------Testing Unsupported Bitfields------<br><br>"); //to store results of test

        results+="\n\n----------------------------BITFIELDS TEST RESULTS----------------------------\n\n";
        results+="------Testing Unsupported Bitfields------\n\n"; //to store results of test


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
                    appendMessage("current bitfield: "+ bf.name()+"<br>");
                    
                    results+="current bitfield: "+ bf.name()+"\n";

                    ((WriteReadDataCmd)wrCmd.getCommand()).addReadBitField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(1000);
                    appendMessage("Status trying to read unsupported bitfield: "+ bf.name() +" " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                    appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> NO Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "<br>");

                    results+="Status trying to read unsupported bitfield: "+ bf.name() +" " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                    results+="\n* FAIL *\n\n NO Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "\n";

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    appendMessage("Status trying to read unsupported bitfield: "+ bf.name() +" "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                    appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br> Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "<br>");
                    appendMessage("Details: " + ex.toString() +"<br><br>");

                    results+="Status trying to read unsupported bitfield: "+ bf.name() +" "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                    results+="\n\n* PASS *\n\n Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "\n";
                    results+="Details: " + ex.toString() +"\n\n";
                    //Remove bitfield so system can throw exception for next invalid bitfiled
                    ((WriteReadDataCmd)wrCmd.getCommand()).removeReadDataField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(1000);
                }
            }
        }

        appendMessage("------Testing Read/Write Access for Supported READ-ONLY Bitfields------<br><br>"); //to store results of test

        results+="------Testing Read/Write Access for Supported READ-ONLY Bitfields------\n\n"; //to store results of test

        //Loop through all readonly supported fields
        for(BitFieldId b: supportedRdBitFields)
        {
            //if it's readonly, try to write to it and verify exception is thrown

            try {
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(b, 10);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(1000);
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> NO Exception thrown when trying to write read-only bitfield:  " + b.name() + "<br>");

                results+="\n* FAIL *\n\n NO Exception thrown when trying to write read-only bitfield:  " + b.name() + "\n";

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br> Exception thrown when trying to write read-only bitfield:  "+b.name()+ "<br>");
                appendMessage(" Details: " + ex.getMessage() +"<br><br>");

                results+="\n\n* PASS *\n\n Exception thrown when trying to write read-only bitfield:  "+b.name()+ "\n";
                results+=" Details: " + ex.getMessage() +"\n\n";

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

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of  "+timeOfTest+"  secs \n";
        return results;
    }
    //-------------------------------------------------------------------------------//
    //   Test input values for each bitfield - testBitfieldValuesValidation()        //
    //                                                                               //
    //      - Send invalid values to each bitfield and verify exception is thrown    //
    //      - Send valid values and then read them to verify it                      //
    //-------------------------------------------------------------------------------//

    /**
     * Test input values for each bitfield
     *  - Send invalid values to each bitfield and verify either an exception is thrown or value is not written
     *  - Send valid values and then read them to verify writing operation successfull
     * @return
     * @throws Exception
     */
    public String testBitfieldValuesValidation() throws Exception
    {
        String results = "";
        System.out.println("NOW RUNNING READ/WRITE ACCESS FOR SUPPORTED BITFIELDS...<br>");
        appendMessage("------Testing Read/Write Access with valid values for Supported WRITE/READ Bitfields------<br><br>"); //to store results of test
       
        results+="------Testing Read/Write Access with valid values for Supported WRITE/READ Bitfields------\n\n"; //to store results of test

        ArrayList<BitFieldId> supportedWrBitFields = new ArrayList<BitFieldId>(MainDevice.getInfo().getSupportedWriteBitfields());

        double   valueToWrite;
        Object defaultValue;
        Object temp;

        //Loop through all read/write supported fields, write invalid value and verify read value from brainboard is the default value
        // Then write a valid value and verify it by reading it from brainboard


        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        for (BitFieldId bf : supportedWrBitFields) {
            //All Write/Read bitfields are: KPH, GRADE, RESISTANCE, FAN_SPEED,VOLUME, WORKOUT_MODE, AUDIO_SOURCE, WORKOUT, AGE, WEIGHT, GEARS
            //TRANS_MAX, BV_VOLUME, BV_FREQUENCY IDLE_TIMEOUT, PAUSE_TIMEOUT, SYSTEM_UNITS, GENDER, FIRST_NAME, LAST_NAME, IFIT_USER_NAME,
            //HEIGHT, KPH_GOAL, GRADE_GOAL, RESISTANCE_GOAL, WATT_GOAL, RPM_GOAL, DISTANCE_GOAL,PULSE_GOAL

            //TODO: Make sure to test one invalid value from below and from above valid range for each  bitfield
            switch (bf.name()) {
                case "KPH":
                    valueToWrite = -5.0;//Invalid value
                    results+=verifyBitfield(ModeId.RUNNING,bf,valueToWrite,false);
                    valueToWrite = 25.0;//Invalid value
                    results+=verifyBitfield(ModeId.RUNNING,bf,valueToWrite,false);
                    valueToWrite = 3.0;
                    results+=verifyBitfield(ModeId.RUNNING,bf,valueToWrite,true);
                    break;
                case "GRADE":
                    valueToWrite = 45.0;//Invalid value
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,false);
                    valueToWrite = -10.0;//Invalid value
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,false);
                    valueToWrite = 5.0;
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,true);
                    break;
                case "RESISTANCE":
                    break;
                case "FAN_SPEED":
                    valueToWrite = -3.0;//Invalid value
                    results+=verifyBitfield(ModeId.RUNNING,bf,valueToWrite,false);
                    valueToWrite = 12.0; //set fan speed to 15% of max
                    results+=verifyBitfield(ModeId.RUNNING,bf,valueToWrite,true);
                    break;
                case "VOLUME":
                    break;
                case "WORKOUT_MODE":
                    ((WriteReadDataCmd) wrCmd.getCommand()).addReadBitField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(1000);
                    valueToWrite = 15.0;
                    results+=verifyBitfield(ModeId.RUNNING,bf,valueToWrite,false);
                    valueToWrite = -13.0;
                    results+=verifyBitfield(ModeId.RUNNING,bf,valueToWrite,false);
                    valueToWrite = ModeId.PAUSE.getValue(); //Pause Mode
                    results+=verifyBitfield(ModeId.RUNNING,bf,valueToWrite,true);
                    break;
                case "AUDIO_SOURCE":
                    break;
                case "WORKOUT":
                    break;
                case "AGE":
                    valueToWrite = 2.0; //invalid value
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,false);
                    valueToWrite = 100; //invalid value
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,false);
                    valueToWrite = 18.0; //set age to 20 years old
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,true);
                    break;
                case "WEIGHT":
                    valueToWrite = 20.0; //invalid value
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,false);
                    valueToWrite = 200; //invalid value
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,false);
                    valueToWrite = 68.0; //set weight to 20 years old
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,true);
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
                    valueToWrite = -4.0; //set timeout to 9secs to go from pause to IDLE
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,false);
                    valueToWrite = 9.0; //set timeout to 9 secs to go from pause to IDLE
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,true);
                    break;
                case "PAUSE_TIMEOUT":
                    valueToWrite = -9.0; //set timeout to 9secs to go from pause to IDLE
                    results+=verifyBitfield(ModeId.IDLE,bf,valueToWrite,false);
                    valueToWrite = 10.0; //set timeout to 5secs to go from pause to IDLE
                    results+=verifyBitfield(ModeId.PAUSE,bf,valueToWrite,true);
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

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        return results;
    }

    /**
     * Helper function for {@link #testBitfieldValuesValidation()}
     * @param modeId the right mode id to be able to use bitfield
     * @param bitFieldId the Id of the bitfield being tested
     * @param valueToWrite the value to write to the bitfield
     * @param validValue true if value to write is valid, false otherwise
     * @throws InvalidCommandException
     * @throws InvalidBitFieldException
     */
    private String verifyBitfield(ModeId modeId, BitFieldId bitFieldId, double valueToWrite, boolean validValue) throws InvalidCommandException, InvalidBitFieldException {
        String results = "";
        long time=1000;

        try {
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, modeId);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(bitFieldId, valueToWrite);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            ((WriteReadDataCmd) wrCmd.getCommand()).addReadBitField(bitFieldId);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if(validValue) {
            appendMessage("<br>using VALID value "+ valueToWrite);
           
            results+="\nusing VALID value "+ valueToWrite;
            if (hCmd.getValue(bitFieldId) == valueToWrite) {
                appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br> value " + hCmd.toString() + " read from brainboard matches value " + valueToWrite + " written to bitfield " + bitFieldId.name() + "<br>");
               
                results+="\n\n* PASS *\n\n value " + hCmd.toString() + " read from brainboard matches value " + valueToWrite + " written to bitfield " + bitFieldId.name() + "\n";
            
            } else {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> value " + hCmd.toString() + " read from brainboard DOESN'T match value " + valueToWrite + " written to bitfield " + bitFieldId.name() + "<br>");
               
                results+="\n* FAIL *\n\n value " + hCmd.toString() + " read from brainboard DOESN'T match value " + valueToWrite + " written to bitfield " + bitFieldId.name() + "\n";
            }
        }
        else
        { appendMessage("<br>using INVALID value "+ valueToWrite);
            
            results+="\nusing INVALID value "+ valueToWrite;

            if (hCmd.getValue(bitFieldId) == valueToWrite) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> invalid value " + hCmd.toString() + " read from brainboard should have not been written for bitfield " + bitFieldId.name() + "<br>");
                
                results+="\n* FAIL *\n\n invalid value " + hCmd.toString() + " read from brainboard should have not been written for bitfield " + bitFieldId.name() + "\n";

            } else {
                appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br> invalid value " + valueToWrite + " was not written to brainboard for bitfield " + bitFieldId.name()+" value "+hCmd.toString() + " was written instead<br>");
                results+="\n\n* PASS *\n\n invalid value " + valueToWrite + " was not written to brainboard for bitfield " + bitFieldId.name()+" current value is "+hCmd.toString() + " \n";

            }
        }
        try {
            ((WriteReadDataCmd) wrCmd.getCommand()).removeReadDataField(bitFieldId);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    return results;
    }

    /**
     * Runs any tests related to workout modes (RUNNING, IDLE, etc..). It is comprised of three sub-tests:
     *  1. Tests valid transitions between modes
     *  2. Tests invalid transitions between modes
     *  3. Tests actions (changing bitfield values) allowed on each mode
     * @return text log of test results
     * @throws Exception
     */
    public String testModes() throws Exception {

        String results="";
        System.out.println("**************** MODES TEST ****************");
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();

        appendMessage("<br><br>----------------------------MODE TEST RESULTS----------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------------MODE TEST RESULTS----------------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        ModeId [] Modes;

        Modes = new ModeId[]{ ModeId.IDLE, ModeId.DEBUG, ModeId.IDLE, ModeId.LOG, ModeId.IDLE, ModeId.MAINTENANCE, ModeId.IDLE,
                                      ModeId.RUNNING,ModeId.PAUSE,ModeId.RUNNING, ModeId.PAUSE, ModeId.RESULTS, ModeId.IDLE };// run both of previous cases if nothing is specified
        appendMessage("<br><br>----------------------------TESTING VALID MODE TRANSITIONS----------------------------<br><br>");
        results+="\n\n----------------------------TESTING VALID MODE TRANSITIONS----------------------------\n\n";
        results+=runModesTest(Modes, wrCmd, true);

        appendMessage("<br><br>----------------------------TESTING INVALID MODE TRANSITIONS----------------------------<br><br>");
        results+="\n\n----------------------------TESTING INVALID MODE TRANSITIONS----------------------------\n\n";
        Modes = null; //Initialized inside the Method
        results+=runModesTest(Modes, wrCmd, false);

        appendMessage("<br><br>----------------------------TESTING BITFIELDS FUNCTIONS per MODE----------------------------<br><br>");
        results+="\n\n----------------------------TESTING BITFIELDS BITFIELDS FUNCTIONS per MODE----------------------------\n\n";


//        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
//        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//        Thread.sleep(1000);
//
//        appendMessage("Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
//        appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");
//
//        results += "Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
//        results += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "\n\n";
//        appendMessage("Speed before setting it to 4 in IDLE: "+hCmd.getSpeed()+"<br>");
//        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, 16);
//        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//        Thread.sleep(1000);
//        appendMessage("Speed after setting it to 4 in IDLE: "+hCmd.getSpeed()+"<br>");
//
//
//
//        appendMessage("Status of setting speed "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
//
//        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.DEBUG);
//        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//        Thread.sleep(1000);
//
//        appendMessage("Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
//        appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");
//
//        results += "Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
//        results += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "\n\n";
//
//
//        while(hCmd.getMode()==ModeId.DEBUG)
//        {
//
//        }
        Modes = new ModeId[]{ModeId.IDLE,ModeId.RUNNING,ModeId.PAUSE, ModeId.RESULTS, ModeId.IDLE,
                ModeId.DEBUG, ModeId.IDLE, ModeId.LOG, ModeId.IDLE, ModeId.MAINTENANCE};
        results+=runBitfieldsAccessInModes(Modes);


        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");

        results += "Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        results += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "\n\n";
        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;
        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";

        return results;
    }

    /**
     * Helper function of {@link #testModes()}. Tests the following:
     *  1. Valid transitions between modes
     *  2. Invalid transitions between modes
     * @param modes array of all modes to be tested
     * @param wrCmd command to be used to write bitfield values
     * @param validMode if true test 1. is performed, 2. if false
     * @return text log of test results
     */
    private String runModesTest(ModeId[] modes, FecpCommand wrCmd, boolean validMode) {

        String results="";
        ModeId transitionMode = ModeId.UNKNOWN; // Mode to transition to the next state to be tested
        ModeId currentMode;
        boolean testFailed = false;
        ArrayList<String> failedTransitions = new ArrayList<String>(); // To hold failedTransitions
        String failedTransitionStr = ""; // Value to add to the failed Transitions array
        int count = 0; // To determine when all modes have been tested
        int idleCount = 0; //To Count the times we visit idle case
      /*Count that indicates all invalid transitions were tested.
      If all invalid transitions pass their tests, this number is 7, if not if will increment by 2 everytime a transition fails*/
        int doneCount = 7;
        //This array will help get back to the mode currently being tested once a transition has failed
        ModeId [] validTransitionsFlow = new ModeId[]{ModeId.IDLE,ModeId.RUNNING,ModeId.PAUSE,ModeId.RESULTS, ModeId.IDLE,ModeId.DEBUG, ModeId.IDLE,ModeId.LOG, ModeId.IDLE,ModeId.MAINTENANCE};

        if(validMode) {
            //Testing valid transitions
//            currentMode = hCmd.getMode();
//            if(currentMode!=ModeId.IDLE)
//            {
//                for (int x = 0; x<validTransitionsFlow.length; x++)
//                {
//                    try {
//                    ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, validTransitionsFlow[x].getValue());
//                        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//                        Thread.sleep(1000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    if (hCmd.getMode() == ModeId.IDLE)
//                    {
//                        break;
//                    }
//                }
//            }
            try {
            ((WriteReadDataCmd) wrCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < modes.length; i++) {
                try {
                    currentMode = hCmd.getMode();
                    ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, modes[i].getValue());
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(1000);

                    appendMessage("Status of changing mode to "+modes[i].name()+": "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                    appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");

                    results += "Status of changing mode to "+modes[i].name()+": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                    results += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "\n\n";

                    if (hCmd.getMode().getValue() == modes[i].getValue()) {
                        appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>Mode succesfully transitioned from "+currentMode+" to: "+hCmd.getMode()+"<br>");

                        results += "\n* PASS *\n\nMode succesfully transitioned from "+currentMode+" to: "+hCmd.getMode()+"\n";
                    } else {
                        appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>Mode DID NOT transition from "+currentMode+" to: "+hCmd.getMode()+"<br>");

                        results += "\n* FAIL *\n\nMode DID NOT transition from "+currentMode+" to: "+modes[i].name()+"\n";
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        else
        {

            // Testing invalid transitions
            //ISSUES FOUND: Mode can do invalid transitions RUNNING->RESULTS, PAUSE->IDLE
            do {
                currentMode = hCmd.getMode();
                switch (currentMode.name()) {
                    case "DEBUG":
                        transitionMode = ModeId.IDLE;
                        modes = new ModeId[]{ModeId.RUNNING, ModeId.PAUSE, ModeId.RESULTS,ModeId.LOG,ModeId.MAINTENANCE};
                        count++;
                        break;
                    case "MAINTENANCE":
                        transitionMode = ModeId.IDLE;
                        modes = new ModeId[]{ModeId.RUNNING, ModeId.PAUSE, ModeId.RESULTS,ModeId.DEBUG,ModeId.LOG};
                        count++;
                        break;
                    case "LOG":
                        transitionMode = ModeId.IDLE;
                        modes = new ModeId[]{ModeId.RUNNING, ModeId.PAUSE, ModeId.RESULTS,ModeId.DEBUG,ModeId.MAINTENANCE};
                        count++;
                        break;
                    case "RUNNING":
                        transitionMode = ModeId.PAUSE;
                        modes = new ModeId[]{ModeId.RESULTS,ModeId.IDLE,ModeId.DEBUG, ModeId.LOG, ModeId.MAINTENANCE};
                        count++;
                        break;
                    case "PAUSE":
                        transitionMode = ModeId.RESULTS;
                        modes = new ModeId[]{ModeId.IDLE, ModeId.DEBUG, ModeId.LOG, ModeId.MAINTENANCE};
                        count++;
                        break;
                    case "RESULTS":
                        transitionMode = ModeId.IDLE;
                        modes = new ModeId[]{ModeId.RUNNING, ModeId.PAUSE, ModeId.DEBUG, ModeId.LOG, ModeId.MAINTENANCE};
                        count++;
                        break;
                    case "IDLE":
                        idleCount++;
                        if(idleCount==1 ){
                            transitionMode = ModeId.RUNNING;
                        }
                        else
                        {
                            switch (idleCount)
                            {
                                case 2:
                                    transitionMode = ModeId.DEBUG;
                                    break;
                                case 3:
                                    transitionMode = ModeId.LOG;
                                    break;
                                case 4:
                                    transitionMode = ModeId.MAINTENANCE;
                                    break;
                            }
                            try {
                                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, transitionMode.getValue());
                                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                Thread.sleep(1000);

                                appendMessage("Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                                appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");

                                results += "Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                                results += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "\n\n";
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            continue; // Skip to the next run of the while loop
                        }
                        modes = new ModeId[]{ModeId.PAUSE, ModeId.RESULTS};
                        count++;
                        break;
                }
                appendMessage("<br><font color = #80C0FF> ********Testing invalid transitions for mode: "+currentMode+"********</font><br><br>");
                results+="\nTesting invalid transitions for mode: "+currentMode+"\n\n";
                for(int i = 0; i<modes.length; i++) // Go through each invalid mode and verify transition to them is not possible
                {
                    if (!failedTransitions.contains(currentMode.name() + modes[i].name())) // If this transition failed, don't try it again to avoid circular dependencies
                    {
                        try {
                            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, modes[i].getValue());
                            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                            Thread.sleep(1000);

                            appendMessage("Status of sending command to change mode to "+modes[i].name()+" "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                            appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");

                            results += "Status of sending command to change mode to "+modes[i].name()+" " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                            results += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "\n\n";

                            if (hCmd.getMode() != modes[i]) {
                                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br> Mode " + currentMode + " did not transition to invalid mode " + modes[i] + " <br>");

                                results += "\n* PASS *\n\n Mode " + currentMode + " did not transition to invalid mode " + modes[i] + "\n";
                                testFailed = false;
                            } else {
                                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>Mode " + currentMode + " transitioned to invalid mode " + modes[i] + "<br>");

                                results += "\n* FAIL *\n\n Mode " + currentMode + " transitioned to invalid mode  " + modes[i] + "\n";
                                testFailed = true;

                                // Try to get back to mode being tested before transition failed
                                for (int x = 0; x<validTransitionsFlow.length; x++)
                                {
                                    ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, validTransitionsFlow[x].getValue());
                                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                    Thread.sleep(1000);
                                    if (hCmd.getMode() == currentMode)
                                    {
                                        break;
                                    }
                                }
                                failedTransitionStr = currentMode.name() + modes[i].name();
                                failedTransitions.add(failedTransitionStr);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
                /*Now switch to the next mode to test invalid transitions for it. */

                try {
                    ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, transitionMode.getValue());
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(1000);

                    appendMessage("Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                    appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");

                    results += "Status of sending mode command: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                    results += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "\n\n";
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } while(count<7); // Do until all mode have been tested
        }

        return results;
    }

    /**
     * Helper function of {@link #testModes()} to test actions (changing bitfield values) allowed on each mode
     * Goes through every bitfield on every mode
     * @param modes the modes to be tested
     * @return text log of results
     * @throws Exception
     */

    private String runBitfieldsAccessInModes(ModeId [] modes) throws Exception {
        ArrayList<BitFieldId> supportedWrBitFields = new ArrayList<BitFieldId>(MainDevice.getInfo().getSupportedWriteBitfields());
        String results = "";
        Object expectedValue,valueToWrite;

        Random rand = new Random();

        //Go through each mode and verify supported bitfield access
        for(int i = 0; i<modes.length; i++) {
            try {
                //set the mode
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, modes[i].getValue());
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(1000);
                appendMessage("Status of setting mode to " + modes[i].name() + " " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                results += "Status of setting mode to " + modes[i].name() + " " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                if(modes[i].name()=="IDLE" && i!=0)
                {
                    continue; // If idle has already been tested, go to next mode because in this case
                    // IDLE is only used to transition to DEBUG, LOG, OR MAINTENANCE mode
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            appendMessage("<br><font color = #80C0FF> ********Testing mode: "+modes[i].name()+"********</font><br><br>");
            results+="\nTesting mode: "+modes[i].name()+"\n\n";
            appendMessage("Current mode is: "+hCmd.getMode()+"<br>");
            results+="Current mode is: "+hCmd.getMode()+"\n";

            for (BitFieldId bf : supportedWrBitFields) {
                //All Write/Read bitfields are: KPH, GRADE, RESISTANCE, FAN_SPEED,VOLUME, WORKOUT_MODE, AUDIO_SOURCE, WORKOUT, AGE, WEIGHT, GEARS
                //TRANS_MAX, BV_VOLUME, BV_FREQUENCY IDLE_TIMEOUT, PAUSE_TIMEOUT, SYSTEM_UNITS, GENDER, FIRST_NAME, LAST_NAME, IFIT_USER_NAME,
                //HEIGHT, KPH_GOAL, GRADE_GOAL, RESISTANCE_GOAL, WATT_GOAL, RPM_GOAL, DISTANCE_GOAL,PULSE_GOAL

                //TODO: Make sure to test one invalid value from below and from above valid range for each  bitfield
                switch (bf.name()) {
                    case "KPH":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                            case "RESULTS":
                            case "PAUSE":
                            case "LOG":
                            case "DEBUG":
                            case "MAINTENANCE":
                                valueToWrite = (double)rand.nextInt(6)+2; // Send a random value ( 2 to 6) on each mode
                                results+=testBitfieldAccessInModes(bf,false,valueToWrite,null);
                                break;
                            case "RUNNING":
                                valueToWrite = expectedValue = 4.0;
                                results+=testBitfieldAccessInModes(bf,true,valueToWrite,expectedValue);
                                break;
                        }
                        break;
                    case "GRADE":
                        //Incline can be changed in all modes, hence no switch case statement
                        valueToWrite = expectedValue = (double)rand.nextInt(11); // Write a random value (0 to 10) to incline on each mode
                        results+=testBitfieldAccessInModes(bf,true,valueToWrite,expectedValue);
                        break;
                    case "RESISTANCE":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                            case "RESULTS":
                            case "LOG":
                            case "DEBUG":
                            case "MAINTENANCE":
                                valueToWrite = (double)rand.nextInt(20)+10; // Send a random value ( 10 to 20) on each mode
                                expectedValue = 1.0;
                                results+=testBitfieldAccessInModes(bf,false,valueToWrite,expectedValue);
                                break;
                            case "RUNNING":
                            case "PAUSE":
                                valueToWrite = expectedValue = (double)rand.nextInt(20)+10; // Send a random value ( 10 to 20) on each mode
                                results+=testBitfieldAccessInModes(bf,true,valueToWrite,expectedValue);
                                break;
                        }
                        break;
                    case "FAN_SPEED":
                        //Fan Speed can be changed in all modes, hence no switch case statement
                        valueToWrite = expectedValue = (double)rand.nextInt(50)+20; // Write a random value (20 to 50) to fan speed on each mode
                        results+=testBitfieldAccessInModes(bf,true,valueToWrite,expectedValue);
                        break;
                    case "VOLUME":
                        valueToWrite = expectedValue = (double)rand.nextInt(20)+10; // Write a random value (10 to 20) to fan speed on each mode
                        results+=testBitfieldAccessInModes(bf,true,valueToWrite,expectedValue);
                        break;
                    case "AUDIO_SOURCE":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "WORKOUT":
                        switch (modes[i].name())
                        {
                            case "IDLE":
//                                valueToWrite = expectedValue = (double)rand.nextInt(13); // Write a random value (0 to 12) to workout on each mode
//                                results+=testBitfieldAccessInModes(bf,true,valueToWrite,expectedValue);
                                break;
                            case "RUNNING":
                            case "PAUSE":
                            case "LOG":
                            case "RESULTS":
                            case "DEBUG":
                            case "MAINTENANCE":
//                                valueToWrite = (double)rand.nextInt(13); // Write a random value (0 to 12) to workout on each mode
//                                results+=testBitfieldAccessInModes(bf,false,valueToWrite,null);
                                break;
                        }
                        break;
                    case "AGE":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                            case "PAUSE":
                            case "LOG":
                            case "RESULTS":
                            case "DEBUG":
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "WEIGHT":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                            case "PAUSE":
                            case "LOG":
                            case "RESULTS":
                            case "DEBUG":
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "GEARS":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "TRANS_MAX":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "BV_VOLUME":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "BV_FREQUENCY":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "IDLE_TIMEOUT":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                            case "MAINTENANCE":
                                valueToWrite = expectedValue = (double)rand.nextInt(10)+5; // Write a random value (5 to 10) to incline on each mode
                                results+=testBitfieldAccessInModes(bf,true,valueToWrite,expectedValue);
                                break;
                            case "RUNNING":
                            case "PAUSE":
                            case "LOG":
                            case "RESULTS":
                            case "DEBUG":
                                valueToWrite = expectedValue = (double)rand.nextInt(20)+11; // Write a random value (11 to 20) to incline on each mode
                                results+=testBitfieldAccessInModes(bf,false,valueToWrite,null);
                                break;
                        }
                        break;
                    case "PAUSE_TIMEOUT":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                            case "MAINTENANCE":
                                valueToWrite = expectedValue = (double)rand.nextInt(10)+5; // Write a random value (5 to 10) to incline on each mode
                                results+=testBitfieldAccessInModes(bf,true,valueToWrite,expectedValue);
                                break;
                            case "RUNNING":
                            case "PAUSE":
                            case "LOG":
                            case "RESULTS":
                            case "DEBUG":
                                valueToWrite = expectedValue = (double)rand.nextInt(30)+21; // Write a random value (21 to 30) to incline on each mode
                                results+=testBitfieldAccessInModes(bf,false,valueToWrite,null);
                                break;
                        }
                        break;
                    case "SYSTEM_UNITS":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "GENDER":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "FIRST_NAME":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "LAST_NAME":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "HEIGHT":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "KPH_GOAL":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "GRADE_GOAL":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "RESISTANCE_GOAL":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "WATT_GOAL":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "RPM_GOAL":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "DISTANCE_GOAL":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    case "PULSE_GOAL":
                        switch (modes[i].name())
                        {
                            case "IDLE":
                                break;
                            case "RUNNING":
                                break;
                            case "PAUSE":
                                break;
                            case "LOG":
                                break;
                            case "RESULTS":
                                break;
                            case "DEBUG":
                                break;
                            case "MAINTENANCE":
                                break;
                        }
                        break;
                    default:
                        break;

                }
                // mSFitSysCntrl.getFitProCntrl().removeCmd(cmd);
                // Thread.sleep(1000);

            }

        }
        return  results;
    }

    /**
     * Helper function of {@link #runBitfieldsAccessInModes(com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId[])}
     * Performs corresponding action on bitfield and validates it
     * @param bf the bitfiled being tested
     * @param canWrite true of we are suppose to write to "bf" on the current mode, else otherwise
     * @param valueToWrite the value to write to bitfield "vf"
     * @param expectedResult the expected result after writing operation performed
     * @return text log of test results
     */

    private String testBitfieldAccessInModes(BitFieldId bf, boolean canWrite, Object valueToWrite, Object expectedResult) {
        String results = "";
        long time=1000;
//        if(bf.name() =="KPH" || bf  .name() =="GRADE")
//        {
//            time = 5000;
//        }
        try {

            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(bf, valueToWrite);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of trying to write " +valueToWrite +" to bitfield "+bf+" "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of trying to write " +valueToWrite +" to bitfield "+bf+" "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
            ((WriteReadDataCmd) wrCmd.getCommand()).addReadBitField(bf);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(time);
            appendMessage("Status of adding read bitfield "+bf+" "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
            results+="Status of adding read bitfield "+bf+" "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if(canWrite) {
            appendMessage("<br>using VALID value "+ valueToWrite);

            results+="\nusing VALID value "+ valueToWrite;
            if (hCmd.getValue(bf.name()) == ((double)expectedResult)) {
                appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br> value " + hCmd.toString() + " read from brainboard matches value " + valueToWrite + " written to bitfield " + bf.name() + "<br>");

                results+="\n\n* PASS *\n\n value " + hCmd.toString() + " read from brainboard matches value " + valueToWrite + " written to bitfield " + bf.name() + "\n";

            } else {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> value " + hCmd.toString() + " read from brainboard DOESN'T match value " + valueToWrite + " written to bitfield " + bf.name() + "<br>");

                results+="\n* FAIL *\n\n value " + hCmd.toString() + " read from brainboard DOESN'T match value " + valueToWrite + " written to bitfield " + bf.name() + "\n";
            }
        }
        else
        { appendMessage("<br>using INVALID value "+ valueToWrite);

            results+="\nusing INVALID value "+ valueToWrite;

            if (hCmd.getValue(bf.name()) == ((double)valueToWrite)) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> invalid value " + hCmd.toString() + " read from brainboard should have not been written for bitfield " + bf.name() + "<br>");

                results+="\n* FAIL *\n\n invalid value " + hCmd.toString() + " read from brainboard should have not been written for bitfield " + bf.name() + "\n";

            } else {

                appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br> invalid value " + valueToWrite + " was not written to brainboard for bitfield " + bf.name()+" its current value is "+hCmd.toString() + "<br>");
                results+="\n\n* PASS *\n\n invalid value " + valueToWrite + " was not written to brainboard for bitfield " + bf.name()+" its current value is "+hCmd.toString() + "\n";

            }
        }
        try {
            ((WriteReadDataCmd) wrCmd.getCommand()).removeReadDataField(bf);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Runs all Bitfields tests
     * @return text log of test results
     * @throws Exception
     */
    @Override
    public String runAll() throws Exception {
        String results = "";

       results="";
       results+=this.testBitfieldRdWr();
       results+=this.testBitfieldValuesValidation();
       results+=this.testModes();

        return results;
    }
}
