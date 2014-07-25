package com.ifit.sfit.sparky;

import android.graphics.AvoidXfermode;

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
public class TestIncline implements TestAll {

    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    private FecpCommand wrCmd;
    private FecpCommand rdCmd;
    private String currentWorkoutMode = "";
    private double currentIncline = 0.0;
    private double actualInlcine = 0.0;

    private final int NUM_TESTS = 1;

    public TestIncline(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
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
        String inclineResults;

        inclineResults = "\n----------------------INCLINE CONTROLLER TEST RESULTS----------------------\n\n";
        inclineResults += Calendar.getInstance().getTime() + "\n\n";

        double maxIncline;
        double minIncline;
        double currentActualIncline;
        double transMax;

        minIncline = hCmd.getMinIncline();
        maxIncline = hCmd.getMaxIncline();
        inclineResults += "Min Incline is " + minIncline + "%\n";
        System.out.println("Min Incline is " + minIncline + "%\n");


        //If TransMax is ever set to a ridiculously high number or something, you can reset it to this more reasonable TransMax value
        //fixed but sometimes reading 92 instead of 183
        //((WriteReadDataCmd)setTransMax.getCommand()).addWriteData(BitFieldId.TRANS_MAX, 183);
        //mFecpController.addCmd(setTransMax);
        //Thread.sleep(1000);

        transMax = hCmd.getTransMax();
        inclineResults += "TransMax is " + transMax + "\n\n";
        System.out.println("TransMax is " + transMax + "%\n");

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
                inclineResults += "Sending a command for incline at " + j + "% to the FecpController\n";

                //Set value for the incline
                ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, j);
               mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
                Thread.sleep(50);

                //Check status of the command to send the incline
                inclineResults += "Status of sending incline " + j + "%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
                startime= System.nanoTime();
                do
                {
                    currentActualIncline = hCmd.getActualIncline();
                    Thread.sleep(300);
                    System.out.println("Current Incline is: " + currentActualIncline+ " goal: " + j+" time elapsed: "+seconds);
                    elapsedTime = System.nanoTime() - startime;
                    seconds = elapsedTime / 1.0E09;
                } while(j!=currentActualIncline && seconds < 180);//Do while the incline hasn't reached its point yet or took more than 3 mins

                currentWorkoutMode = "Workout mode of incline at " + j + "% is " + hCmd.getMode() + "\n";
                inclineResults += currentWorkoutMode;

                inclineResults += "The last set incline is " + hCmd.getIncline() + "%\n";
                System.out.println("The last set incline is " + hCmd.getIncline() + "%\n");

                //Read the actual incline off of device
                actualInlcine = hCmd.getActualIncline();
                inclineResults += "The actual incline is currently at: " + actualInlcine + "%\n";
                System.out.println("The actual incline is currently at: " + actualInlcine + "%\n");

                inclineResults += "\nFor Incline at " + j + "%:\n";

                if(j == actualInlcine)
                {
                    inclineResults += "\n* PASS *\n\n";
                }
                else
                {
                    inclineResults += "\n* FAIL *\n\nThe incline is off by " + (j - actualInlcine) + "%\n\n";
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
        return inclineResults;
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
        String stopInclineResults;
        double maxIncline;
        double minIncline;
        double maxToMinIncline1;
        double maxToMinIncline2;
        double minToMaxIncline1;
        double minToMaxIncline2;

        stopInclineResults = "\n\n----------------------STOP INCLINE TEST RESULTS----------------------\n\n";
        stopInclineResults += Calendar.getInstance().getTime() + "\n\n";
        double currentActualIncline;
        long elapsedTime = 0, startime = 0;
        double seconds = 0;


        maxIncline = hCmd.getMaxIncline();
        stopInclineResults += "Max Incline is " + maxIncline + "%\n";
        System.out.println("Max Incline is " + maxIncline + "%\n");

        minIncline = hCmd.getMinIncline();
        stopInclineResults += "Min Incline is " + minIncline + "%\n";
        System.out.println("MinIncline is " + minIncline + "%\n");

        //Set Incline to Min Incline
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, minIncline);
        mFecpController.addCmd(wrCmd);
        Thread.sleep(1000);
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

