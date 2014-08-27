package com.ifit.sfit.sparky;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


/**
 * Created by jc.almonte on 6/25/14.
 * Class for motor type tests
 * Used sean's TestMotor code and adapted it to work with SparkyAndroidLib 0.0.9
 */
public class TestMotor extends TestCommons implements TestAll {
    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private BaseTest mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;
    private double currentSpeed; // Current motor speed
    private FecpCommand wrCmd;
    private FecpCommand rdCmd;


    //To hold time lapsed
    private long stopTimer = 0;
    private long startTimer = 0;


    //TestMotor constructor. Receive needed parameters from main activity(TestApp) to initialize controller
    public TestMotor(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
        System.out.println("^^^^^^^^^^^ MOTOR TESTS ^^^^^^^^^^^");
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            this.hCmd = new HandleCmd(this.mAct);// Init handlers

            ByteBuffer secretKey = ByteBuffer.allocate(32);
            for (int i = 0; i < 32; i++) {
                secretKey.put((byte) i);
            }
            try {
                //unlock the system
                this.mSFitSysCntrl.getFitProCntrl().unlockSystem(secretKey);
                Thread.sleep(1000);
                //Get current system device
                MainDevice = this.mFecpController.getSysDev();
                this.wrCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd);
                ((WriteReadDataCmd) wrCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(1000);

                this.rdCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 0, 100);
               ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.MAX_KPH);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.GRADE);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.DISTANCE);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                //  ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.WEIGHT);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.KPH);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.CALORIES);
                mSFitSysCntrl.getFitProCntrl().addCmd(rdCmd);
                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
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

