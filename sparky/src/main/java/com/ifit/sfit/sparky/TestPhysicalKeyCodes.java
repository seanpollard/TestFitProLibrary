package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

/**
 * Created by jc.almonte on 7/9/14.
 */

public class TestPhysicalKeyCodes {
    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    private FecpCommand modeCommand;
    private FecpCommand readModeCommand;
    private FecpCommand sendKeyCmd;
    private FecpCommand readSpeedCommand;
    private FecpCommand speedCommand;
    private FecpCommand readKeyObjectCommand;

    public TestPhysicalKeyCodes(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            hCmd = new HandleCmd(this.mAct);// Init handlers
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();

            readModeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd);
            modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);

            readSpeedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd);
            speedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);

            readKeyObjectCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public String testStartKey() throws Exception {
        //
        String startKeyResults;
        String currentKey;
        int timeHeld;

        startKeyResults = "PHYSICAL START KEY TEST\n";

        ((WriteReadDataCmd)readKeyObjectCommand.getCommand()).addReadBitField(BitFieldId.KEY_OBJECT);
        mSFitSysCntrl.getFitProCntrl().addCmd(readKeyObjectCommand);
        Thread.sleep(5000);

        currentKey = hCmd.getKey().getCookedKeyCode().toString();
        timeHeld = hCmd.getKey().getTimeHeld();

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
    public String testStopKey() throws Exception {
        //
        String stopKeyResults;
        String currentKey;
        int timeHeld;

        stopKeyResults = "\n\nPHYSICAL STOP KEY TEST\n";

        ((WriteReadDataCmd)readKeyObjectCommand.getCommand()).addReadBitField(BitFieldId.KEY_OBJECT);
        mFecpController.addCmd(readKeyObjectCommand);
        Thread.sleep(14000);

        currentKey = hCmd.getKey().getCookedKeyCode().toString();
        timeHeld = hCmd.getKey().getTimeHeld();

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

}
