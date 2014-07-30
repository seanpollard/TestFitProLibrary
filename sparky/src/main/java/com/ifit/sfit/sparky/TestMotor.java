package com.ifit.sfit.sparky;

import com.ifit.sfit.sparky.tests.BaseTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

import java.nio.ByteBuffer;
import java.util.Calendar;


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
                this.rdCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 0, 100);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.KPH);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.DISTANCE);
                ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                //  ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.WEIGHT);
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
        System.out.println("**************** START SPEED TEST ****************");

        //outline for code support #958 **first task to automate**
        //send basic start command to start motor at on position
        //request actual speed from device to make sure it is connected and moving
        //read speed received into this code which should be target speed
        //check against constant variable of 1.0 mph
        //make sure formatting is right for verification for english or metric units
        double actualspeed = 0;

        appendMessage("<br><br>----------------------START SPEED TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        //print out the current mode
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");


        currentSpeed = hCmd.getSpeed();
        appendMessage("The current speed is: " + currentSpeed + "<br>");


        //Set Mode to Running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("<br>Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

        appendMessage("<br>The current mode is " + hCmd.getMode() + "<br>");
        actualspeed = hCmd.getActualSpeed();
        appendMessage("The actual speed is: " + actualspeed + "<br>");

        Thread.sleep(5000); // Give the motor 5 secs to reach the desired speed

        currentSpeed = hCmd.getSpeed();
        actualspeed = hCmd.getActualSpeed();


        appendMessage("The current speed after 5 seconds running is: " + currentSpeed + "<br>");
        appendMessage("The ACTUAL speed after 5 seconds running is: " + actualspeed + "<br>");

        if (currentSpeed == 1.0) {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("Speed correctly started at 1.0 mph (2.0 kph)<br>");
        } else {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("Speed should have started at 1.0 mph (2.0 kph), but is actually set at " + currentSpeed + " kph<br>");
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

        return res;

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
        appendMessage( "<br>----------------------------DISTANCE TEST---------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");


        appendMessage("The status of reading the initial mode is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");

        //set mode to running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("The status of changing mode to RUNNING is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");

        //Set the motor speed to 10 KPH
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, 10);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("The status of setting speed to 10kph: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        appendMessage("Now wait 1.5 mins...<br>");

        //wait 1.5 minutes
        Thread.sleep(90000);

        distance = hCmd.getDistance();
        appendMessage("The distance was " + distance + "<br>");
        appendMessage("The status of reading the distance is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        //set mode to back to idle to end the test.
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("The status of changing mode to PAUSE is: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");

        //set mode back to Results
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //set mode back to IDLE to reset running time
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //5% tolerance for passing: 250 meters = +/- 12.5
        if ((distance < 234.5) || (distance > 272.5)) {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The distance was off by " + (distance - 250) + "<br><br>");
        } else
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The distance should be 250 meters and is " + distance + " meters which is within 5%<br><br>");

        //Remove all commands from the device that have a command ID = "WRITE_READ_DATA"
        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        Thread.sleep(1000);

        return res;
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
    public String testModes(String mode) throws Exception {
        System.out.println("**************** MODES TEST ****************");

        appendMessage("<br><br>----------------------------MODE TEST RESULTS----------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        int[] Modes;

        switch (mode) {
            case "IDLE":
            case "DEBUG":
            case "MAINTENANCE":
            case "LOG":
                Modes = new int[]{1, 5, 1, 6, 1, 7, 1};// idle, debug, idle, log, idle, Maintenance, idle
                appendMessage("<br><br>----------------------------IDLE/DEBUG/MAINTENANCE/LOG MODE TEST RESULTS----------------------------<br><br>");
                runModesTest(Modes, wrCmd);
                break;

            case "RUNNING":
            case "PAUSE":
            case "RESULTS":
                Modes = new int[]{2, 3, 2, 3, 4, 1};// running, pause,running, pause,results,idle
                appendMessage("<br><br>----------------------------RUNNING/PAUSE/RESULTS MODE TEST RESULTS----------------------------<br><br>");
                runModesTest(Modes, wrCmd);
                break;

            default:
                Modes = new int[]{1, 5, 1, 6, 1, 7, 1, 2, 3, 2, 3, 4, 1};// run both of previous cases if nothing is specified
                appendMessage("<br><br>----------------------------ALL MODES TEST RESULTS----------------------------<br><br>");
                runModesTest(Modes, wrCmd);
                break;
        }
       return res;
    }

    private String runModesTest(int[] modes, FecpCommand wrCmd) {

        for (int i = 0; i < modes.length; i++) {
            try {
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, modes[i]);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(1000);

                appendMessage("Status of changing mode: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                appendMessage("Current Mode is: " + hCmd.getMode() + "  and its value is  " + hCmd.getMode().getValue() + "<br><br>");

                if (hCmd.getMode().getValue() == modes[i])
                    appendMessage("This mode matches : <br><font color = #00ff00>* PASS *</font><br><br>");
                else
                    appendMessage("This mode does not match : <br><font color = #ff0000>* FAIL *</font><br><br>");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return res;
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

    public String testPauseResume() throws Exception {
        System.out.println("**************** PAUSE/RESUME TEST ****************");

        //Support #954 in Redmine
        //Turn mode to Running (mimics Start button press)
        //Set speed to 5 kph
        //set mode to Pause
        //Set mode to Running
        //Verify actual speed is 1.0 mph/2.0 kph (as of 3/12/14, the resume speed is 1.0 kph)
        double actualspeed = 0;

        appendMessage("<br><br>----------------------PAUSE/RESUME SPEED TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        //Set Mode to Running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();
        appendMessage("Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");
        appendMessage("Before setting speed to 5kph speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph <br>");

        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, 5);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);

        Thread.sleep(10000);// Give it time to motor to reach speed of 5 KPH
        appendMessage("Status of changing speed to 5 KPH: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        //Set command to read speed and verify its at 5 kph

        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();
        appendMessage("The speed is currently set at: " + currentSpeed + " kph and the actual speed is " + actualspeed + "kph (After 10 secs)<br>");

        //Set Mode to Pause
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(5000); // Give time for motor to stop seconds


        appendMessage("Status of changing mode to Pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        //print out the current mode
        appendMessage("The current mode is " + hCmd.getMode() + "<br>");
        currentSpeed = hCmd.getSpeed(); //read current speed
        actualspeed = hCmd.getActualSpeed();
        appendMessage("The speed during PAUSE is: " + currentSpeed + " kph and the actual speed is " + actualspeed + " kph<br>");

        //Set Mode to Running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(5000); // Run for 5 seconds

        appendMessage("Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

        //print out the current mode
         appendMessage("The current mode is " + hCmd.getMode() + "<br>");
        currentSpeed = hCmd.getSpeed();// Read speed again

        if (currentSpeed == 1.0) {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The speed should be 1.0 kph and it is currently set at: " + currentSpeed + " kph<br>");
        } else {
           appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
           appendMessage("The speed should be 1.0 kph, but it is currently set at: " + currentSpeed + " kph<br>");
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
        mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
        Thread.sleep(1000);

        return res;
    }

    //--------------------------------------------//
    //                                            //
    //  Testing All Speeds (in decrements of 0.1) //
    //                                            //
    //--------------------------------------------//

    /* Future tests include
    * TODO: read actual parameters from brainboard (when it becomes available on next SparkyAndroidLib release, current release is 0.0.9 as of 7/2/14)
    * TODO: Test with English units and verify it does proper conversion
    * */
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

        String currentWorkoutMode;
        String currentMode;
        final int NUM_TESTS = 1;
        double roundedJ;

        appendMessage("<br>--------------------------SPEED TEST RESULTS--------------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        currentMode = "Current Mode is: " + hCmd.getMode() + "<br>";
        appendMessage(currentMode);

        //Set the Mode to Running Mode
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //Check status of changing the mode to running
        appendMessage("Status of changing mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

        currentMode = "Current Mode is: " + hCmd.getMode() + "<br>";
        appendMessage(currentMode);

        //TODO: read the min speed from the Brainboard (not implemented yet)

        //TODO: read the max speed from the Brainboard (not implemented yet)

        //Set NUM_TESTS to the number of times you want to run the test
        for (int i = 0; i < NUM_TESTS; i++) {
            for (double j = MAX_SPEED; j >= 0.5; j -= 0.1)      //Set MAX_SPEED TO the maximum speed
            {
                roundedJ = ((double) Math.round(j * 10) / 10);
                appendMessage("Sending a command for speed " + roundedJ + " to the FecpController<br>");


                //Set value for the speed
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, roundedJ);
                mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(1000);

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

                currentWorkoutMode = "Workout mode of speed " + roundedJ + " is " + hCmd.getMode() + "<br>";

                //((ModeConverter)(((WriteReadDataSts)readModeCommand.getCommand().getStatus()).getResultData().get(BitFieldId.WORKOUT_MODE))).getMode()
                appendMessage(currentWorkoutMode);
                currentSpeed = hCmd.getSpeed();
                appendMessage("Current speed is: " + currentSpeed + " and actual speed is " + hCmd.getActualSpeed() + "<br>");

                Thread.sleep(1000);

               appendMessage("<br>For Speed " + roundedJ + ":<br>");

                //with double values the rounding does not always work with 0.1 values
                if ((Math.abs(roundedJ - currentSpeed) < 0.1)) {
                    appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The speed was minimally off by " + (roundedJ - currentSpeed) + "<br><br>");
                } else {
                    appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The speed is greatly off by " + (roundedJ - currentSpeed) + "<br><br>");
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

        return res;
    }

    public String testCalories() throws Exception {

        //this test is for #964 in redmine and later #1052 when incline is implemented
        //currently not implemented by the FECP library or brainboard and will fail
        //calories depend on weight so we need to check weight also for the proper calorie calculation
        //we also need to implement the incline with calorie verification #1052
        //see TestIncline.java

        double calories, expectedCalories;
        System.out.println("NOW RUNNING CALORIES TEST<br>");
        appendMessage("<br><br>----------------------------CALORIE TEST----------------------------<br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");
        expectedCalories = 15.64;
        long time = 60; //seconds
        //read the weight value for calorie calculation
        appendMessage("<br>The current weight before setting to 83.91 kg is " + hCmd.getWeight() + "<br>");
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
        //set mode to running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //send speed
        startTimer = System.currentTimeMillis();
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, 10);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);

        //wait time
        Thread.sleep(time * 1000);
        //read calories
        calories = hCmd.getCalories();
        //set mode to back to idle to end the test.
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        stopTimer = System.currentTimeMillis() - startTimer;
        //Thread.sleep(16000);
        appendMessage("The status of the calorie command is: " + wrCmd.getCommand().getStatus().getStsId().getDescription());

        //5% tolerance for passing expect 15.20 for 1 min
        if (Math.abs(expectedCalories - calories) > 0.05) {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The calories were " + calories + " and were off by " + (calories - expectedCalories) + "<br><br>");
        } else {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>The calories should be " + expectedCalories + " and is " + calories + " which is within 5%<br><br>");
        }

        appendMessage("The calories are now " + hCmd.getCalories() + "<br>");
        appendMessage("The test took " + ((stopTimer) / 1000) + " seconds<br>");
        //stop the call back
        mFecpController.removeCmd(wrCmd);
        mFecpController.removeCmd(rdCmd);

        return res;
    }

    //--------------------------------------------//
    //                                            //
    //           Testing PWM Overshoot            //
    //                                            //
    //--------------------------------------------//
    /*Futures test includes
    * TODO: Test with different speeds and use other non-running modes (PAUSE, RESULTS, etc...) before pressing start again
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

        double maxSpeed = 16.0; //TODO: Hardcoded Max Speed until it is implemented on the Brainboard (3/25/14)
        double[] currentSpeeds = new double[30];
        boolean alwaysLess = true;

        appendMessage("<br><br>----------------------PWM OVERSHOOT TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        //Set Mode to Running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("Status of changing mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        appendMessage("About to set speed to MAX (16 Kph)... Current speed is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");

        //Set speed to Max Speed
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, maxSpeed);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of changing speed to 16kph " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        appendMessage("current speed after setting it to max and before waiting 22 secs is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        Thread.sleep(22000);    //Wait for Max Speed
        appendMessage("current speed after 22 secs and before going into pause mode is: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");


        //Set Mode to PAUSE(Simulate Stop Key)
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of Setting mode to PAUSE (simulate Stop key ): " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        appendMessage("Current Speed During PAUSE: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");
        appendMessage("About to try to set speed to Max... This action SHOULD NOT CHANGE the speed!<br> ");
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, maxSpeed);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Command Status after trying to set speed to max: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Speed after attempting to set max speed during pause mode: " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");

        //Set Mode to RUNNING (Simulate Start Key)
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("Status of sending changing mode to running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Current Mode is: " + hCmd.getMode() + "<br>");
        appendMessage("Current Speed " + hCmd.getSpeed() + " actual speed is " + hCmd.getActualSpeed() + "<br>");

        //TODO: Actual Speed is implemented as of 7/22/14 but the value we read is not yet accurate
        for (int i = 0; i < 30; i++) {
            currentSpeeds[i] = hCmd.getSpeed();
//            currentSpeeds[i] = hCmd.getActualSpeed();
            Thread.sleep(100);
        }
        //TODO: This logic on the if statement needs testing for this loop with actual speed
        for (int i = 0; i < currentSpeeds.length - 1; i++) {
            if (currentSpeeds[i] >= currentSpeeds[i + 1]) {
                alwaysLess = true;
            } else {
                alwaysLess = false;
                appendMessage("The current speed is more than the previous " + currentSpeeds[i + 1] + " with " + currentSpeeds[i] + "<br>");
                break;
            }
        }

        if (alwaysLess) {
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The Speed did not increase once the Start button was pressed<br>");
        } else {
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The Speed increased or the Speed never changed from zero<br>");
        }

        ///Set Mode to Pause
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

        return res;
    }

    @Override
    public String runAll() {
        String allMotorTestResults = "";
        try {
            this.testDistance();
            this.testStartSpeed();
            this.testModes("all");
            this.testPauseResume();
            this.testPwmOvershoot();
            this.testSpeedController();
            this.testCalories();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

}