//        //outline for code support #958 **first task to automate**
//        //send basic start command to start motor at on position
//        //request actual speed from device to make sure it is connected and moving
//        //read speed received into this code which should be target speed
//        //check against constant variable of 1.0 mph
//        //make sure formatting is right for verification for english or metric units


        String results ="";
        double actualspeed = 0;
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();

        appendMessage("<br><br>----------------------START SPEED TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------START SPEED TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        //print out the current mode
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");

        results+="The current mode is " + hCmd.getMode() + "\n";


        currentSpeed = hCmd.getSpeed();
        appendMessage("The current speed is: " + currentSpeed + "<br>");

        results+="The current speed is: " + currentSpeed + "\n";

        appendMessage("Now setting mode to RUNNING...<br>");

        results+="Now setting mode to RUNNING...\n";

        //Set Mode to Running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(2000);

        appendMessage("<br>Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

        results+="\nStatus of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

        appendMessage("<br>The current mode is " + hCmd.getMode() + "<br>");

        results+="\nThe current mode is " + hCmd.getMode() + "\n";

        actualspeed = hCmd.getActualSpeed();
        appendMessage("The actual speed is: " + actualspeed + "<br>");
        appendMessage("Wait 5 secs.. for motor to reach speed<br>");

        results+="The actual speed is: " + actualspeed + "\n";
        results+="Wait 5 secs.. for motor to reach speed\n";
        Thread.sleep(5000); // Give the motor 5 secs to reach the desired speed

        currentSpeed = hCmd.getSpeed();
        actualspeed = hCmd.getActualSpeed();


        appendMessage("The current speed after 5 seconds running is: " + currentSpeed + "<br>");
        appendMessage("The ACTUAL speed after 5 seconds running is: " + actualspeed + "<br>");

        results+="The current speed after 5 seconds running is: " + currentSpeed + "\n";
        results+="The ACTUAL speed after 5 seconds running is: " + actualspeed + "\n";

        if (currentSpeed == 2.0) {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("Speed correctly started at 2.0 kph<br>");

            results+="\n\n* PASS *\n\n";
            results+="Speed correctly started at 2.0 kph\n";
        } else {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("Speed should have started at 2.0 kph but is actually set at " + currentSpeed + " kph<br>");

            results+="\n\n* FAIL *\n\n";
            results+="Speed should have started at 2.0 kph but is actually set at " + currentSpeed + " kph\n";

        }

        //Set Mode to Pause
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to Results
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to IDLE to reset running time
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //Remove command
        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        Thread.sleep(1000);

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";

//        double actualSpeed = 0;
//        while(true)
//        {
//            currentSpeed = hCmd.getSpeed();
//            actualSpeed = hCmd.getActualSpeed();
//            appendMessage("Current Speed is "+currentSpeed+" actual speed is "+actualSpeed+" Max speed is "+ hCmd.getMaxSpeed()+"<br>");
//            appendMessage("Wait 5 secs... and clear screen<br>");
//            Thread.sleep(5000);
//            res = "";
//            Thread.sleep(2000);
//            if(hCmd.getIncline() == 0)
//                break;
//
//        }        appendMessage("<br>This test took a total of"+timeOfTest+"secs <br>");

        return results;

    }

    //--------------------------------------------//
    //                                            //
    //              Testing Distance              //
    //                                            //
    //--------------------------------------------//
    public String testDistance() throws Exception {
        System.out.println("**************** DISTANCE TEST ****************");

        //outline for code support #929 in redmine
        //start timer stopwatch
        //send a speed of 10 kph for a 1.5 min/km pace
        //wait 1.5 minutes
        //read distance value
        //verify distance is 250 meters

        double distance = 0;
        double setSpeed = 10;// speed to use in the test in KPH
        double expectedDistance = 250; //
        long time = 90; // time to run test in seconds (1.5 mins in this case)
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();

        String results="";

        appendMessage("<br>----------------------------DISTANCE TEST---------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");
        appendMessage("Test runs for " +time+ " seconds at a speed of "+setSpeed+" KPH. Expected distance is: "+expectedDistance+" meters\n");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");

        results+="\n----------------------------DISTANCE TEST---------------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";
        results+="Test runs for " +time+ " seconds at a speed of "+setSpeed+" KPH. Expected distance is: "+expectedDistance+" meters\n";
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        //set mode to running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("The status of changing mode to RUNNING is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");

        results+="The status of changing mode to RUNNING is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        results+="Current Mode is: " + hCmd.getMode() + "\n";

        //Set the motor speed to 10 KPH
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, setSpeed);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("The status of setting speed to "+setSpeed+" is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        appendMessage("Now wait "+time+" seconds...<br>");

        results+="The status of setting speed to "+setSpeed+" is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        results+="Current Mode is: " + hCmd.getMode() + "\n";
        results+="Now wait "+time+" seconds...\n";

        //wait time seconds
        Thread.sleep(time*1000);

        distance = hCmd.getDistance();
        appendMessage("The distance was " + distance + "<br>");
        appendMessage("The status of reading the distance is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        results+="The distance was " + distance + "\n";
        results+="The status of reading the distance is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";

        //set mode to back to idle to end the test.
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("The status of changing mode to PAUSE is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");

        results+="The status of changing mode to PAUSE is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        results+="Current Mode is: " + hCmd.getMode() + "\n";


        //set mode back to Results
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to IDLE to reset running time
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //5% tolerance for passing: 250 meters = +/- 12.5
        if (Math.abs(expectedDistance-distance) > expectedDistance*0.05) {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The distance was off by " + (expectedDistance-distance) + "meters <br><br>");

            results+="\n* FAIL *\n\nThe distance was off by " + (expectedDistance-distance) + "meters \n\n";

        } else {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The distance should be " + expectedDistance + "  meters and is " + distance + " meters which is within 5%<br><br>");

            results+="\n* PASS *\n\nThe distance should be "+expectedDistance+"  meters and is " + distance + " meters which is within 5%\n\n";

        }
        //Remove all commands from the device that have a command ID = "WRITE_READ_DATA"
        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        Thread.sleep(1000);

        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";

        return results;
    }


    //--------------------------------------------//
    //                                            //
    //                Testing Mode                //
    //                                            //
    //--------------------------------------------//
    /*
    Future tests include
  //TODO: Testing commands supported on each mode
  // Done: Testing transitions between modes
  * */
    public String testModes() throws Exception {

        String results="";
        System.out.println("**************** MODES TEST ****************");
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        int currentMode = 0;

        appendMessage("<br><br>----------------------------MODE TEST RESULTS----------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------------MODE TEST RESULTS----------------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        ModeId [] Modes;

//        Modes = new ModeId[]{ ModeId.IDLE, ModeId.DEBUG, ModeId.IDLE, ModeId.LOG, ModeId.IDLE, ModeId.MAINTENANCE, ModeId.IDLE,
//                                      ModeId.RUNNING,ModeId.PAUSE,ModeId.RUNNING, ModeId.PAUSE, ModeId.RESULTS, ModeId.IDLE };// run both of previous cases if nothing is specified
//        appendMessage("<br><br>----------------------------TESTING VALID MODE TRANSITIONS----------------------------<br><br>");
//        results+="\n\n----------------------------TESTING VALID MODE TRANSITIONS----------------------------\n\n";
//        results+=runModesTest(Modes, wrCmd, true);
//
//        appendMessage("<br><br>----------------------------TESTING INVALID MODE TRANSITIONS----------------------------<br><br>");
//        results+="\n\n----------------------------TESTING INVALID MODE TRANSITIONS----------------------------\n\n";
//        Modes = null; //Initialized inside the Method
//        results+=runModesTest(Modes, wrCmd, false);

        appendMessage("<br><br>----------------------------TESTING BITFIELDS FUNCTIONS per MODE----------------------------<br><br>");
        results+="\n\n----------------------------TESTING BITFIELDS BITFIELDS FUNCTIONS per MODE----------------------------\n\n";

        mSFitSysCntrl.getFitProCntrl().removeCmd(rdCmd); // Remove read command so call back doesn't interfere with test reading
        Thread.sleep(1000);


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

        mSFitSysCntrl.getFitProCntrl().addCmd(rdCmd);
        Thread.sleep(1000);
       return results;
    }

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
            for (int i = 0; i < modes.length; i++) {
                try {
                    currentMode = hCmd.getMode();
                    ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, modes[i].getValue());
                    mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                    Thread.sleep(1000);

                    appendMessage("Status of changing mode: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                    appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");

                    results += "Status of changing mode: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                    results += "Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "\n\n";

                    if (hCmd.getMode().getValue() == modes[i].getValue()) {
                        appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>Mode succesfully transitioned from "+currentMode+" to: "+hCmd.getMode()+"<br>");

                        results += "\n* PASS *\n\nMode succesfully transitioned from "+currentMode+" to: "+hCmd.getMode()+"\n";
                    } else {
                        appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>Mode DID NOT transition from "+currentMode+" to: "+hCmd.getMode()+"<br>");

                        results += "\n* FAIL *\n\nMode DID NOT transition from "+currentMode+" to: "+hCmd.getMode()+"\n";
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

    //--------------------------------------------//
    //                                            //
    //          Testing Pause/Resume Speed        //
    //                                            //
    //--------------------------------------------//


     public String testPauseResume() throws Exception {
        System.out.println("**************** PAUSE/RESUME TEST ****************");

        //Support #954 in Redmine
        //Turn mode to Running (mimics Start button press)
        //Set speed to 5 kph
        //set mode to Pause
        //Set mode to Running
        //Verify actual speed is 1.0 mph/2.0 kph (as of 3/12/14, the resume speed is 1.0 kph)
        String results="";
        double actualspeed = 0;
        double setSpeed = 5;//speed to be used in this test
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        appendMessage("<br><br>----------------------PAUSE/RESUME SPEED TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------PAUSE/RESUME SPEED TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        //Set Mode to Running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();
        appendMessage("Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");
        appendMessage("Before setting speed to 5kph speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph <br>");

        results+="Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        results+="The current mode is " + hCmd.getMode() + "\n";
        results+="Before setting speed to 5kph speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph \n";


        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, setSpeed);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        appendMessage("Before setting speed to 5kph speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph <br>");
        appendMessage("Wait 10 secs for motor to reach speed...<br>");
        
        results+="Before setting speed to 5kph speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph \n";
        results+="Wait 10 secs for motor to reach speed...\n";
       
        Thread.sleep(10000);// Give it time to motor to reach speed of 5 KPH
        appendMessage("Status of changing speed to 5 KPH: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

        results+="Status of changing speed to 5 KPH: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        //Set command to read speed and verify its at 5 kph

        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();
        appendMessage("The speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph (After 10 secs)<br>");

        results+="The speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph (After 10 secs)\n";

        //Set Mode to Pause
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        appendMessage("Set mode to pause and give 5 secs for motor to speed down...<br>");

        results+="Set mode to pause and give 5 secs for motor to speed down...\n";

        Thread.sleep(5000); // Give time for motor to stop seconds

        appendMessage("Status of changing mode to Pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

        results+="Status of changing mode to Pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

        //print out the current mode
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");

        results+="The current mode is " + hCmd.getMode() + "\n";

        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();
        appendMessage("The speed during PAUSE is: " + currentSpeed + " kph and the actual speed is " + actualspeed + " kph<br>");

        results+="The speed during PAUSE is: " + currentSpeed + " kph and the actual speed is " + actualspeed + " kph\n";


        //Set Mode to Running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        appendMessage("Set mode to running again, for 5 secs<br>");

        results+="Set mode to running again, for 5 secs\n";
        
        Thread.sleep(5000); // Run for 5 seconds

        appendMessage("Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
       
        results+="Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

        //print out the current mode
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");

        results+="The current mode is " + hCmd.getMode() + "\n";

        currentSpeed = hCmd.getSpeed();// Read speed again

        if (currentSpeed == 2.0) {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The speed should be 2.0 kph and it is currently set at: " + currentSpeed + " kph<br>");

            results+="\n* PASS *\n\n";
            results+="The speed should be 2.0 kph and it is currently set at: " + currentSpeed + " kph\n";

        } else {
           appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
           appendMessage("The speed should be 2.0 kph, but it is currently set at: " + currentSpeed + " kph<br>");
           
           results+="\n* FAIL *\n\n";
           results+="The speed should be 2.0 kph, but it is currently set at: " + currentSpeed + " kph\n";

        }

        //Set Mode to Pause
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to Results
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to IDLE to reset running time
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("<br>The current mode is " + hCmd.getMode() + "<br>");
        results+="\nThe current mode is " + hCmd.getMode() + "\n";

        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        Thread.sleep(1000);
         timeOfTest = System.nanoTime() - startTestTimer;
         timeOfTest = timeOfTest / 1.0E09;

         appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
         results+="\nThis test took a total of "+timeOfTest+" secs \n";
        return results;
    }

    //--------------------------------------------//
    //                                            //
    //  Testing All Speeds (in decrements of 0.1) //
    //                                            //
    //--------------------------------------------//


    public String testSpeedController() throws Exception {
        System.out.println("**************** SPEED CONTROLLER TEST ****************");
        final double MAX_SPEED = 16; //hardcode the value until we can read it
        //outline for code support #927 in redmine
        //run test for treadmill & incline trainers
        //send speed command
        //validate speed was sent
        //read speed (not actual speed)
        //validate speed response is what you sent originally
        //go through entire speed range 1-15mph, for example
        String results="";
        String currentWorkoutMode;
        String currentMode;
        final int NUM_TESTS = 1;
        double roundedJ;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        double actualSpeed = 0;
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();

        appendMessage("<br>--------------------------SPEED TEST RESULTS--------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n--------------------------SPEED TEST RESULTS--------------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        currentMode = "Current Mode is: " + hCmd.getMode() + "<br>";
        appendMessage(currentMode);

        results+="Current Mode is: " + hCmd.getMode() + "\n";

        //Set the Mode to Running Mode
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //Check status of changing the mode to running
        appendMessage("Status of changing mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        
        results+="Status of changing mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

        currentMode = "Current Mode is: " + hCmd.getMode() + "<br>";
        appendMessage(currentMode);
        
        results+="Current Mode is: " + hCmd.getMode() + "\n";


        //TODO: read the min speed from the Brainboard (not implemented yet)

        //TODO: read the max speed from the Brainboard (not implemented yet)

        //Set NUM_TESTS to the number of times you want to run the test
        for (int i = 0; i < NUM_TESTS; i++) {
            for (double j = MAX_SPEED; j >= 0.5; j -= 0.1)      //Set MAX_SPEED TO the maximum speed
            {
                roundedJ = ((double) Math.round(j * 10) / 10);
                appendMessage("Sending a command for speed " + roundedJ + " to the FecpController<br>");
                
                results+="Sending a command for speed " + roundedJ + " to the FecpController\n";


                //Set value for the speed
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, roundedJ);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(1000);

/* THIS PART WILL BE UNCOMMENTED ONCE ACTUAL SPEED IS ACCURATE
                startime= System.nanoTime();
                do
                {
                    actualSpeed = hCmd.getActualSpeed();
                    Thread.sleep(300);
                    appendMessage("Current Speed is: " + actualSpeed+ " goal: " + j+" time elapsed: "+seconds+"<br>");
                    results+="Current Speed is: " + actualSpeed+ " goal: " + j+" time elapsed: "+seconds+"\n";
                    elapsedTime = System.nanoTime() - startime;
                    seconds = elapsedTime / 1.0E09;
                } while(j!=actualSpeed && seconds < 20);//Do while the incline hasn't reached its point yet or took more than 20 secs

*/
                if (roundedJ == 0) {
                    Thread.sleep(3000);
                } else if (roundedJ < MAX_SPEED && roundedJ > 0) {
                    Thread.sleep(2000);
                } else if (roundedJ == MAX_SPEED) {
                    Thread.sleep((long) (j) * 1000 * 2); //Typecast j (speed) to a long to delay time for double the time based on the speed
                    //Ex: delay for 12 kph should be 24 seconds (24000 milliseconds)
                }

                //Check status of the command to send the speed
                appendMessage("Status of sending speed " + roundedJ + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
               
                results+="Status of sending speed " + roundedJ + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

                currentWorkoutMode = "Workout mode of speed " + roundedJ + " is " + hCmd.getMode() + "<br>";

                //((ModeConverter)(((WriteReadDataSts)readModeCommand.getCommand().getStatus()).getResultData().get(BitFieldId.WORKOUT_MODE))).getMode()
                appendMessage(currentWorkoutMode);
               
                results+="Workout mode of speed " + roundedJ + " is " + hCmd.getMode() + "\n";
                currentSpeed = hCmd.getSpeed();
                appendMessage("Current speed is: " + currentSpeed + " and actual speed is " + hCmd.getActualSpeed() + "<br>");

                results+="Current speed is: " + currentSpeed + " and actual speed is " + hCmd.getActualSpeed() + "\n";

                Thread.sleep(1000);

                appendMessage("<br>For Speed " + roundedJ + ":<br>");

                results+="\nFor Speed " + roundedJ + ":\n";

                //with double values the rounding does not always work with 0.1 values
                if ((Math.abs(roundedJ - currentSpeed) < 0.2)) {
                    appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The speed was minimally off by " + (roundedJ - currentSpeed) + "<br><br>");
                   
                    results+="\n* PASS *\n\nThe speed was minimally off by " + (roundedJ - currentSpeed) + "\n\n";

                } else {
                    appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The speed is greatly off by " + (roundedJ - currentSpeed) + "<br><br>");
                    
                    results+="\n* FAIL *\n\nThe speed is greatly off by " + (roundedJ - currentSpeed) + "\n\n";

                }

                System.out.println("Current Speed " + roundedJ + " (from Brainboard): " + currentSpeed + " actual speed is " + hCmd.getActualSpeed());
            }
            //Set Mode to Pause
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);

            //set mode back to Results
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);

            //set mode back to IDLE to reset running time
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);
            //Remove all commands from the device that have a command ID = "WRITE_READ_DATA"
            mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
            Thread.sleep(1000);

        }
        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        return results;
    }

    public String testCalories() throws Exception {

        //this test is for #964 in redmine and later #1052 when incline is implemented
        //currently not implemented by the FECP library or brainboard and will fail
        //calories depend on weight so we need to check weight also for the proper calorie calculation
        //we also need to implement the incline with calorie verification #1052
        //see TestIncline.java
        String results="";
        double calories = 0;
        double expectedCalories = 15.64;
        double setSpeed = 10; // Speed in Kph
        long time = 60; //seconds
        double incline = 0;// Incline in % grade
        double weight = 83.91; //weight in Kgs
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();

        System.out.println("NOW RUNNING CALORIES TEST<br>");
        appendMessage("<br><br>----------------------------CALORIE TEST----------------------------<br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");
        appendMessage(expectedCalories+" calories are expected at speed of "+setSpeed+ ", incline of "+incline+", time of "+time+" seconds and weight of "+weight+"<br><br>");

        results+="\n\n----------------------------CALORIE TEST----------------------------\n";
        results+=Calendar.getInstance().getTime() + "\n\n";
        results+=expectedCalories+" calories are expected at speed of "+setSpeed+ ", incline of "+incline+", time of "+time+" seconds and weight of "+weight+"\n\n";

        //read the weight value for calorie calculation
        appendMessage("<br>The current weight before setting to 83.91 kg is " + hCmd.getWeight() + "<br>");

        results+="\nThe current weight before setting to 83.91 kg is " + hCmd.getWeight() + "\n";

        System.out.println("The current weight before setting to 83.91 kg is " + hCmd.getWeight());
        //set the weight value for calorie calculation
//        if(hCmd.getWeight() != 83.91) {
//            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WEIGHT, 83.91);
//            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//            Thread.sleep(1000);
//            calorieResults += "<br><br>The weight is not 83.91 kg = 185 pounds so we are setting it<br><br>";
//            calorieResults += "<br><br>The status of setting the weight is " + wrCmd.getCommand().getStatus().getStsId().getDescription();
//            calorieResults += "The current weight after setting to 83.91 kg is read as " + hCmd.getWeight() + "<br>";
//        }
        System.out.println("The current weight after setting to 83.91 kg is " + hCmd.getWeight());
        //set incline to target incline for this test
        if(hCmd.getIncline()!=incline)
        {
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, incline);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);
        }
        //set mode to running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //send speed
        startTimer = System.currentTimeMillis();
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, setSpeed);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        appendMessage("Wait "+time+" secs...<br>");
        results+="Wait "+time+" secs...\n";
        //wait time
        Thread.sleep(time * 1000);
        //read calories
        calories = hCmd.getCalories();
        //set mode to back to idle to end the test.
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        stopTimer = System.currentTimeMillis() - startTimer;
        //Thread.sleep(16000);
        appendMessage("The status of the calorie command is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"\n");

        results+="The status of the calorie command is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"\n";

        //5% tolerance for passing expect 15.20 for 1 min
        if (Math.abs(expectedCalories - calories) > 0.05) {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The calories were " + calories + " and were off by " + (calories - expectedCalories) + "<br><br>");
            
            results+="\n* FAIL *\n\nThe calories were " + calories + " and were off by " + (calories - expectedCalories) + "\n\n";

        } else {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The calories should be " + expectedCalories + " and is " + calories + " which is within 5%<br><br>");
           
            results+="\n* PASS *\n\nThe calories should be " + expectedCalories + " and is " + calories + " which is within 5%\n\n";

        }

        appendMessage("The calories are now " + hCmd.getCalories() + "<br>");
        appendMessage("The test took " + ((stopTimer) / 1000) + " seconds<br>");

        results+="The calories are now " + hCmd.getCalories() + "\n";
        results+="The test took " + ((stopTimer) / 1000) + " seconds\n";
        //stop the call back
        mFecpController.removeCmd(wrCmd);
        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of "+timeOfTest+" secs \n";
        return results;
    }

    public String testCals() throws Exception {
        /*
        * Calories Formula
        *
        *   Parameters:
        *       M = Mass (kg)
        *       S = Speed (m/s)
        *       G = Percent grade (m/m)
        *       T = time spent at that pace and grade (sec)
        *       VO2 = Volume of oxygeb consumed (ml/kg/sec)
        *       n1 = Constant
        *       n2 = Constant
        *                                               | 0.1, S < 1.8 m/s              | 1.8, S < 1.8 m/s
        *   VO2 = n1*S + n2*S*G + 0.583333   where n1 = |                      &   n2 = |
        *                                               | 0.2, S >= 1.8 m/s             | 0.9, S >= 1.8 m/s
        *
        *   kcal burned = (VO2 * M/1000) * 5T
        *
        *   kcal burned = ( (n1*S + n2*S*G + 0.583333) * M/1000) * 5T
        *
        * */


        String results="";

        //Parameters for the calories tests
        long time = 60;           //Time to be used for the test (in secs)
        double rCalories; //Calories read as result of the test
        BigDecimal  resultCalories; // rCalories formatted to 2 digits
        double caloriesPersec = 0;
        double  eCalories;  // Calories expected for the test (calculated with formula)
        BigDecimal expectedCalories;

        double [] incline ={0,5,10,15};      // Incline to be used for the test (in % grade)
                                                      //-2,0,5,10,15 % grade respectively

        double [] weight= {115,185,400}; //weight to be used for the test (in lbs)
                                                //52.16,83.91 and 181.44 kgs respectively

        double [] speed ={1.6,3.21,4.82,6.43,8.04,9.65};   // Speeds to be used for the test (in kph) ---> 1 kph = 0.28 m/s
                                                              //0.45,0.90,1.35,1.80,2.25,2.70 m/s respectively or 1,2,3,4,5,6 mph respectively
        double speedMPH = 0; // To convert to mph
        double currentSpeed = 0;
        double currentWeight = 0;
        double currentActualIncline = 0;

        double n1,n2; // Constants
        n1=n2=0;
        int wLen = weight.length;
        int iLen = incline.length;
        int sLen = speed.length;
        double [] expectedCalsArray = new double[wLen*iLen*sLen];
        double [] resultsCalsArray = new double[wLen*iLen*sLen];
        int calsIndex = 0; // Index for expectedCals results
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;



        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();


        System.out.println("NOW RUNNING CALORIES TEST<br>");
        appendMessage("<br><br>----------------------------CALORIE TEST----------------------------<br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------------CALORIE TEST----------------------------\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        // For each weight test every incline and for each incline test every speed

        for(int w = 0; w < wLen; w++)  // Weight Loop
        {
            appendMessage("<br><br>--------------WEIGHT = "+weight[w]+" lbs--------------<br>");
            results+="\n--------------WEIGHT = "+weight[w]+" lbs\n";
            currentWeight = hCmd.getWeight();
            appendMessage("Weight is currently set to "+currentWeight+" lbs--------------<br>");
            results+="Weight is currently set to "+currentWeight+" lbs\n";
        //TODO: Uncomment this part as once we have weight bitfield working again
//            if(currentWeight!= weight[w]) {
//                appendMessage("Setting weight to "+weight[w]+" kgs<br>");
//                results+="Setting weight to "+weight[w]+" kgs\n";
//
//                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WEIGHT, weight[w]);
//                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//                Thread.sleep(1000);
//                currentWeight = hCmd.getWeight();
//                appendMessage("The status of setting weight is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"<br>");
//                results+="The status of setting weight is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"\n";
//
//                appendMessage("Weight is currently set to "+currentWeight+" lbs<br>");
//                results+="Weight is currently set to "+currentWeight+" lbs\n";
//            }

                    for(int i = 0; i < iLen; i++) //Incline loop
                    {
                        currentActualIncline = hCmd.getActualIncline();
                        appendMessage("Incline is currently set to "+currentActualIncline+" %<br>");
                        results+="Incline is currently set to "+currentActualIncline+" %\n";

                        if(currentActualIncline != incline[i]) {
                            appendMessage("Setting speed to " + incline[i] + " %<br>");
                            results += "Setting speed to " + incline[i] + " %\n";

                            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, incline[i]);
                            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                            Thread.sleep(1000);

                            appendMessage("The status of setting incline is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
                            results += "The status of setting incline is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";

                            appendMessage("Waiting for incline to reach set value...<br>");
                            results += "Waiting for incline to reach set value...\n";
                            startime = System.nanoTime();
                            do {
                                currentActualIncline = hCmd.getActualIncline();
                                Thread.sleep(350);
                                appendMessage("Current Incline is: " + currentActualIncline + " goal: " + incline[i] + " time elapsed: " + seconds + "<br>");
                                results += "Current Incline is: " + currentActualIncline + " goal: " + incline[i] + " time elapsed: " + seconds + "\n";
                                elapsedTime = System.nanoTime() - startime;
                                seconds = elapsedTime / 1.0E09;
                            }
                            while (incline[i] != currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins
                        }

                        appendMessage("<br><br>--------------INCLINE = "+incline[i]+" %--------------<br>");
                        results+="\n\n--------------INCLINE = "+incline[i]+" %--------------\n";

                        for(int s = 0; s < sLen; s++) // Speed Loop
                        {

                            // Console has to be in running mode to be able to set speed
                            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
                            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                            Thread.sleep(1000);

                            appendMessage("The status of setting mode to RUNNING is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"<br>");
                            results+="The status of setting mode to RUNNING is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"\n";

                            appendMessage("<br><br>--------------SPEED = "+speed[s]+" kph--------------<br>");
                            results+="\n\n--------------SPEED = "+speed[s]+" kph--------------\n";
                            currentSpeed = hCmd.getSpeed();
                            appendMessage("Speed is currently set to "+currentSpeed+" kph<br>");
                            results+="Speed is currently set to "+currentSpeed+" kph\n";

                            if(currentSpeed != speed[s]) {
                                appendMessage("Setting speed to "+speed[s]+" kph<br>");
                                results+="Setting speed to "+speed[s]+" kph\n";

                                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, speed[s]);
                                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                Thread.sleep(1000);

                                appendMessage("The status of setting speed is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"<br>");
                                results+="The status of setting speed is: " + wrCmd.getCommand().getStatus().getStsId().getDescription()+"\n";
                                currentSpeed = hCmd.getSpeed();
                                appendMessage("Speed is currently set to "+currentSpeed+" kph<br>");
                                results+="Speed is currently set to "+currentSpeed+" kph\n";

                                speedMPH = currentSpeed*0.625; // In MPH

                                n1 = (speedMPH<4) ? 1:2;
                                n2 = (speedMPH<4) ? 18:9;

                                //                THIS PART WILL BE UNCOMMENTED ONCE ACTUAL SPEED IS ACCURATE
//                startime= System.nanoTime();
//                do
//                {
//                    actualSpeed = hCmd.getActualSpeed();
//                    Thread.sleep(350);
//                    appendMessage("Current Speed is: " + actualSpeed+ " kph goal: " + s+" kph time elapsed: "+seconds+"<br>");
//                    results+="Current Speed is: " + actualSpeed+ " kph goal: " + s+" kph time elapsed: "+seconds+"\n";
//                    elapsedTime = System.nanoTime() - startime;
//                    seconds = elapsedTime / 1.0E09;
//                } while(j!=actualSpeed && seconds < 20);//Do while the incline hasn't reached its point yet or took more than 20 secs

                                appendMessage("Now wait "+time+" secs...<br>");
                                results+="Now wait "+time+" secs...\n";
                                Thread.sleep(time*1000);
//                            appendMessage("Speed is currently set to "+hCmd.getSpeed()+" kgs<br>");
//                            results+="Speed is currently set to "+hCmd.getSpeed()+" kgs\n";
//  //set mode back to idle to stop the test
                                ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
                                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                Thread.sleep(1000);

                                currentWeight = weight[w];    //TODO: Elimiate this line once Levi adds weight bitfiled to config file

                                caloriesPersec=(currentWeight * (n1 * speedMPH * 270 + 350 + (currentActualIncline / 100 * speedMPH * 270 * n2))) / 2640000;
                                eCalories = caloriesPersec*time;
                                expectedCalories = new BigDecimal(eCalories);
                                expectedCalories = expectedCalories.setScale(2, BigDecimal.ROUND_FLOOR);

                                rCalories = hCmd.getCalories();
                                resultCalories = new BigDecimal(rCalories);
                                resultCalories = resultCalories.setScale(2, BigDecimal.ROUND_FLOOR);

                                //These values are stored for data comparison (just in case we want to do it)
                                expectedCalsArray[calsIndex] = eCalories;
                                resultsCalsArray[calsIndex] = rCalories;
                                calsIndex++;

                                if( Math.abs(eCalories-rCalories)< eCalories*0.05) // If calories are within 5% of expected, PASS
                                {
                                    appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                                    results+="\n* PASS *\n\n";

                                    appendMessage("Read calories value is: "+resultCalories+" which is within 5% tolerance of expected value "+expectedCalories+"<br>");
                                    appendMessage("Weight-->"+currentWeight+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+time+" secs<br>");
                                    results+="Read calories value is: "+resultCalories+" which is within 5% tolerance of expected value "+expectedCalories+"\n";
                                    results+="Weight-->"+currentWeight+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+time+" secs\n";
                                }
                                else
                                {
                                    appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>Calories value is:"+resultCalories+" and it should have been "+expectedCalories+" Calories were off by " + (eCalories - rCalories) + "<br><br>");
                                    results+="\n* FAIL *\n\nCalories value is:"+resultCalories+" and it should have been "+expectedCalories+" Calories were off by " + (eCalories - rCalories) + "\n\n";
                                    appendMessage("Weight-->"+currentWeight+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+time+" secs<br>");
                                    results+="Weight-->"+currentWeight+" lbs, speed-->"+currentSpeed+" kph, incline-->"+currentActualIncline+" %, time-->"+time+" secs\n";

                                }

                                //To reset calories value, system must be stopped and set to IDLE

                                ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
                                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                Thread.sleep(1000);
                                ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
                                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                                Thread.sleep(1000);

                                // Give time for incline to calibrate. This is temporary until calibration issue is solved
                                Thread.sleep(90000);

                            }
                    }

            }

        }



       // appendMessage(expectedCalories+" calories are expected at speed of "+setSpeed+ ", incline of "+incline+", time of "+time+" seconds and weight of "+weight+"<br><br>");
       // results+=expectedCalories+" calories are expected at speed of "+setSpeed+ ", incline of "+incline+", time of "+time+" seconds and weight of "+weight+"\n\n";

        return results;
    }

    //--------------------------------------------//
    //                                            //
    //           Testing PWM Overshoot            //
    //                                            //
    //--------------------------------------------//
    /*Futures test includes
    * */
    public String testPwmOvershoot() throws Exception {
        System.out.println("**************** PWM OVERSHOOT TEST ****************");

        //RedMine Support #956
        //Checklist item #39
        //Set Speed to Max Speed
        //Simulate Stop button press (Pause Mode)
        //Simulate Start button press (Running Mode)
        //Read Actual Speed to verify does not speed up
        //Set to running mode with max speed
        //Pause, then send a requesto to set max speed (while in pause mode)
        //It should ignore this request and continue to slow down til it stops
        String results="";
        double maxSpeed = 16.0; //TODO: Hardcoded because our console only reaches 15.0 KPH
        double[] actualSpeeds = new double[4];
        boolean alwaysLess = true;
        double timeOfTest = 0; //how long test took in seconds
        long startTestTimer = System.nanoTime();
        appendMessage("<br><br>----------------------PWM OVERSHOOT TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        results+="\n\n----------------------PWM OVERSHOOT TEST RESULTS----------------------\n\n";
        results+=Calendar.getInstance().getTime() + "\n\n";

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");

        results+="Current Mode is: " + hCmd.getMode() + "\n";

        //Set Mode to Running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        appendMessage("About to set speed to MAX (16 Kph)... Current speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");


        results+="Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        results+="Current Mode is: " + hCmd.getMode() + "\n";
        results+="About to set speed to MAX (16 Kph)... Current speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";

        //Set speed to Max Speed
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, maxSpeed);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of changing speed to 16kph " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        appendMessage("current speed after setting it to max and before waiting 22 secs is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        appendMessage("now waiting 22 secs...<br> ");
        
        results+="Status of changing speed to 16kph " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        results+="current speed after setting it to max and before waiting 22 secs is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";
        results+="now waiting 22 secs...\n";
        
        Thread.sleep(22000);    //Wait for Max Speed
        appendMessage("current speed after 22 secs and before going into pause mode is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");

        results+="current speed after 22 secs and before going into pause mode is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";

        actualSpeeds[0] = hCmd.getActualSpeed(); // Speed here should be 16 Kph
        appendMessage("current speed after 22 secs and before going into pause mode is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");

        results+="current speed after 22 secs and before going into pause mode is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";

        //Set Mode to PAUSE(Simulate Stop Key)
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(5000);
        actualSpeeds[1] = hCmd.getActualSpeed(); // Speed here should be decreasing

        appendMessage("Status of Setting mode to PAUSE (simulate Stop key: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        appendMessage("After aprox 5 secs in pause mode Current Speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        appendMessage("About to try to set speed to Max... This action SHOULD NOT CHANGE the speed!<br>");

        results+="Status of Setting mode to PAUSE (simulate Stop key: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        results+="Current Mode is: " + hCmd.getMode() + "\n";
        results+="After aprox 5 secs in pause mode Current Speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";
        results+="About to try to set speed to Max... This action SHOULD NOT CHANGE the speed!\n";
        
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, maxSpeed);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(4000);
        actualSpeeds[2] = hCmd.getActualSpeed(); // Speed here should be decreasing still
        appendMessage("Command Status after trying to set speed to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Speed after attempting to set max speed during pause mode: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");

        results+="Command Status after trying to set speed to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        results+="Current Speed after attempting to set max speed during pause mode: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";


        //Set Mode to RUNNING (Simulate Start Key)
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(4000);
        actualSpeeds[3] = hCmd.getActualSpeed(); // Speed here should be decreasing still

        appendMessage("Status of changing mode to running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        appendMessage("Current Speed " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");

        results+="Status of changing mode to running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        results+="Current Mode is: " + hCmd.getMode() + "\n";
        results+="Current Speed " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "\n";


        //Check that the speed was always decreasing after setting mode to pause and then running
        for (int i = 0; i < actualSpeeds.length - 2; i++) {
            if (actualSpeeds[i] >= actualSpeeds[i + 1]) {
                alwaysLess = true;
            } else {
                alwaysLess = false;
                appendMessage("The current speed is more than the previous " + actualSpeeds[i + 1] + " with " + actualSpeeds[i] + "<br>");

                results+="The current speed is more than the previous " + actualSpeeds[i + 1] + " with " + actualSpeeds[i] + "\n";

                break;
            }
        }

        if (alwaysLess) {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The Speed did not increase once the Start button was pressed<br>");

            results+="\n* PASS *\n\n";
            results+="The Speed did not increase once the Start button was pressed\n";
        } else {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The Speed increased or the Speed never changed from zero<br>");

            results+="\n* FAIL *\n\n";
            results+="The Speed increased or the Speed never changed from zero\n";
        }

        ///Set Mode to Pause
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to Results
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to IDLE
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        timeOfTest = System.nanoTime() - startTestTimer;
        timeOfTest = timeOfTest / 1.0E09;

        appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
        results+="\nThis test took a total of"+timeOfTest+"secs \n";
        return results;
    }

    @Override
    public String runAll() {
        String results="";
        try {
            results+=this.testStartSpeed();
            results+=this.testPauseResume();
            results+=this.testCalories();
            results+=this.testPwmOvershoot();
            results+=this.testDistance();
            results+=this.testSpeedController();
            //results+= this.testCals();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

}
