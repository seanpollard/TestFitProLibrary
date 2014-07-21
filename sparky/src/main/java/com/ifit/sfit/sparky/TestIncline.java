package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

import java.util.Calendar;

/**
 * Created by jc.almonte on 7/14/14.
 */
public class TestIncline {

    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    private FecpCommand modeCommand;
    private FecpCommand inclineCommand;
    private FecpCommand readMinIncline;
    private FecpCommand readMaxIncline;
    private FecpCommand readInclineCommand;
    private FecpCommand readActualIncline;
    private FecpCommand readModeCommand;
    private FecpCommand sendKeyCmd;

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

            modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
            inclineCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
            readMinIncline = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 100, 100);
            readMaxIncline = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 100, 100);
            readModeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 100, 100);
            readInclineCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 100, 100);
            readActualIncline = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd, 100, 100);

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //--------------------------------------------//
    //                                            //
    //Testing All Inclines (in decrements of 1.0%)//
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

        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.MAX_GRADE);
        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.GRADE);
        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.TRANS_MAX);
        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
        mSFitSysCntrl.getFitProCntrl().addCmd(readModeCommand);
        Thread.sleep(1000);
        //Testing to see if setting TransMax value works
        //no longer need to set transmax but sometimes it is reading 92 instead of 183
        //FecpCommand setTransMax = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));

        //Set Mode to Idle
        ((WriteReadDataCmd)modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        Thread.sleep(1000);

       // ((WriteReadDataCmd)readMaxIncline.getCommand()).addReadBitField(BitFieldId.MAX_GRADE);
//       mSFitSysCntrl.getFitProCntrl().addCmd(readMaxIncline);
//        Thread.sleep(1000);

        //Check status of the command to receive the incline
       // inclineResults += "Status of reading max incline: " + (readMaxIncline.getCommand()).getStatus().getStsId().getDescription() + "\n";

        maxIncline = hCmd.getMaxIncline();
        inclineResults += "Max Incline is " + maxIncline + "%\n";
        System.out.println("Max Incline is " + maxIncline + "%\n");

//        ((WriteReadDataCmd)readMinIncline.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
//       mSFitSysCntrl.getFitProCntrl().addCmd(readMinIncline);
//        Thread.sleep(1000);

        //Check status of the command to receive the incline
       // inclineResults += "Status of reading min incline: " + (readMinIncline.getCommand()).getStatus().getStsId().getDescription() + "\n";

        minIncline = hCmd.getMinIncline();
        inclineResults += "Min Incline is " + minIncline + "%\n";
        System.out.println("Min Incline is " + minIncline + "%\n");


        //If TransMax is ever set to a ridiculously high number or something, you can reset it to this more reasonable TransMax value
        //fixed but sometimes reading 92 instead of 183
        //((WriteReadDataCmd)setTransMax.getCommand()).addWriteData(BitFieldId.TRANS_MAX, 183);
        //mFecpController.addCmd(setTransMax);
        //Thread.sleep(1000);

        //Check status of the command to set the transMax
        //inclineResults += "Status of setting Trans Max: " + (setTransMax.getCommand()).getStatus().getStsId().getDescription() + "\n";

//        ((WriteReadDataCmd)readCmd.getCommand()).addReadBitField(BitFieldId.TRANS_MAX);
//       mSFitSysCntrl.getFitProCntrl().addCmd(readCmd);
//        Thread.sleep(1000);

        //Check status of the command to receive the transMax
       // inclineResults += "Status of reading Trans Max: " + (readModeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

        transMax = hCmd.getTransMax();
        inclineResults += "TransMax is " + transMax + "\n\n";
        System.out.println("TransMax is " + transMax + "%\n");


        // mSFitSysCntrl.getFitProCntrl().removeCmd(readCmd);


        //--------------------------------------------------------------------------------------------------------------//
        //Run through all incline settings, going from -3% to 15% (hard-coded until min and max incline are implemented)//
        //--------------------------------------------------------------------------------------------------------------//
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        for(int i = 0; i < NUM_TESTS; i++)
        {
            for(double j = maxIncline; j >= minIncline; j = j-0.5)      //Set MAX_INCLINE TO the maximum incline and MIN_INCLINE to minimum
            {
                inclineResults += "Sending a command for incline at " + j + "% to the FecpController\n";

                //Set value for the incline
                ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.GRADE, j);
               mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
                Thread.sleep(50);

                //Check status of the command to send the incline
                inclineResults += "Status of sending incline " + j + "%: " + (modeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";
                startime= System.nanoTime();
                do
                {
                    currentActualIncline = hCmd.getActualIncline();
                    Thread.sleep(300);
                    System.out.println("Current Incline is: " + currentActualIncline+ "goal: " + j+" time elapsed: "+seconds);
                    elapsedTime = System.nanoTime() - startime;
                    seconds = elapsedTime / 1.0E09;
                } while(j!=currentActualIncline && seconds < 180);//Do while the incline hasn't reached its point yet or took more than 3 mins

//                ((WriteReadDataCmd)readModeCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
//               mSFitSysCntrl.getFitProCntrl().addCmd(readModeCommand);

//                Thread.sleep(1000);

//                inclineResults += "Status of reading the Mode: " + (readModeCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

                currentWorkoutMode = "Workout mode of incline at " + j + "% is " + hCmd.getMode() + "\n";
                inclineResults += currentWorkoutMode;

//                mFecpController.removeCmd(readModeCommand);

                //Read sent incline off of device
//                ((WriteReadDataCmd)readInclineCommand.getCommand()).addReadBitField(BitFieldId.GRADE);
//               mSFitSysCntrl.getFitProCntrl().addCmd(readInclineCommand);
//                Thread.sleep(1000);
//
//                //Check Status of the command to read the send incline
//                inclineResults += "Status of reading last sent incline at " + j + "%: " + (readInclineCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

                inclineResults += "The last set incline is " + hCmd.getIncline() + "%\n";
                System.out.println("The last set incline is " + hCmd.getIncline() + "%\n");


                //Read the actual incline off of device
//                ((WriteReadDataCmd)readActualIncline.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
//               mSFitSysCntrl.getFitProCntrl().addCmd(readActualIncline);
//                Thread.sleep(1000);

                //Check status of the command to receive the actual incline
//                inclineResults += "Status of reading actual incline at " + j + "%: " + (readActualIncline.getCommand()).getStatus().getStsId().getDescription() + "\n";

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

        return inclineResults;
    }

}
