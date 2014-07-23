package com.ifit.sfit.sparky;

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
public class TestIncline {

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

        return inclineResults;
    }

}
