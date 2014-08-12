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

            String results="";
            String currentMode;

            results += "\n\n------------------STOP KEY TEST---------------\n\n";
            results+= Calendar.getInstance().getTime() + "\n\n";
            appendMessage("<br><br>------------------STOP KEY TEST------------------<br><br>");
            appendMessage(Calendar.getInstance().getTime() + "<br><br>");

            currentMode = hCmd.getMode().getDescription();
            results += "Mode currently set to " + currentMode + "\n";
            appendMessage("Mode currently set to " + currentMode + "<br>");
            results += "setting the mode to running...\n";
            appendMessage("setting the mode to running...<br>");

            //set the mode to running
            ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
            mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
            Thread.sleep(1000);
            results += "Status of changing mode to running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            appendMessage("Status of changing mode to running: " + wrCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

            Thread.sleep(4000 );//Run for 5 seconds
            currentMode = hCmd.getMode().getDescription();

            results += "Mode currently set to " + currentMode + "\n";
            appendMessage("Mode currently set to " + currentMode + "<br>");

            results += "sending the stop key command...\n";
            appendMessage("sending the stop key command...<br>");
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

            results += "Status of sending Stop key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            appendMessage("Status of sending Stop key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
            Thread.sleep(1000);

            currentMode = hCmd.getMode().getDescription();

            //When the Stop key is pressed, it should change the mode from Running to Pause Mode
            if(currentMode.equals("Pause Mode")){
                results += "\n* PASS *\n\n";
                results += "Stop key successfully changed Running Mode to Pause Mode\n";

                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Stop key successfully changed Running Mode to Pause Mode\n");


            }
            else{
                results += "\n* FAIL *\n\n";
                results += "Mode should be changed to Pause Mode, but is currently set at " + currentMode + "\n";

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


            return results;
        }
 public String testStartKey() throws Exception{
        //Redmine Support #1170
        //Testing Start Key Press
//        Read current mode
//        Simulate Start key press
//        Validate that mode is changed to Running
//        Change mode to Pause (stop)

        String results="";
        String currentMode;

        results += "\n\n------------------START KEY TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------START KEY TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");

        currentMode = hCmd.getMode().getDescription();

        results += "Mode currently set to " + currentMode + "\n";
        appendMessage("Mode currently set to " + currentMode + "<br>");

        results += "sending the start key command...\n";
        appendMessage("sending the start key command...<br>");
        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.START);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

        mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
        Thread.sleep(1000);

        results += "Status of sending Start key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
        appendMessage("Status of sending Start key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
        mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
        Thread.sleep(1000);

        currentMode = hCmd.getMode().getDescription();

        //When the Stop key is pressed, it should change the mode from Running to Pause Mode
        if(currentMode.equals("Running Mode")){
            results += "\n* PASS *\n\n";
            results += "Start key successfully changed Mode to Running\n";

            appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
            appendMessage("Start key successfully changed Mode to Running\n");


        }
        else{
            results += "\n* FAIL *\n\n";
            results += "Mode should be changed to Running, but is currently set at " + currentMode + "\n";

            appendMessage(" <br><font color = #ff0000>* FAIL *</font><br><br>");
            appendMessage("Mode should be changed to Running, but is currently set at " + currentMode + "<br>");
        }


        //set mode to Idle to reset for other tests
        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);


        return results;
    }
 public String testInclineUpKey() throws Exception{
     //Redmine Support #1171
     //Testing Incline Up key press
     //1. Initialize Incline to min
     //2. Simulate Incline Up key press
     //3. Validate that Incline went up 0.5%
     //4. Repeat steps 2-3 until max incline reached

     String results ="";
     double currentActualIncline = 0;
     double incline1 = 0;
     double incline2 = 0;
     double maxIncline =  15; //hCmd.getMaxIncline(); // The motor we are using has max incline of 15%
     double minIncline = hCmd.getMinIncline();
     long elapsedTime = 0;
     double seconds = 0;
     long startime = 0;


     results += "\n\n------------------INCLINE UP KEY TEST---------------\n\n";
     results+= Calendar.getInstance().getTime() + "\n\n";
     appendMessage("<br><br>------------------INCLINE UP KEY TEST------------------<br><br>");
     appendMessage(Calendar.getInstance().getTime() + "<br><br>");


     results += "setting incline to min...\n";
     appendMessage("setting incline to min...<br>");
    //Set value for the incline
     ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, minIncline);
     mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
     Thread.sleep(1000);

     //Check status of the command to send the incline
     appendMessage("Status of setting incline to min: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
     appendMessage("Checking incline will reach set value...<br>");

     results+="Status of setting incline to min: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
     results+="Checking incline will reach set value...\n";
     startime= System.nanoTime();
     do
     {
         currentActualIncline = hCmd.getActualIncline();
         Thread.sleep(350);
         appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + minIncline+" time elapsed: "+seconds+"<br>");
         results+="Current Incline is: " + currentActualIncline+ " goal: " + minIncline+" time elapsed: "+seconds+"\n";
         elapsedTime = System.nanoTime() - startime;
         seconds = elapsedTime / 1.0E09;
     } while(minIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1.5 mins

     results += "sending the start key command...\n";
     appendMessage("sending the start key command...<br>");
     Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

     if(keyPressTemp != null){
         Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
         if(writeKeyPressCmd != null){
             sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.INCLINE_UP);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
         }
     }

     //Increment incline by 0.5 and verify the increment happened. Repeat until max incline reached

     for (double i = minIncline; i<=maxIncline; i+=0.5)
     {
         incline1 = hCmd.getActualIncline();
         mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
         Thread.sleep(3000); //wait 3 seconds
         results += "Status of sending Incline Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         appendMessage("Status of sending Incline Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
         incline2 = hCmd.getActualIncline();

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */

         if((incline2 - incline1) == 0.5)
         {
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("Previous incline: "+incline1+" current incline: "+incline2+". Incline correclty incremented by "+(incline2-incline1)+" %<br>");

             results+="\n* PASS *\n\n";
             results+="Previous incline: "+incline1+" current incline: "+incline2+". Incline correclty incremented by "+(incline2-incline1)+" %\n";
         }
         else
         {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
             appendMessage("Previous incline: "+incline1+" current incline: "+incline2+". Incline increment was "+(incline2-incline1)+" % and should have been 0.5 %<br>");

             results+="\n* FAIL *\n\n";
             results+="Previous incline: "+incline1+" current incline: "+incline2+". Incline increment was "+(incline2-incline1)+" % and should have been 0.5 %<br>";
         }
     }

     return results;
 }
 public String testInclineDownKey() throws Exception{
        //Redmine Support #1171
        //Testing Incline Up key press
        //1. Initialize Incline to min
        //2. Simulate Incline Up key press
        //3. Validate that Incline went up 0.5%
        //4. Repeat steps 2-3 until max incline reached

        String results ="";
        double currentActualIncline = 0;
        double incline1 = 0;
        double incline2 = 0;
        double maxIncline =  15; //hCmd.getMaxIncline(); // The motor we are using has max incline of 15%
        double minIncline = hCmd.getMinIncline();
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;


        results += "\n\n------------------INCLINE DOWN KEY TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------INCLINE DOWN KEY TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");


        results += "setting incline to max...\n";
        appendMessage("setting incline to max...<br>");
        //Set value for the incline
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.GRADE, maxIncline);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //Check status of the command to send the incline
        appendMessage("Status of setting incline to max: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        appendMessage("Checking incline will reach set value...<br>");

        results+="Status of setting incline to max: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
        results+="Checking incline will reach set value...\n";
        startime= System.nanoTime();
        do
        {
            currentActualIncline = hCmd.getActualIncline();
            Thread.sleep(350);
            appendMessage("Current Incline is: " + currentActualIncline+ " goal: " + maxIncline+" time elapsed: "+seconds+"<br>");
            results+="Current Incline is: " + currentActualIncline+ " goal: " + maxIncline+" time elapsed: "+seconds+"\n";
            elapsedTime = System.nanoTime() - startime;
            seconds = elapsedTime / 1.0E09;
        } while(minIncline!=currentActualIncline && seconds < 60);//Do while the incline hasn't reached its point yet or took more than 1.5 mins

        results += "sending the start key command...\n";
        appendMessage("sending the start key command...<br>");
        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.INCLINE_DOWN);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

        //Decrement incline by 0.5 and verify the decrement happened. Repeat until min incline reached

        for (double i = maxIncline; i>=minIncline; i-=0.5)
        {
            incline1 = hCmd.getActualIncline();
            mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
            Thread.sleep(3000); //wait 3 seconds
            results += "Status of sending Incline Down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            appendMessage("Status of sending Incline Down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");
            incline2 = hCmd.getActualIncline();

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */

            if( (incline2 - incline1) == -0.5)
            {
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                appendMessage("Previous incline: "+incline1+" current incline: "+incline2+". Incline correclty decremented by "+(incline2-incline1)+" %<br>");

                results+="\n* PASS *\n\n";
                results+="Previous incline: "+incline1+" current incline: "+incline2+". Incline correclty decremented by "+(incline2-incline1)+" %\n";
            }
            else
            {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                appendMessage("Previous incline: "+incline1+" current incline: "+incline2+". Incline decrement was "+(incline2-incline1)+" % and should have been 0.5 %<br>");

                results+="\n* FAIL *\n\n";
                results+="Previous incline: "+incline1+" current incline: "+incline2+". Incline decrement was "+(incline2-incline1)+" % and should have been 0.5 %<br>";
            }
        }

        return results;
    }
 public String testSpeedUpKey() throws Exception {
     //Testing Speed Up key press
     //1. Set mode to running
     //2. Simulate Speed Up key press
     //3. Validate that Speed went up by 0.1 kph
     //4. Repeat steps 2-3 until max speed reached


     String results = "";
     double currentSpeed = 0;
     long elapsedTime = 0;
     double seconds = 0;
     long startime = 0;


     results += "\n\n------------------SPEED UP KEY TEST---------------\n\n";
     results+= Calendar.getInstance().getTime() + "\n\n";
     appendMessage("<br><br>------------------SPEED UP KEY TEST------------------<br><br>");
     appendMessage(Calendar.getInstance().getTime() + "<br><br>");


     results += "setting mode to running...\n";
     appendMessage("setting mode to running...<br>");
     ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
     mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
     Thread.sleep(1000);

     //Check status of the command to send the incline
     appendMessage("Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
     results+="Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

     results += "sending the Speed Up key command...\n";
     appendMessage("sending the Speed Up key command...<br>");
     Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

     if(keyPressTemp != null){
         Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
         if(writeKeyPressCmd != null){
             sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.SPEED_UP);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
             ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
         }
     }

     double expected = 1.0;
//Tests range from 1.1 up to 16.0
     for (int i=0; i < 150; i++) {
         mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
         Thread.sleep(3000); //wait 3 seconds
         results += "Status of sending Speed Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
         appendMessage("Status of sending Speed Up key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
         currentSpeed = hCmd.getSpeed(); //TODO: use actual speed once speed is accurate
         appendMessage("Current speed is: " + currentSpeed + " kph<br>");
         results += "Current speed is: " + currentSpeed + " kph\n";

         if (currentSpeed == ((double) Math.round(expected * 10) / 10)) {   //Need to round off to the nearest tenth, because of the ultra-precision of doubles
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br> Speed Up button incremented by 0.1 <br><br>");

             results += "\n* PASS *\n\nSpeed Up button incremented by 0.1\n";

         } else {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> Speed Up button did not increment by 0.1 <br><br>");

             results += "\n* FAIL *\n\n Speed Up button did not increment by 0.1\n";

         }
     }

     return results;
 }
 public String testSpeedDownKey() throws Exception {
        //Testing Speed Up key press
//        1. Set mode to running
//        2. Set speed to max
//        3. Simulate Speed Down key press
//        4. Validate that Speed went down by 0.1 kph
//        5. Repeat steps 2-4 until min speed reached


        String results = "";
        double currentSpeed = 0;
        long elapsedTime = 0;
        double seconds = 0;
        long startime = 0;
        double maxSpeed = 16; //hCmd.getMaxSpeed();


        results += "\n\n------------------SPEED DOWN KEY TEST---------------\n\n";
        results+= Calendar.getInstance().getTime() + "\n\n";
        appendMessage("<br><br>------------------SPEED DOWN KEY TEST------------------<br><br>");
        appendMessage(Calendar.getInstance().getTime() + "<br><br>");


        results += "setting mode to running...\n";
        appendMessage("setting mode to running...<br>");
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //Check status of the command to send the incline
        appendMessage("Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        results+="Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

        results += "setting speed to max...\n";
        appendMessage("setting speed to max...<br>");
        ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.KPH, maxSpeed);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
        Thread.sleep(1000);

        //Check status of the command to send the incline
        appendMessage("Status of setting speed to max: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
        results+="Status of setting speed to max: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

       Thread.sleep(23000);// Wait for speed to reach max

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

        results += "sending the Speed Down key command...\n";
        appendMessage("sending the Speed Up key command...<br>");
        Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);

        if(keyPressTemp != null){
            Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
            if(writeKeyPressCmd != null){
                sendKeyCmd = new FecpCommand(writeKeyPressCmd, hCmd);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.SPEED_UP);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
            }
        }

        double expected = 1.0;
//Tests range from 16.0 t0 1.0
        for (int i=149; i >= 0; i--) {
            mSFitSysCntrl.getFitProCntrl().addCmd(sendKeyCmd);
            Thread.sleep(3000); //wait 3 seconds
            results += "Status of sending Speed Down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "\n";
            appendMessage("Status of sending Speed Down key command: " + sendKeyCmd.getCommand().getStatus().getStsId().getDescription() + "<br>");

        /* This part might be optional. Depends on wheter a command already added exception is thrown or not
         mSFitSysCntrl.getFitProCntrl().removeCmd(sendKeyCmd);
         Thread.sleep(1000); */
            currentSpeed = hCmd.getSpeed(); //TODO: use actual speed once speed is accurate
            appendMessage("Current speed is: " + currentSpeed + " kph<br>");
            results += "Current speed is: " + currentSpeed + " kph\n";

            if (currentSpeed == ((double) Math.round(expected * 10) / 10)) {   //Need to round off to the nearest tenth, because of the ultra-precision of doubles
                appendMessage("<br><font color = #00ff00>* PASS *</font><br><br> Speed Down button decremented by 0.1 <br><br>");

                results += "\n* PASS *\n\nSpeed Down button decremented by 0.1\n";

            } else {
                appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br> Speed Down button did not decrement by 0.1 <br><br>");

                results += "\n* FAIL *\n\n Speed Down button did not decrement by 0.1\n";

            }
        }

        return results;
    }

    @Override
    public String runAll() throws Exception {
        //Redmine Support #925

        String keysResults="";

        keysResults += testStopKey();
        keysResults += testStartKey();
        keysResults += testInclineUpKey();
        keysResults += testInclineDownKey();
//        keysResults += testQuickInclineKeys();
        keysResults += testSpeedUpKey();
        keysResults += testSpeedDownKey();
//        keysResults += testQuickSpeedKeys();
//        keysResults += testAgeUpKey();
//        keysResults += testAgeDownKey();

        return keysResults;
    }
}
