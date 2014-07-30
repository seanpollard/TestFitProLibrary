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
 * Created by jc.almonte on 7/14/14.
 */
public class TestIncline extends TestCommons implements TestAll {

    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private BaseTest mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    private FecpCommand wrCmd;
    private FecpCommand rdCmd;
    private String currentWorkoutMode = "";
    private double currentIncline = 0.0;
    private double actualInlcine = 0.0;
    private final int NUM_TESTS = 1;

    public TestIncline(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            hCmd = new HandleCmd(this.mAct);// Init handlers
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();
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
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MAX_GRADE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.GRADE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.TRANS_MAX);
                ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.KPH);
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
    //Testing All Inclines (in decrements of 0.5%)//
    //                                            //
    //--------------------------------------------//
    public String testInclineController() throws Exception{
        //outline for code support #928 in redmine
        //Read the Max Incline value from the brainboard
        //Read the Min Incline value from the brainboard
        //Read the TransMax value from the brainboard
        //Set the incline to Max Incline
        //Read current sent incline
        //Read actual incline
        //Check current sent incline against actual incline
        //Run the above logic for the entire range of incline values from Max Incline to Min Incline in decrements of 0.5%
    
        System.out.println("NOW RUNNING INCLINE CONTROLLER TEST<br>");

        appendMessage("<br>----------------------INCLINE CONTROLLER TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        double maxIncline;
        double minIncline;
        double currentActualIncline;
        double transMax;

        minIncline = hCmd.getMinIncline();
        maxIncline = hCmd.getMaxIncline();
        appendMessage("Min Incline is " + minIncline + "%<br>");
        System.out.println("Min Incline is " + minIncline + "%<br>");


        //If TransMax is ever set to a ridiculously high number or something, you can reset it to this more reasonable TransMax value
        //fixed but sometimes reading 92 instead of 183
        //((WriteReadDataCmd)setTransMax.getCommand()).addWriteData(BitFieldId.TRANS_MAX, 183);
        //mFecpController.addCmd(setTransMax);
        //Thread.sleep(1000);

        transMax = hCmd.getTransMax();
        appendMessage("TransMax is " + transMax + "<br><br>");
        System.out.println("TransMax is " + transMax + "%<br>");

        //--------------------------------------------------------------------------------------------------------------//
        //Run through all incline settings, going from -3% to 15% (hard-coded until min and max incline are implemented)//
        //--------------------------------------------------------------------------------------------------------------//
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        for(int i = 0; i < NUM_TESTS; i++)
        {
            //Set MAx incline harcoded to be 15% since that is our motor's max capacity.
            //This value "J" will be set to maxIncline later on when we use it on a motor with higher incline range
            for(double j = 15; j >= minIncline; j = j-0.5)
            {
                appendMessage("Sending a command for incline at " + j + "% to the FecpController<br>");

                //Set value for the incline
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, j);
               mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(50);

                //Check status of the command to send the incline
                appendMessage("Status of sending incline " + j + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
                startime= System.nanoTime();
                do
                {
                    currentActualIncline = hCmd.getActualIncline();
                    Thread.sleep(300);
                    System.out.println("Current Incline is: " + currentActualIncline+ " goal: " + j+" time elapsed: "+seconds);
                    elapsedTime = System.nanoTime() - startime;
                    seconds = elapsedTime / 1.0E09;
                } while(j!=currentActualIncline && seconds < 180);//Do while the incline hasn't reached its point yet or took more than 3 mins

                currentWorkoutMode = "Workout mode of incline at " + j + "% is " + hCmd.getMode() + "<br>";
                appendMessage(currentWorkoutMode);

                appendMessage("The last set incline is " + hCmd.getIncline() + "%<br>");
                System.out.println("The last set incline is " + hCmd.getIncline() + "%<br>");

                //Read the actual incline off of device
                actualInlcine = hCmd.getActualIncline();
                appendMessage("The actual incline is currently at: " + actualInlcine + "%<br>");
                System.out.println("The actual incline is currently at: " + actualInlcine + "%<br>");

                appendMessage("<br>For Incline at " + j + "%:<br>");

                if(j == actualInlcine)
                {
                    appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                }
                else
                {
                    appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The incline is off by " + (j - actualInlcine) + "%<br><br>");
                }
                Thread.sleep(3000);
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
        return res;
    }


// ISSUE FOUND: if incline is set to a value and stop button is pressed (or Mode is set to pause), incline keeps going which it should not
    public String testStopIncline() throws Exception{
        //Redmine Support #1182
        //Set Incline to 0 or Min Incline
        //Set Incline to Max Incline
        //Send Stop key command before the incline has reached Max
        //Read the Incline
        //Validate that the Incline is not set to the Max Incline
        //Set Incline to Max Incline
        //Set Incline to Min or 0
        //Send Stop key command before the incline has reached Min
        //Read the Incline
        //Validate that the Incline is not set to the Min Incline
        
        System.out.println("NOW RUNNING STOP INCLINE TEST<br>");

        double maxIncline;
        double minIncline;
        double maxToMinIncline1;
        double maxToMinIncline2;
        double minToMaxIncline1;
        double minToMaxIncline2;

        appendMessage("<br><br>----------------------STOP INCLINE TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");
        double currentActualIncline;
        long elapsedTime = 0, startime = 0;
        double seconds = 0;


        maxIncline = hCmd.getMaxIncline();
        appendMessage("Max Incline is " + maxIncline + "%<br>");
        System.out.println("Max Incline is " + maxIncline + "%<br>");

        minIncline = hCmd.getMinIncline();
        appendMessage("Min Incline is " + minIncline + "%<br>");
        System.out.println("MinIncline is " + minIncline + "%<br>");

        //Set Incline to Min Incline
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, minIncline);
        mFecpController.addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of setting incline to min: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        //check actual incline until value reaches minIncline
        startime = System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(300);
            System.out.println("Current Incline is: " + currentActualIncline+ " goal: " + minIncline+" time elapsed: "+seconds);
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(minIncline!=currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins


        //Set Incline to Max Incline
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 15);
        mFecpController.addCmd(wrCmd);
        Thread.sleep(7000);    //Wait for enough time so that the incline does not increase all the way to Max Incline

        //Stop
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mFecpController.addCmd(wrCmd);
        Thread.sleep(1000);

        appendMessage("Status of sending Stop key command: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        minToMaxIncline1 = hCmd.getActualIncline();
        Thread.sleep(5000);

        minToMaxIncline2 = hCmd.getMaxIncline();

        if(minToMaxIncline1 == minToMaxIncline2){
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The incline value from Min Incline to Max Incline was reset to " + minToMaxIncline1 + "%<br>");
        }
        else{
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The Incline value from Min Incline to Max Incline did not stop when the Stop button was pressed<br>");
        }

        //Set Incline to Max Incline
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 15);
        mFecpController.addCmd(wrCmd);
        Thread.sleep(1000);
        maxIncline = 15; //Our motor only reaches to max incline value of 15%
        //check actual incline until value reaches maxIncline
        startime = System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(300);
            System.out.println("Current Incline is: " + currentActualIncline+ " goal: " + maxIncline+" time elapsed: "+seconds);
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(maxIncline!=currentActualIncline && seconds < 90);//Do while the incline hasn't reached its point yet or took more than 1.5 mins


        //Set Incline to Min Incline
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, minIncline);
        mFecpController.addCmd(wrCmd);
        Thread.sleep(7000);    //Wait for enough time so that the incline does not go all the way to Min Incline

        //Stop
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mFecpController.addCmd(wrCmd);
        Thread.sleep(5000);    //Wait for enough time so that the incline does not increase all the way to Max Incline

        appendMessage("Status of sending Stop key command: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        maxToMinIncline1 = hCmd.getActualIncline();
        Thread.sleep(5000);

        maxToMinIncline2 = hCmd.getActualIncline();

        if(maxToMinIncline1 == maxToMinIncline2){
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The incline value from Max Incline to Min Incline was reset to " + maxToMinIncline1 + "%<br>");
        }
        else{
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The Incline value from Max Incline to Min Incline did not stop when the Stop button was pressed<br>");
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
        return res;
    }

    //--------------------------------------------//
    //                                            //
    //          Testing Retained Incline          //
    //                                            //
    //--------------------------------------------//
    // ISSUE FOUND: Incline always calibrates when it goes to IDLE mode after it has been in running mode.
    public String testRetainedIncline() throws Exception {
        //Redmine Support #1077
        //Set Incline to 5%
        //Set mode to Running
        //Read the incline to verify that it hasn't changed
        String retainedInclineResults;
        System.out.println("NOW RUNNING RETAINED INCLINE TEST<br>");

        appendMessage("<br><br>----------------------RETAINED INCLINE TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        double currentIncline1, currentIncline2;
        double testIncline = 5;
        double setIncline = 0;
        String currentMode;
        double currentActualIncline =0;

        currentMode = "Current Mode is: " + hCmd.getMode();
        appendMessage(currentMode + "<br>");

        //Set incline to 5% for testing the incline

        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, testIncline);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        setIncline = hCmd.getIncline();
        appendMessage("The status of setting the Incline to 5%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        //Wait for the incline motor to go to 5%
        startime= System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(300);
            System.out.println("Current Incline is: " + currentActualIncline+ " goal: " + setIncline+" time elapsed: "+seconds);
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(setIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its target point. Break the  loop if it took more than a minute to reach target incline

        currentIncline1 = hCmd.getActualIncline();

        appendMessage("The actual incline is " + currentIncline1 +" % <br>");

        //Set Mode to Running
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of setting the mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

        //wait for the motor to speed up
        Thread.sleep(3000);
        //read the mode
        currentMode = hCmd.getMode().toString();
        appendMessage("Current Mode is: " + currentMode + "<br>");
        //let the workout run for 30 sec
        Thread.sleep(30000);

        currentIncline2 = hCmd.getActualIncline();

        if(currentIncline1 == currentIncline2 && currentIncline1 == testIncline && currentMode.equals("RUNNING")){
            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The Incline went to " + testIncline + " and did not change when the mode was changed to Running<br>");
        }
        else{
            if(!currentMode.equals("RUNNING")){
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Mode didn't change to Running<br>");
            }
            if(currentIncline1 != currentIncline2) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Incline should be " + currentIncline1 + "%, but is " + currentIncline2 + "%<br>");
            }
            if(currentIncline1!= testIncline || currentIncline2 != testIncline) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("The incline did not go to " + testIncline + " %<br>");
                appendMessage("The incline was at " + currentIncline1 + " % before mode change and " + currentIncline2 + " % afterwards <br>");
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

        return res;
    }

    public String testSpeedInclineLimit() throws Exception {
        //TODO: As of 3/12/14, this functionality is not yet implemented
        //Redmine issue #953
        //Testing limits on the incline and on the speed
        //Limits are (Software Checklist 201B, #9):
        //-6% to -5% = 6 mph
        //-4.5% to 3.1% = 7 mph
        //-3% to -2.1% = 8 mph
        //-2% to -1.1% = 8.5 mph
        //-1% to -0.1% = 9 mph
        //0% to 15% = full speed (up to 12 mph)
        //Incline Trainers:
        //>15% to 25% = 8 mph
        //>25% to 40% = 6 mph
        //Club Units:
        //As long as the incline is negative, speed limit is always 10 mph

        //Test
        //Set mode to Running
        //Make sure Incline is set to 0%
        //Set speed to max speed (20 kph for now)
        //Read speed to ensure it's at 20 kph
        //Set Incline to a negative limit
        //Read speed to ensure it was lowered and matches the above limits
        //NOTE: The limit for our incline motor is -6% to 15%, so we can only test the negative limits
        double currentActualIncline = 0;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        double setIncline =0;
        double currentSpeed = 0;

        System.out.println("NOW RUNNING SPEED INCLINE LIMIT TEST<br>");

        appendMessage("<br>----------------------SPEED/INCLINE LIMITS TEST RESULTS----------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        //Set mode to running
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        appendMessage("Status of setting mode tu running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        appendMessage("current mode: "+hCmd.getMode());

        //set Incline to zero
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 0);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        currentIncline = hCmd.getIncline();
        appendMessage("Status of setting incline to zero: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        //Wait til incline reaches target value
        startime = System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(300);
            System.out.println("Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds);
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(currentIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1 mins

        //set speed to max
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, 16); // Our motor's speed limit is 16mph
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(23000); // give it 23 secs to reach max speed

        //Start incline at -1, then go to -6
        for(int i = -1; i >= -6; i--) {
            ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, i);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);

            appendMessage("Status of sending incline at " + i + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");


            currentIncline = hCmd.getIncline();

            appendMessage("Current Incline is set to " + currentIncline + "%<br>");
            //Wait til incline reaches target value
            startime = System.nanoTime();
            do
            {
                currentActualIncline = hCmd.getActualIncline();
                Thread.sleep(300);
                System.out.println("Current Incline is: " + currentActualIncline+ " goal: " + currentIncline+" time elapsed: "+seconds);
                elapsedTime = System.nanoTime() - startime;
                seconds = elapsedTime / 1.0E09;
            } while(currentIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1 mins


            currentSpeed = hCmd.getSpeed();


            //0% to 15% = 12 mph
            if( ( i<= 0 && i >= 15)  && currentSpeed == 12.0) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if (( i<= 0 && i >= 15) && currentSpeed != 12.0) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 12 MPH, but it is " + currentSpeed + " mph<br>");
            }

            //15.5% to 25% = 8 mph
            if( ( i<= 15.5 && i >= 25)  && currentSpeed == 8.0) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if (( i<= 15.5 && i >= 25) && currentSpeed !=8.0) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 8 MPH, but it is " + currentSpeed + " mph<br>");
            }

            //25.5% to 40% = 6 mph
            if( ( i<= 25.5 && i >= 40)  && currentSpeed == 6) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if (( i<= 25.5 && i >= 40) && currentSpeed != 6) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 6 MPH, but it is " + currentSpeed + " mph<br>");
            }

            //-1% to -0.1% = 9 mph
            if( ( i<= 0 && i >= -1)  && currentSpeed == 9.0) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if (( i<= 0 && i >= -1) && currentSpeed != 9.0) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 9 MPH, but it is " + currentSpeed + " mph<br>");
            }

            //-2% to -1.1% = 8.5 mph
            if((i <= -1 && i >= -2)  && currentSpeed == 8.5) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if ( (i<= -1 && i >= -2) && currentSpeed != 8.5) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 8.5 MPH, but it is " + currentSpeed + " mph<br>");
            }

            //-3% to -2.1% = 8 mph
            if( (i<= -2 && i >= -3) && currentSpeed == 8.0) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if ( (i<= -2 && i >= -3) && currentSpeed != 8.0) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 8.0 MPH, but it is " + currentSpeed + " mph<br>");
            }

            //-4.5% to -3.1% = 7 mph
            if( (i< -3.5 && i >= -4.5) && currentSpeed == 7.0) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if ( (i<= -3.5 && i >= -4.5) && currentSpeed != 7.0) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 7.0 MPH, but it is " + currentSpeed + " mph<br>");
            }

            //-6% to -5% = 6 mph
            if( (i< -5 && i >= -6) && currentSpeed == 6.0) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if ( (i<= -5 && i >= -6) && currentSpeed != 6.0) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 6.0 MPH, but it is " + currentSpeed + " mph<br>");
            }
            //>15% to 25% = 8 mph
            if( (i> 15 && i <= 25) && currentSpeed == 8.0) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if ( (i> 15 && i <= 25) && currentSpeed != 8.0) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 6.0 MPH, but it is " + currentSpeed + " mph<br>");
            }
            //>25% to 40% = 6 mph
            if( (i> 25 && i <= 40) && currentSpeed == 6.0) {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("At Incline " +currentIncline+", Current speed is " + currentSpeed + " mph<br>");
            }
            else if ( (i> 25 && i <= 40) && currentSpeed != 6.0) {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("At Incline " +currentIncline+" Current speed should be 6.0 MPH, but it is " + currentSpeed + " mph<br>");
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
        return res;
    }

    //--------------------------------------------//
         //                                            //
         //Testing Incline Condition from Checklist #44//
         //                                            //
         //--------------------------------------------//
    public String testInclineRetentionDmkRecall() throws Exception {
             //From Software Checklist #44
             //Redmine Support #1079
             //Set mode to Idle
             //Set Incline to 0
             //Set Incline to max incline
             //Halfway up, set mode to Running
             //Set mode to Pause
             //Set mode to DMK
             //Read actual Incline to verify the console has correct current incline

             System.out.println("NOW RUNNING INCLINE RETENTION AFTER DMK TEST<br>");

            appendMessage("<br>----------------------INCLINE RETENTION AFTER DMK TEST RESULTS----------------------<br><br>");
            appendMessage(Calendar.getInstance().getTime() + "<br><br>");

             double actualIncline;
             double maxIncline;

             //Set Incline to 0
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, 0);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(1000);

             //Check status of the command to receive the incline
            appendMessage("Status of setting incline to 0%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

             long elapsedTime = 0;
             double seconds = 0;
             long startime = 0;
             double setIncline = 0;
             //Wait for the incline motor to go to 5%
             startime= System.nanoTime();
             do
             {
                 actualIncline = hCmd.getActualIncline();
                 Thread.sleep(300);
                 System.out.println("Current Incline is: " + actualIncline+ " goal: " + setIncline+" time elapsed: "+seconds);
                 elapsedTime = System.nanoTime() - startime;
                 seconds = elapsedTime / 1.0E09;
             } while(setIncline!=actualIncline && seconds < 60);//Do while the incline hasn't reached its target point. Break the  loop if it took more than a minute to reach target incline

             maxIncline = 15; //hCmd.getMaxIncline();

             //Set Incline to Max Incline
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, maxIncline);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(3000);    //Wait a little while to get past 0, but not to max incline

            appendMessage("Status of setting incline to " + maxIncline + "% (Max Incline): " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

             //Set Mode to Running
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(1000);

            appendMessage("Status of setting mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

             //Set Mode to Pause
             ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(1000);

            appendMessage("Status of setting mode to Pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

             System.out.println("Pull DMK key now!");
             appendMessage("Waiting for DMK key to be pulled...<br>");
             while(hCmd.getMode()!=ModeId.DMK);
             {
                 // Stay here until DMK Key is pulled
             }
             System.out.println("DMK Key Pulled!");
             appendMessage("DMK key pulled!<br>");

             //Read Incline and verify it is not equal to max incline or less than, or equal to, zero

             actualIncline = hCmd.getActualIncline();

             if(actualIncline > 0  && actualIncline <maxIncline ){
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Actual Incline is currently at " + actualIncline + "% which is between 0% and max incline<br>");
             }
             else {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Actual Incline should be between 0% and " + maxIncline + "%, but it is currently at " + actualIncline + "%<br>");
             }

            appendMessage("<br>----------------------DMK RECALL INCLINE TEST RESULTS----------------------<br><br>");

             System.out.println("Put DMK key back on console");
             appendMessage("Waiting for DMK key to be put pack on console...<br>");
             while(hCmd.getMode()==ModeId.DMK);
             {
                 // Stay here until DMK Key put back on console
             }
             System.out.println("DMK Key Put back");
             appendMessage("DMK key put back!<br>");

             //Compare the value read for actual incline after key has been pulled to value read after key was put back
             if(actualIncline ==hCmd.getActualIncline() ){
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Actual Incline after DMK put back is at " + actualIncline + "% which is the same as when the key was pulled<br>");
             }
             else {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Actual Incline should be " + actualIncline + "%, but it is currently at " + hCmd.getActualIncline() + "%<br>");
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
             //end the recurring callback
             mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);

             return res;
         }

        @Override
    public String runAll() {
        String allTestInclineResults="";

        try {
            allTestInclineResults+=this.testInclineController();
            allTestInclineResults+=this.testStopIncline();
            allTestInclineResults+=this.testRetainedIncline();
            allTestInclineResults+=this.testSpeedInclineLimit();
            allTestInclineResults+=this.testInclineRetentionDmkRecall();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return allTestInclineResults;
    }
}
