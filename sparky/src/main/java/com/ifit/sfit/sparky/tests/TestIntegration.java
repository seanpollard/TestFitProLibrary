package com.ifit.sfit.sparky.tests;

import com.ifit.sfit.sparky.helperclasses.CommonFeatures;
import com.ifit.sfit.sparky.helperclasses.HandleCmd;
import com.ifit.sfit.sparky.helperclasses.SFitSysCntrl;
import com.ifit.sfit.sparky.testsdrivers.BaseTest;
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
  * Created by jc.almonte on 7/2/14.
  */
 public class TestIntegration extends CommonFeatures {
     private FecpController mFecpController;
     private BaseTest mAct;
     private HandleCmd hCmd;
     private SFitSysCntrl mSFitSysCntrl;
     private SystemDevice MainDevice;
     private  FecpCommand wrCmd;
     private  FecpCommand rdCmd;
    private String emailAddress;

    public TestIntegration(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
         //Get controller sent from the main activity (TestApp)
         try {
             this.mFecpController = fecpController;
             this.mAct = act;
             this.mSFitSysCntrl = ctrl;
             this.emailAddress = "jc.almonte@iconfitness.com";
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

    /**
     * Verifies valid age values can be set as well as invalid age values cant be set
     * @return text log of test results
     * @throws Exception
     */
     public String testAge() throws Exception {
         //Redmine Support #937
         //Read the default Age
         //Set the Age
         //Read the Age
         //Validate the Age
         //Repeat 6 times with values in increments of 5 years
 
         System.out.println("NOW RUNNING AGE TEST<br>");
 
         String ageResults;
 
         ageResults = "\n\n------------------------AGE TEST RESULTS------------------------\n\n";
         ageResults += Calendar.getInstance().getTime() + "\n\n";

         appendMessage("<br><br>------------------------AGE TEST RESULTS------------------------<br><br>");
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
 
         double age;
         double prevAge;
         double timeOfTest = 0; //how long test took in seconds
         long startTestTimer = System.nanoTime();
 
         age = hCmd.getAge();
         ageResults += "The default age is set to " + age + " years old\n";

         appendMessage("The default age is set to " + age + " years old<br>");
 
         //Set age to min=18 and increment by 1 up to maxage= 95
 
         int failureCounter = 0;
         long elapsedTime = 0;
         int i;
         long startTime;

         for(i = 18; i <=95; i++) {
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.AGE, i);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             age = hCmd.getAge();
             startTime = System.currentTimeMillis();
             //Keep reading the value until is the on you set it too or until it has try for long enough (25ms) that
             // we can conclude the reading has failed
             while(age!=i && elapsedTime < 25){
                 age = hCmd.getAge();
                 elapsedTime = System.currentTimeMillis() - startTime;
                 }
             System.out.println(elapsedTime);
 
             ageResults += "Status of setting the Age to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

             appendMessage("Status of setting the Age to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

             if(age == i){
                 ageResults += "\n* PASS *\n\n";
                 ageResults += "Current Age is set to: " + age + " years old (age should really be " + i + ")\n";

                 appendMessage("<br>* PASS *<br><br>");
                 appendMessage("Current Age is set to: " + age + " years old (age should really be " + i + ")<br>");
                 failureCounter++;
             }
             else{
                 ageResults += "\n<font color = #ff0000>* FAIL *</font>\n\n";
                 ageResults += "Current Age is set to: " + age + " years old, but should be set to: " + i + " years old\n";

                 appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                 appendMessage("Current Age is set to: " + age + " years old, but should be set to: " + i + " years old<br>");
             }
             mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
         }


         appendMessage("<br><br>---------------Out of Range Age Values------------------<br><br>");
         ageResults+="\n\n---------------Out of Range Age Values------------------\n\n";

//         for(i = 1; i <=105; i++) {
//             if(i==18)
//             {
//                 i=96;
//             }
//             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.AGE, i);
//             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//             age = hCmd.getAge();
//             startTime = System.currentTimeMillis();
//             //Keep reading the value until is the on you set it too or until it has try for long enough (25ms) that
//             // we can conclude the reading has failed
//             while(age!=i && elapsedTime < 25){
//                 age = hCmd.getAge();
//                 elapsedTime = System.currentTimeMillis() - startTime;
//             }
//             System.out.println(elapsedTime);
//
//             ageResults += "Status of setting the Age to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
//
//             appendMessage("Status of setting the Age to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
//
//             if(age !=i) // i is invalid age so it should not have been written
//             {
//                 ageResults += "\n* PASS *\n\n";
//                 ageResults += "Current age is set to: " + age + " years. Invalid value " + i + " was not written!\n";
//
//                 appendMessage("<br>* PASS *<br><br>");
//                 appendMessage("Current age is set to: " + age + " years. Invalid value " + i + " was not written!\n");
//             }
//             else{
//                 ageResults += "\n<font color = #ff0000>* FAIL *</font>\n\n";
//                 ageResults += "Current age is set to: " + age + " which matches invalid value " + i + " \n";
//
//                 appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
//                 appendMessage("Current age is set to: " + age + " which matches invalid value " + i + " \n");
//
//             }
//             mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
//         }
         timeOfTest = System.nanoTime() - startTestTimer;
         timeOfTest = timeOfTest / 1.0E09;

         appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
         ageResults+="\nThis test took a total of "+timeOfTest+" secs \n";
         return ageResults;
     }

    /**
     * Verifies valid weight ranges can be set as well as invalid weight values cant be set
     * @return text log of test results
     * @throws Exception
     */

     public String testWeight() throws Exception {
         //Weight is implemented in kilograms with a default weight of 185 lbs =84 kg
         //Redmine Support #942
         //Read the default Weight
         //Set a Weight
         //Read the Weight
         //Validate the Weight
         //Repeat 6 times with different values
         String weightResults;
         System.out.println("NOW RUNNING WEIGHT TEST<br>");
 
         weightResults = "\n\n------------------------WEIGHT TEST RESULTS------------------------\n\n";
         weightResults += Calendar.getInstance().getTime() + "\n\n";
         appendMessage("<br><br>------------------------WEIGHT TEST RESULTS------------------------<br><br>");
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
         double weight;
         double timeOfTest = 0; //how long test took in seconds
         long startTestTimer = System.nanoTime();
 
         weight = hCmd.getWeight();
         weightResults += "The default weight is set to " + weight + " kilograms\n";
         appendMessage("The default weight is set to " + weight + " kilograms<br>");

         double diff;
 
         //Set weight to 50 kg and increment by 10 up to 175 kg (max is 400lbs = 181 kg)
         for(double i = 45.35; i <=175; i+=10) {
 
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WEIGHT, i);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             //need more time for weight controller
             Thread.sleep(1000);
 
             weightResults += "\nStatus of setting the Weight to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
             appendMessage("<br>Status of setting the Weight to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

             weight = hCmd.getWeight();
             diff = Math.abs(weight - i);
 
           if(diff < i*.01) // if values are within 1% of each other
           {
                 weightResults += "\n* PASS *\n\n";
                 weightResults += "Current Weight is set to: " + weight + " kilograms should be set to: " + i + " kilograms\n";
                 weightResults+="set and read values have a difference of "+diff+" which is within 1% tolerance\n";

               appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
               appendMessage("Current Weight is set to: " + weight + " kilograms should be set to: " + i + " kilograms<br>");
               appendMessage("set and read values have a difference of "+diff+" which is within 1% tolerance<br>");


           }
             else{
                 weightResults += "\n<font color = #ff0000>* FAIL *</font>\n\n";
                 weightResults += "Current Weight is set to: " + weight + " kilograms, but should be set to: " + i + " kilograms\n";
                 weightResults+="set and read values have a difference of "+diff+" which is outside the 1% tolerance\n";

               appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
               appendMessage("Current Weight is set to: " + weight + " kilograms, but should be set to: " + i + " kilograms<br>");
               appendMessage("set and read values have a difference of "+diff+" which is outside the 1% tolerance<br>");

           }
         }
         appendMessage("<br><br>---------------Out of Range Weight Values------------------<br><br>");
         weightResults+="\n\n---------------Out of Range Weight Values------------------\n\n";
         //Set weight to 1 kg and increment by 5
//         for(double i = 0; i <=220; i+=5) {
//
//             //Jump to beyond max invalid weights
//             if(i >= 45)
//             {
//                 i=181;
//             }
//             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WEIGHT, i);
//             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
//             //need more time for weight controller
//             Thread.sleep(1000);
//
//             weightResults += "\nStatus of setting the Weight to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
//             appendMessage("<br>Status of setting the Weight to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
//
//             weight = hCmd.getWeight();
//
//             if(weight !=i) // i is invalid weight so it should not have been written
//             {
//                 weightResults += "\n* PASS *\n\n";
//                 weightResults += "Current Weight is set to: " + weight + " kilograms. Invalid value " + i + " kilograms was not written!\n";
//
//                 appendMessage("<br>* PASS *<br><br>");
//                 appendMessage("Current Weight is set to: " + weight + " kilograms. Invalid value " + i + " kilograms was not written!\n");
//             }
//             else{
//                 weightResults += "\n<font color = #ff0000>* FAIL *</font>\n\n";
//                 weightResults += "Current Weight is set to: " + weight + " which matches invalid value " + i + " kilograms\n";
//
//                 appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
//                 appendMessage("Current Weight is set to: " + weight + " which matches invalid value " + i + " kilograms\n");
//
//             }
//         }
         mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
         timeOfTest = System.nanoTime() - startTestTimer;
         timeOfTest = timeOfTest / 1.0E09;

         appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
         weightResults+="\nThis test took a total of "+timeOfTest+" secs \n";
         return weightResults;
     }

    /**
     * Verifies branboard config values are correct for the corresponding console
     * @param inputString the string containing the values to check against
     * @return text log of test results
     * @throws Exception
     */
     public String testSystemConfiguration(String inputString) throws Exception{
         //outline for code support #951
         //read System Config data from Brainboard
         //try to output all values from System Device and Device Info
         String titleString;
         String systemString = "";
 
         titleString = "\n----------------------SYSTEM CONFIGURATION TEST----------------------\n\n";
         appendMessage("<br>----------------------SYSTEM CONFIGURATION TEST----------------------<br><br>");
         titleString += Calendar.getInstance().getTime() + "\n\n";
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
 
         double maxIncline;
         double minIncline;
         double maxSpeed;
         double minSpeed;
         double timeOfTest = 0; //how long test took in seconds
         long startTestTimer = System.nanoTime();

         maxIncline = hCmd.getMaxIncline();
         minIncline = hCmd.getMinIncline();
 
         maxSpeed = hCmd.getMaxSpeed();
         minSpeed = hCmd.getMinSpeed();
         Thread.sleep(1000);

         String brainboardLines[] = new String[11];
         //Split the user's input string into separate strings to be compared, line by line ("<br>" is the delimiter)
         String inputLines[] = inputString.split("\r?\n|\r");
 
         brainboardLines[0] = "Console Name: \"" + MainDevice.getSysDevInfo().getConsoleName() + "\"";
         brainboardLines[1] = "Model Number: \"" + MainDevice.getSysDevInfo().getModel() + "\"";
         brainboardLines[2] = "Part Number: \"" + MainDevice.getSysDevInfo().getPartNumber() + "\"";
         brainboardLines[3] = "Software Version: \"" + MainDevice.getInfo().getSWVersion() + "\"";
         brainboardLines[4] = "Hardware Version: \"" + MainDevice.getInfo().getHWVersion() + "\"";
         brainboardLines[5] = "Serial Number: \"" + MainDevice.getInfo().getSerialNumber() + "\"";
         brainboardLines[6] = "Manufacturing Number: \"" + MainDevice.getInfo().getManufactureNumber() + "\"";
         brainboardLines[7] = "Max Incline: \"" + maxIncline + "\"";
         brainboardLines[8] = "Min. Incline: \"" + minIncline + "\"";
         brainboardLines[9] = "Max Speed: \"" + maxSpeed + "\"";
         brainboardLines[10] = "Min Speed: \"" + minSpeed + "\"";
 
         //Comparing the User-entered configuration values (from PDM?) with what is stored on the Brainboard
         for(int i = 0; i < brainboardLines.length; i++)
         {
             if(brainboardLines[i].equals(inputLines[i]))
             {
                 systemString += "\nBrainboard " + brainboardLines[i] + "\nKeyboard Input " + inputLines[i] + "\n\n* PASS *\n\n";
                 appendMessage("<br>Brainboard " + brainboardLines[i] + "<br>Keyboard Input " + inputLines[i] + "<br><font color = #00ff00>* PASS *</font><br><br>");

             }
             else
             {
                 systemString += "\nBrainboard " + brainboardLines[i] + "\n Keyboard Input " + inputLines[i] + "\n\n* FAIL *\n\n";
                 appendMessage("<br>Brainboard " + brainboardLines[i] + "<br>Keyboard Input " + inputLines[i] + "<br><br><font color = #ff0000>* FAIL *</font><br><br>");

             }
         }
 
         systemString = titleString + systemString;

         timeOfTest = System.nanoTime() - startTestTimer;
         timeOfTest = timeOfTest / 1.0E09;

         appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
         systemString+="\nThis test took a total of "+timeOfTest+" secs \n";
         return systemString;
     }

    /**
     * Verifies that console switches from PAUSE to RESULTS after an elapsed time specified by PAUSE_TIMEOUT
     * and from RESULTS to IDLE after an elapsed time specified by IDLE_TIMEOUT
     * @return text log of test results
     * @throws Exception
     */
     public String testPauseIdleTimeout() throws Exception{
         //part of redmine #930
         //Set mode to Pause
         //Delay for 60 seconds
         //Verify Pause timeout by reading the mode and ensuring it is in Results mode
         System.out.println("NOW PAUSE/IDLE TIMEOUT TEST<br>");
         String results = "";
         double pauseTimeout;
         double idleTimeout;
         //setPauseTimeout and setIdleTimeout arrays are parallel arrays so they should be same length and contain same amount of data
         double [] setPauseTimeout ={30,15};
         double [] setIdleTimeout = {10,5};
         String prevMode="";
         double timeOfTest = 0; //how long test took in seconds
         long startTestTimer = System.nanoTime();

         appendMessage("<br><br>------------------------PAUSE TIMEOUT TEST RESULTS------------------------<br><br>");
         results += "\n\n------------------------PAUSE/IDLE TIMEOUT TEST RESULTS------------------------\n\n";
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
         results+=Calendar.getInstance().getTime() + "\n\n";

         appendMessage("Current pause timeout is: "+hCmd.getPauseTimeout()+"<br>");
         results+="Current pause timeout is: "+hCmd.getPauseTimeout()+"\n";
         appendMessage("Current idle timeout is: "+hCmd.getIdleTimeout()+"<br>");
         results+="Current idle timeout is: "+hCmd.getIdleTimeout()+"\n";
         appendMessage("Current Mode: " + hCmd.getMode()+"<br>");
         results+="Current Mode: " + hCmd.getMode()+"\n";


         //Set pause timeout to 30 secs
         for(int i = 0; i<setPauseTimeout.length; i++) {
             appendMessage("Setting the pause timeout to " + setPauseTimeout[i] + " secs...<br>");
             results += "Setting the pause timeout to "+ setPauseTimeout[i] + " secs...\n";
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.PAUSE_TIMEOUT, setPauseTimeout[i]);//set pause timeout to 30 secs
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(1000);
             appendMessage("<br>Status of setting the Pause timeout to "+ setPauseTimeout[i] +" secs:  " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results += "\nStatus of setting the Pause timeout to "+ setPauseTimeout[i] +" secs:  " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

             appendMessage("Setting the Idle timeout to " + setIdleTimeout[i] + " secs...<br>");
             results += "Setting the Idle timeout to "+ setIdleTimeout[i] + " secs...\n";
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.IDLE_TIMEOUT, setIdleTimeout[i]);//set pause timeout to 30 secs
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(1000);
             appendMessage("<br>Status of setting the Idle timeout to "+ setIdleTimeout[i] +" secs:  " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results += "\nStatus of setting the Idle timeout to "+ setIdleTimeout[i] +" secs:  " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
             pauseTimeout = hCmd.getPauseTimeout();
             idleTimeout = hCmd.getIdleTimeout();
             appendMessage("New pause timeout is: " + pauseTimeout + "<br>");
             results += "New pause timeout is: " + pauseTimeout + "\n";
             appendMessage("New Idle timeout is: " + idleTimeout + "<br>");
             results += "New Idle timeout is: " + idleTimeout + "\n";

             //Set mode to Running
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(1000);


             appendMessage("Status of setting the Mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results += "Status of setting the Mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
             appendMessage("Current Mode: " + hCmd.getMode() + "<br>");
             results += "Current Mode: " + hCmd.getMode() + "\n";

             //Set mode to Pause
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             Thread.sleep(1000);

             appendMessage("Status of setting the Mode to Pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
             results += "Status of setting the Mode to Pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
             appendMessage("Current Mode: " + hCmd.getMode() + "<br>");
             results += "Current Mode: " + hCmd.getMode() + "\n";

             //Check each second to see if mode has changed from Pause mode. Also prevents from waiting for longer than the timeout if it doesn't work
             prevMode = hCmd.getMode().getDescription();
             for (long totalTime = 0; totalTime < pauseTimeout + 5; totalTime++) {
                 Thread.sleep(1000);
                 appendMessage("after " + totalTime + " sec(s)the mode is  " + hCmd.getMode().getDescription() + "<br>");
                 results += "after " + totalTime + " sec(s)the mode is  " + hCmd.getMode().getDescription() + "\n";
                 if (hCmd.getMode().getDescription() != "Pause Mode") {
                     Thread.sleep(1000);//give time for mode to update before reading it
                     appendMessage("<br>The mode changed from " + prevMode + " to " + hCmd.getMode().getDescription() + " after " + totalTime + " seconds<br>");
                     results += "\nThe mode changed from " + prevMode + " to " + hCmd.getMode().getDescription() + " after " + totalTime + " seconds\n";
                     break;
                 }
             }
             if (hCmd.getMode().getDescription() != "Pause Mode") {
                 appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                 results += "\n* PASS *\n\n";
                 appendMessage("Pause Mode timed out to " + hCmd.getMode().getDescription() + "<br>");
                 results += "\nPause Mode timed out to " + hCmd.getMode().getDescription() + "\n";
             } else {
                 appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                 results += "\n* FAIL *\n\n";
                 appendMessage("Pause Mode did not time out after " + pauseTimeout + " seconds\n");
                 results += "\nPause Mode did not time out after " + pauseTimeout + " seconds\n";
             }
             //Now wait for the IDLE timeout to happen and change mode from RESULTS to IDLE
             //TODO: verify that bitfields are resetting after IDLE timeout
             prevMode = hCmd.getMode().getDescription();
             for (long totalTime = 0; totalTime < idleTimeout + 5; totalTime++) {
                 Thread.sleep(1000);
                 appendMessage("after " + totalTime + " sec(s)the mode is  " + hCmd.getMode().getDescription() + "<br>");
                 results += "after " + totalTime + " sec(s)the mode is  " + hCmd.getMode().getDescription() + "\n";
                 if (hCmd.getMode().name() != "RESULTS") {
                     Thread.sleep(1000);//give time for mode to update before reading it
                     appendMessage("<br>The mode changed from " + prevMode + " to " + hCmd.getMode().getDescription() + " after " + totalTime + " seconds<br>");
                     results += "\nThe mode changed from " + prevMode + " to " + hCmd.getMode().getDescription() + " after " + totalTime + " seconds\n";
                     break;
                 }
             }

             if (hCmd.getMode().name() != "RESULTS") {
                 appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
                 results += "\n* PASS *\n\n";
                 appendMessage("Results Mode timed out to " + hCmd.getMode().getDescription() + "<br>");
                 results += "Results Mode timed out to " + hCmd.getMode().getDescription() + "\n";
             } else {
                 appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
                 results += "\n* FAIL *\n\n";
                 appendMessage("Results mode did not time out after " + idleTimeout + " seconds<br>");
                 results += "Results mode did not time out after " + idleTimeout + " seconds\n";
             }
         }
         timeOfTest = System.nanoTime() - startTestTimer;
         timeOfTest = timeOfTest / 1.0E09;

         appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
         results+="\nThis test took a total of "+timeOfTest+" secs \n";
         return results;
     }

    /**
     * Runs a workout for an specifed amount of time and checks that the running time recorded
     * by the console is accurate
     * @param runType if "m" run marathon workout (4 hours), else a normal workout.
     * @return text log of test results
     * @throws Exception
     */
     public String testRunningTime(String runType) throws Exception{
         //outline for code support #930 in redmine
         
         String results;

         long runtime; //time for running test (in secs)
         long pauseruntime; //time for running test with pause in secs
        //Maybe test later 5K, 10K, Half-Marathon, Full Marathon cases
        switch (runType)
        {
            case "m":
                runtime = 14400; // 4 hours (in secs)
                pauseruntime=1800; // Half an hour
            break;

            default:
                runtime = 60; // 60 secs
                pauseruntime=23; // 23 secs
            break;
        }
         double timeOfTest = 0; //how long test took in seconds
         long startTestTimer = System.nanoTime();
         System.out.println("RUNNING-TIME TEST<br>");
 
 
         appendMessage("<br><br>------------------------RUNNING TIME TEST RESULTS------------------------<br><br>");
         results="\n\n------------------------RUNNING TIME TEST RESULTS------------------------\n\n";
 
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
         results+= Calendar.getInstance().getTime() + "\n\n";

         appendMessage("Current pause timeout is: "+hCmd.getPauseTimeout()+"<br>");
         results+="Current pause timeout is: "+hCmd.getPauseTimeout()+"\n";

         appendMessage("Current Mode: "+hCmd.getMode()+"<br>");
         results+="Current Mode: "+hCmd.getMode()+"\n";


         //set the pause timeout to 60
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.PAUSE_TIMEOUT,60);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
         appendMessage("Status of setting pause timeout to 60 secs: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("New pause timeout is: "+hCmd.getPauseTimeout()+"<br>");
         results+="Status of setting pause timeout to "+runtime+" secs: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         results+="New pause timeout is: "+hCmd.getPauseTimeout()+"\n";
         //set the mode to running
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(100);
         appendMessage("Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("Now wait "+runtime+ " seconds...<br>");
         results+="Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         results+="Now wait "+runtime+ " seconds...\n";
         Thread.sleep(runtime*1000);

         //wait 1 minute
 //        long elapsedTime = 0;
 //        double seconds = 0;
 //        long startime = System.nanoTime();
 //        //Check the time elasped constanly until one minute has passed
 //        for(double i = 0; i < 59; i=seconds)
 //        {
 //            elapsedTime = System.nanoTime() - startime;
 //            seconds = elapsedTime / 1.0E09;
 //        }
         //read the running time
 
         double timeOfRunningTest = hCmd.getRunTime();
 
         //Test whether the running time is within +/- 2 second of 60 seconds (allow for 1 sec read time)
         if(timeOfRunningTest >= runtime-2 && timeOfRunningTest <= runtime+2) {
             appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br>");
             results+="\n\n* PASS *\n\n";
         }
         else {
             appendMessage("<br><br><font color = #ff0000>* FAIL *</font><br><br>");
             results+="\n\n* FAIL *\n\n";
         }
 
         appendMessage("The running time for this "+runtime+" second test was " + timeOfRunningTest + " seconds<br>");
         results+="The running time for this "+runtime+" second test was " + timeOfRunningTest + " seconds\n";
         //set mode back to Pause to stop the test
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
 
         //set mode back to Results
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RESULTS);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
 
         //set mode back to IDLE to reset running time
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
 
         appendMessage("<br>Pause Test: <br>");
         results+="\nPause Test: \n";
         //start pause test
         //start running
         //set the mode to running
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(100);
         appendMessage("Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("Now wait "+pauseruntime+ " seconds...<br>");
         results+="Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         results+="Now wait "+pauseruntime+ " seconds...\n";
         Thread.sleep(pauseruntime*1000);
         //wait 30 seconds
 //        startime = System.nanoTime();
 //        //Check the time elasped constanly until 30 secs have passed
 //        for(double i = 0; i < 29; i=seconds)
 //        {
 //            elapsedTime = System.nanoTime() - startime;
 //            seconds = elapsedTime / 1.0E09;
 //        }
         //pause the test
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(100);
         appendMessage("Status of setting mode to pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("wait 30 seconds...<br>");
         results+="Status of setting mode to pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         results+="wait 30 seconds...\n";
         //wait 30 seconds
         Thread.sleep(30000);
         //go from pause to running
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(100);
         appendMessage("Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("Now wait "+pauseruntime+ " seconds...<br>");
         results+="Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         results+="Now wait "+pauseruntime+ " seconds...\n";
         Thread.sleep(pauseruntime*1000);
         //wait another 30 seconds for a total of 1 minute expected running time
 //        startime = System.nanoTime();
 //        //Check the time elasped constanly until 30 secs have passed
 //        for(double i = 0; i < 29; i=seconds)
 //        {
 //            elapsedTime = System.nanoTime() - startime;
 //            seconds = elapsedTime / 1.0E09;
 //        }
 
 
         double timeOfPauseTest = hCmd.getRunTime();
         pauseruntime*=2; // This time is used twice
         //Test whether the running time is within +/- 2 seconds of 55 seconds
         if(timeOfPauseTest >= pauseruntime-2 && timeOfPauseTest <= pauseruntime+2){
             appendMessage("<br><br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("The total time for this "+pauseruntime+ " sec test with 30 sec pause correctly ran for " + timeOfPauseTest + " secs<br><br>");
             results+="\n\n* PASS *\n\n";
             results+="The total time for this "+pauseruntime+ " sec test with 30 sec pause correctly ran for " + timeOfPauseTest + " secs\n\n";
     }
         else {
             appendMessage("<br><br><font color = #ff0000>* FAIL *</font><br><br>");
             appendMessage("The total time for this "+pauseruntime+" sec test with 30 sec pause actually ran for " + timeOfPauseTest +"<br><br>");
             results+="\n\n* FAIL *\n\n";
             results+="The total time for this "+pauseruntime+ " sec test with 30 sec pause actually ran for " + timeOfPauseTest + " secs\n\n";

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
         timeOfTest = System.nanoTime() - startTestTimer;
         timeOfTest = timeOfTest / 1.0E09;

         appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
         results+="\nThis test took a total of "+timeOfTest+" secs \n";
         return results;
     }
 
     //--------------------------------------------//
     //                                            //
     //              Testing Max Speed             //
     //                                            //
     //--------------------------------------------//

    /**
     * Records the amount of time it takes to go from 0 to max speed
     * @return text log of test results
     * @throws Exception
     */

     public String testMaxSpeedTime() throws Exception{
         //outline for code #1051 in redmine
         //look up max speed for device (currently is not implemented - so just going to use 20kph)
         //send basic start command to start motor at on position
         //start stopwatch timer
         //send command to change speed to max speed
         //read current speed until actual is the same as target
         //stop stopwatch and return/display/record the value of the stopwatch
         
         double maxSpeed;
         double currentActualSpeed = 0;
         double timeOfTest = 0; //how long test took in seconds
         long startTestTimer = System.nanoTime();

         System.out.println("NOW RUNNING MAX SPEED TIME TEST<br>");
         String results="";
         appendMessage("<br>--------------------------MAX SPEED TEST--------------------------<br><br>");
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
         results+="\n--------------------------MAX SPEED TEST--------------------------\n\n";
         results+=Calendar.getInstance().getTime() + "\n\n";
         //TODO: Once Max Speed command is implemented, just change the constant MAX_SPEED to the maxSpeed variable (which reads the value off of the Brainboard)
 
         maxSpeed = hCmd.getMaxSpeed();
         Thread.sleep(1000);
         System.out.println("The max speed is " + maxSpeed+" kph but our console only reaches up to 16 kph");
 
         //start timer
         //set mode to running
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
         appendMessage("Status of setting mode to running " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("setting speed to 16 kph...<br>");
         results+="Status of setting mode to running " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";
         results+="setting speed to 16 kph...\n";
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.KPH, 16.0);//replace literal by maxSpeed later on
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
         appendMessage("Status of speed to 16kph: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         results+="Status of speed to 16kph: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "\n";

         long elapsedTime = 0;
         double seconds = 0;
         long startime = System.nanoTime();
         //Read the actual speed and count elsaped time. Do this until speed has reached MAX
         while(currentActualSpeed < 16) // Replace 16 by maxSpeed once we have a motor that can reach more then 16 kph
         {
             currentActualSpeed = hCmd.getActualSpeed();
             elapsedTime = System.nanoTime() - startime;
             seconds = elapsedTime / 1.0E09;
             Thread.sleep(1000);
             appendMessage("actual speed "+currentActualSpeed+" elapsed time " + seconds +" seconds<br>");
             results+="actual speed "+currentActualSpeed+" elapsed time " + seconds +" seconds\n";
         }


         appendMessage("The max speed is " + maxSpeed + "<br>");
         appendMessage("The motor took " + seconds + " seconds to go to max speed<br>");
         results+="The max speed is " + maxSpeed + "\n";
         results+="The motor took " + seconds + " seconds to go to max speed\n";
 
         if(maxSpeed < 10){
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The Max Speed was not properly read from the brainboard (Max Speed: "+maxSpeed+" kph)<br>");
             results+="\n<font color = #ff0000>* FAIL *</font>\n\nThe Max Speed was not properly read from the brainboard (Max Speed: "+maxSpeed+" kph)\n";

         }
         //TODO: Calculate seconds for pass/pail standard based on Max Speed. For example a 10mph max speed unit might reach max speed quicker than a 15mph max speed unit
         //%5 pass standard with a 23 sec spec from #59
         if((seconds <= 22) || (seconds >= 24)) {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The motor was off by " + (seconds - 23) + " seconds<br>");
             results+="\n* FAIL *\n\nThe motor was off by " + (seconds - 23) + " seconds\n";
         }
 
         else {
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("The Max Speed was correctly read off of the brainboard and the speed up to Max Speed took "+
                     seconds+" seconds, which is within the 5% tolerance<br>");

             results+="\n* PASS *\n\n";
             results+="The Max Speed was correctly read off of the brainboard and the speed up to Max Speed took "+
                     seconds+" seconds, which is within the 5% tolerance\n";
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

         timeOfTest = System.nanoTime() - startTestTimer;
         timeOfTest = timeOfTest / 1.0E09;

         appendMessage("<br>This test took a total of "+timeOfTest+" secs <br>");
         results+="\nThis test took a total of "+timeOfTest+" secs \n";
         return results;
     }

    /**
     * Runs all Integration tests
     * @return text log of test results
     * @throws Exception
     */
    @Override
     public String runAll() {
        String results = "";
         try {
            // this.testAge();
             results+=this.testMaxSpeedTime();
            // this.testWeight();
             results+=this.testRunningTime(" ");
             results+=this.testPauseIdleTimeout();
         }
         catch (Exception ex) {
             ex.printStackTrace();
         }
         return results;
     }
 
 
 }