        stopInclineResults += "Status of sending Stop key command: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";

        minToMaxIncline1 = hCmd.getActualIncline();
        Thread.sleep(5000);

        minToMaxIncline2 = hCmd.getMaxIncline();

        if(minToMaxIncline1 == minToMaxIncline2){
            stopInclineResults += "\n* PASS *\n\n";
            stopInclineResults += "The incline value from Min Incline to Max Incline was reset to " + minToMaxIncline1 + "%\n";
        }
        else{
            stopInclineResults += "\n* FAIL *\n\n";
            stopInclineResults += "The Incline value from Min Incline to Max Incline did not stop when the Stop button was pressed\n";
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

        stopInclineResults += "Status of sending Stop key command: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";

        maxToMinIncline1 = hCmd.getActualIncline();
        Thread.sleep(5000);

        maxToMinIncline2 = hCmd.getActualIncline();

        if(maxToMinIncline1 == maxToMinIncline2){
            stopInclineResults += "\n* PASS *\n\n";
            stopInclineResults += "The incline value from Max Incline to Min Incline was reset to " + maxToMinIncline1 + "%\n";
        }
        else{
            stopInclineResults += "\n* FAIL *\n\n";
            stopInclineResults += "The Incline value from Max Incline to Min Incline did not stop when the Stop button was pressed\n";
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
        return stopInclineResults;
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

        retainedInclineResults = "\n\n----------------------RETAINED INCLINE TEST RESULTS----------------------\n\n";
        retainedInclineResults += Calendar.getInstance().getTime() + "\n\n";

        double currentIncline1, currentIncline2;
        double testIncline = 5;
        double setIncline = 0;
        String currentMode;
        double currentActualIncline =0;

        currentMode = "Current Mode is: " + hCmd.getMode();
        retainedInclineResults += currentMode + "\n";

        //Set incline to 5% for testing the incline

        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, testIncline);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        setIncline = hCmd.getIncline();
        retainedInclineResults += "The status of setting the Incline to 5%: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

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

        retainedInclineResults += "The actual incline is " + currentIncline1 +" % \n";

        //Set Mode to Running
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);
        retainedInclineResults += "Status of setting the mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

        //wait for the motor to speed up
        Thread.sleep(3000);
        //read the mode
        currentMode = hCmd.getMode().toString();
        retainedInclineResults += "Current Mode is: " + currentMode + "\n";
        //let the workout run for 30 sec
        Thread.sleep(30000);

        currentIncline2 = hCmd.getActualIncline();

        if(currentIncline1 == currentIncline2 && currentIncline1 == testIncline && currentMode.equals("RUNNING")){
            retainedInclineResults += "\n* PASS *\n\n";
            retainedInclineResults += "The Incline went to " + testIncline + " and did not change when the mode was changed to Running\n";
        }
        else{
            if(!currentMode.equals("RUNNING")){
                retainedInclineResults += "\n* FAIL *\n\n";
                retainedInclineResults += "Mode didn't change to Running\n";
            }
            if(currentIncline1 != currentIncline2) {
                retainedInclineResults += "\n* FAIL *\n\n";
                retainedInclineResults += "Incline should be " + currentIncline1 + "%, but is " + currentIncline2 + "%\n";
            }
            if(currentIncline1!= testIncline || currentIncline2 != testIncline) {
                retainedInclineResults += "\n* FAIL *\n\n";
                retainedInclineResults += "The incline did not go to " + testIncline + " %\n";
                retainedInclineResults += "The incline was at " + currentIncline1 + " % before mode change and " + currentIncline2 + " % afterwards \n";
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

        return retainedInclineResults;
    }


    @Override
    public String runAll() {
        String allTestInclineResults="";

        try {
            allTestInclineResults+=this.testInclineController();
            allTestInclineResults+=this.testStopIncline();
            allTestInclineResults+=this.testRetainedIncline();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return allTestInclineResults;
    }
}
