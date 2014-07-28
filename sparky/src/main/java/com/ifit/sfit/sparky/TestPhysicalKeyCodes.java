package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

import java.nio.ByteBuffer;

/**
 * Created by jc.almonte on 7/9/14.
 */

//This class tests that the program recognizes the physical key pressed
// and it tells for how long it was pressed (in milliseconds)

public class TestPhysicalKeyCodes implements TestAll {
    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    private FecpCommand rdCmd;


    public TestPhysicalKeyCodes(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
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
            //unlock the system
            this.mSFitSysCntrl.getFitProCntrl().unlockSystem(secretKey);
            Thread.sleep(1000);
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();

            rdCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd);
            ((WriteReadDataCmd) rdCmd.getCommand()).addReadBitField(BitFieldId.KEY_OBJECT);
            mSFitSysCntrl.getFitProCntrl().addCmd(rdCmd);
            Thread.sleep(1000);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //Test the Start Key
    public String testStartKey() throws Exception {
        System.out.println("NOW RUNNING START KEY TEST.. PRESS AND HOLD START KEY\n");
        String startKeyResults;
        String currentKey;
        int timeHeld;

        startKeyResults = "PHYSICAL START KEY TEST\n";
        Thread.sleep(3000); // Give 3 secs for people to press start button
        currentKey = hCmd.getKey().getCookedKeyCode().toString();
        timeHeld = hCmd.getKey().getTimeHeld();
        System.out.println("YOU CAN LET GO OF START KEY!\n");
        if(currentKey.equals("START")){
            startKeyResults += "\n* PASS *\n\n";
            startKeyResults += "The " + currentKey + " key was pressed and held for " + timeHeld + " ms\n";
        }
        else{
            startKeyResults += "\n* FAIL *\n\n";
            startKeyResults += "The " + currentKey + " key was pressed and held for " + timeHeld + " ms (should have been START key)\n";
        }

        return startKeyResults;
    }
    //Test the stop key
    public String testStopKey() throws Exception {
        System.out.println("NOW RUNNING STOP KEY TEST.. PRESS AND HOLD START KEY\n");
        String stopKeyResults;
        String currentKey;
        int timeHeld;

        stopKeyResults = "\n\nPHYSICAL STOP KEY TEST\n";
        Thread.sleep(3000);// Give 3 secs for people to press stop button
        currentKey = hCmd.getKey().getCookedKeyCode().toString();
        timeHeld = hCmd.getKey().getTimeHeld();
        System.out.println("YOU CAN LET GO OF STOP KEY!\n");
        if(currentKey.equals("STOP")){
            stopKeyResults += "\n* PASS *\n\n";
            stopKeyResults += "The " + currentKey + " key was pressed and held for " + timeHeld + " ms\n";
        }
        else{
            stopKeyResults += "\n* FAIL *\n\n";
            stopKeyResults += "The " + currentKey + " key was pressed and held for " + timeHeld + " ms (should have been STOP key)\n";
        }

        return stopKeyResults;
    }

    public String testQuickInclineKeys() throws Exception {
        System.out.println("NOW RUNNING QUICK INCLINE KEY TEST.. \n");
        String quickInclineKeyResults;
        String currentKey;
        int timeHeld;
        //max and min incline should eventually be read from the brainboard, but the max incline is currently set at 15%,
        //which is the max of the incline motor - not the console buttons
        int maxIncline = 40;
        int minIncline = -6;

        quickInclineKeyResults = "PHYSICAL QUICK INCLINE KEYS TEST\n";


        for(int i = maxIncline; i >= 0; i-=5) {

            System.out.println("Press the "+ i +" key");
            Thread.sleep(5000);
            currentKey = hCmd.getKey().getCookedKeyCode().toString();
            timeHeld = hCmd.getKey().getTimeHeld();
            System.out.println(currentKey);
            if (currentKey.equals("INCLINE_" + i)) {
                quickInclineKeyResults += "\n* PASS *\n\n";
                quickInclineKeyResults += "The " + currentKey + " key was pressed and held for " + timeHeld + " ms\n";
            } else {
                quickInclineKeyResults += "\n* FAIL *\n\n";
                quickInclineKeyResults += "The " + currentKey + " key was pressed and held for " + timeHeld + " ms (should have been INCLINE_"+i+" key)\n";
            }

        }
        for(int i = -2; i >= minIncline; i-=2){
            System.out.println("Press the "+ i +" key");
            Thread.sleep(5000);
            currentKey = hCmd.getKey().getCookedKeyCode().toString();
            timeHeld = hCmd.getKey().getTimeHeld();
            System.out.println(currentKey);
            if(currentKey.equals("INCLINE_NEG_"+Math.abs(i))){
                quickInclineKeyResults += "\n* PASS *\n\n";
                quickInclineKeyResults += "The " + currentKey + " key was pressed and held for " + timeHeld + " ms\n";
            }
            else{
                quickInclineKeyResults += "\n* FAIL *\n\n";
                quickInclineKeyResults += "The " + currentKey + " key was pressed and held for " + timeHeld + " ms (should have been INCLINE_NEG_"+Math.abs(i)+" key)\n";
            }
        }

        return quickInclineKeyResults;
    }

    @Override
    public String runAll() throws Exception {
        String allPhysicalCodesRes = "";

        allPhysicalCodesRes+=this.testStartKey();
        allPhysicalCodesRes+=this.testStopKey();
        allPhysicalCodesRes+=this.testQuickInclineKeys();

        return allPhysicalCodesRes;
    }
}
