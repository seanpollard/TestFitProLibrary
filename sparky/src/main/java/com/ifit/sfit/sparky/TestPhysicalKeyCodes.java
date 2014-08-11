package com.ifit.sfit.sparky;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

import java.nio.ByteBuffer;

/**
 * Created by jc.almonte on 7/9/14.
 */

//This class tests that the program recognizes the physical key pressed
// and it tells for how long it was pressed (in milliseconds)

public class TestPhysicalKeyCodes extends TestCommons implements TestAll {
    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private BaseTest mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    private FecpCommand rdCmd;


    public TestPhysicalKeyCodes(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
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
        System.out.println("NOW RUNNING START KEY TEST.. PRESS AND HOLD START KEY<br>");
        String currentKey;
        int timeHeld;

        appendMessage("NOW RUNNING START KEY TEST.. PRESS AND HOLD START KEY<br>");
        Thread.sleep(3000); // Give 3 secs for people to press start button
        currentKey = hCmd.getKey().getCookedKeyCode().toString();
        timeHeld = hCmd.getKey().getTimeHeld();
        appendMessage("YOU CAN LET GO OF START KEY!<br>");
        if(currentKey.equals("START")){
            appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The " + currentKey + " key was pressed and held for " + timeHeld + " ms<br>");
        }
        else{
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The " + currentKey + " key was pressed and held for " + timeHeld + " ms (should have been START key)<br>");
        }

        return res;
    }
    //Test the stop key
    public String testStopKey() throws Exception {
        String currentKey;
        int timeHeld;

        appendMessage("<br><br>PHYSICAL STOP KEY TEST<br>");
        appendMessage("PRESS AND HOLD START KEY<br>");
        Thread.sleep(3000);// Give 3 secs for people to press stop button
        currentKey = hCmd.getKey().getCookedKeyCode().toString();
        timeHeld = hCmd.getKey().getTimeHeld();
        System.out.println("YOU CAN LET GO OF STOP KEY!<br>");
        if(currentKey.equals("STOP")){
            appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("The " + currentKey + " key was pressed and held for " + timeHeld + " ms<br>");
        }
        else{
            appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("The " + currentKey + " key was pressed and held for " + timeHeld + " ms (should have been STOP key)<br>");
        }

        return res;
    }

    public String testQuickInclineKeys() throws Exception {
        String currentKey;
        int timeHeld;
        //max and min incline should eventually be read from the brainboard, but the max incline is currently set at 15%,
        //which is the max of the incline motor - not the console buttons
        int maxIncline = 40;
        int minIncline = -6;

        appendMessage("PHYSICAL QUICK INCLINE KEYS TEST<br>");


        for(int i = maxIncline; i >= 0; i-=5) {

            appendMessage("Press the "+ i +" key");
            Thread.sleep(5000);
            currentKey = hCmd.getKey().getCookedKeyCode().toString();
            timeHeld = hCmd.getKey().getTimeHeld();
            System.out.println(currentKey);
            if (currentKey.equals("INCLINE_" + i)) {
                appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("The " + currentKey + " key was pressed and held for " + timeHeld + " ms<br>");
            } else {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("The " + currentKey + " key was pressed and held for " + timeHeld + " ms (should have been INCLINE_"+i+" key)<br>");
            }

        }
        for(int i = -2; i >= minIncline; i-=2){
            appendMessage("Press the "+ i +" key");
            Thread.sleep(5000);
            currentKey = hCmd.getKey().getCookedKeyCode().toString();
            timeHeld = hCmd.getKey().getTimeHeld();
            System.out.println(currentKey);
            if(currentKey.equals("INCLINE_NEG_"+Math.abs(i))){
                appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("The " + currentKey + " key was pressed and held for " + timeHeld + " ms<br>");
            }
            else{
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("The " + currentKey + " key was pressed and held for " + timeHeld + " ms (should have been INCLINE_NEG_"+Math.abs(i)+" key)<br>");
            }
        }

        return res;
    }

    @Override
    public String runAll() throws Exception {

        String results = "";
        results+=this.testStartKey();
        results+=this.testStopKey();
        results+=this.testQuickInclineKeys();

        return results;
    }
}
