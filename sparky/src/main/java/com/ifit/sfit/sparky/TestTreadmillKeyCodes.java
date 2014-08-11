package com.ifit.sfit.sparky;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.SetTestingKeyCmd;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.key.KeyCodes;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * Created by jc.almonte on 8/6/14.
 */
public class TestTreadmillKeyCodes extends TestCommons implements TestAll {

        //Variables needed to initialize connection with Brainboard
        private FecpController mFecpController;
        private BaseTest mAct;
        private HandleCmd hCmd;
        private SFitSysCntrl mSFitSysCntrl;
        private SystemDevice MainDevice;
        private FecpCommand wrCmd;
        private FecpCommand rdCmd;
        private FecpCommand sendKeyCmd;

        public TestTreadmillKeyCodes(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
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
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.KPH);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.GRADE);
                    // ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.AGE);
                    //((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.WEIGHT);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.PAUSE_TIMEOUT);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.IDLE_TIMEOUT);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.RUNNING_TIME);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MAX_KPH);
                    ((WriteReadDataCmd)rdCmd.getCommand()).addReadBitField(BitFieldId.MIN_KPH);

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
        //      Testing All Treadmill Keycodes        //
        //                                            //
        //--------------------------------------------//
//TODO: In the past when this tests worked, you could hear the buzzer clicking sound when key command was sent
//TODO: Write the code for the rest of the Keys to have it ready to go as soon as set_testing_key is working
 public String testStopKey() throws Exception{
            //Redmine Support #925
            //Test Stop button press
            //Set mode to Running
            //Simulate Stop button press
            //Validate that mode is changed to Pause

            String stopKeyResults="";
            String currentMode;

            stopKeyResults += "\n\n------------------STOP KEY TEST---------------\n\n";
            stopKeyResults+= Calendar.getInstance().getTime() + "\n\n";
            appendMessage("<br><br>------------------STOP KEY TEST------------------<br><br>");
            appendMessage(Calendar.getInstance().getTime() + "<br><br>");

            //set the mode to running
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(5000); //Run for 5 seconds

            currentMode = hCmd.getMode().getDescription();

            stopKeyResults += "Currently set to " + currentMode + "\n";
            appendMessage("Currently set to " + currentMode + "<br>");

            stopKeyResults += "sent the stop key command...\n";
            appendMessage("sent the stop key command...<br>");
            Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

            if(keyPressTemp != null){
                Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
                if(writeKeyPressCmd != null){
                    sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.STOP);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
                }
            }

            mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
            Thread.sleep(1000);

            stopKeyResults += "Status of sending Stop key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            appendMessage("Status of sending Stop key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
            Thread.sleep(1000);

            currentMode = hCmd.getMode().getDescription();

            //When the Stop key is pressed, it should change the mode from Running to Pause Mode
            if(currentMode.equals("Pause Mode")){
                stopKeyResults += "\n* PASS *\n\n";
                stopKeyResults += "Stop key successfully changed Running Mode to Pause Mode\n";

                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Stop key successfully changed Running Mode to Pause Mode\n");


            }
            else{
                stopKeyResults += "\n* FAIL *\n\n";
                stopKeyResults += "Mode should be changed to Pause Mode, but is currently set at " + currentMode + "\n";

                appendMessage(" <br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Mode should have changed to Pause Mode, but is currently set at " + currentMode + "<br>");
            }


            //set mode to Idle to reset for other tests
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);

            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);


            return stopKeyResults;
        }

    @Override
    public String runAll() throws Exception {
        //Redmine Support #925
        //Testing all keycodes for a treadmill
        String keysResults;

        keysResults = "\n----------------------TREADMILL KEYCODES TEST----------------------\n\n";
        keysResults += Calendar.getInstance().getTime() + "\n\n";

          keysResults += testStopKey();
//        keysResults += testStartKey();
//        keysResults += testInclineUpKey();
//        keysResults += testInclineDownKey();
//        keysResults += testQuickInclineKeys();
//        keysResults += testSpeedUpKey();
//        keysResults += testSpeedDownKey();
//        keysResults += testQuickSpeedKeys();
//        keysResults += testAgeUpKey();
//        keysResults += testAgeDownKey();

        return keysResults;
    }
}
