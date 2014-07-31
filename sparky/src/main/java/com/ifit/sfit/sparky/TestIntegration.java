package com.ifit.sfit.sparky;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;
 import com.ifit.sparky.fecp.FecpCommand;
 import com.ifit.sparky.fecp.SystemDevice;
 import com.ifit.sparky.fecp.communication.FecpController;
 import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
 import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
 import com.ifit.sparky.fecp.interpreter.command.CommandId;
 import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
 import com.ifit.sparky.fecp.interpreter.device.Device;
 
 import java.nio.ByteBuffer;
 import java.util.Calendar;

/**
  * Created by jc.almonte on 7/2/14.
  */
 public class TestIntegration extends TestCommons implements TestAll {
     private FecpController mFecpController;
     private BaseTest mAct;
     private HandleCmd hCmd;
     private SFitSysCntrl mSFitSysCntrl;
     private SystemDevice MainDevice;
     private  FecpCommand wrCmd;
     private  FecpCommand rdCmd;
 
     public TestIntegration(FecpController fecpController, BaseTest act, SFitSysCntrl ctrl) {
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
     //
     //Testing Age
     //
     //--------------------------------------------//

     public String testAge() throws Exception {
         //Redmine Support #937
         //Read the default Age
         //Set the Age
         //Read the Age
         //Validate the Age
         //Repeat 6 times with values in increments of 5 years
 
         System.out.println("NOW RUNNING AGE TEST<br>");
 
         String ageResults;
 
         ageResults = "<br><br>------------------------AGE TEST RESULTS------------------------<br><br>";
         ageResults += Calendar.getInstance().getTime() + "<br><br>";
 
         double age;
         double prevAge;
 
 
         age = hCmd.getAge();
         ageResults += "The default age is set to " + age + " years old<br>";
 
         //Set age to min=18 and increment by 1 up to maxage= 95
 
         int failureCounter = 0;
         long elapsedTime = 0;
         int i;
         long startTime;
         for(i = 18; i <=95; i+=1) {
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
 
             ageResults += "Status of setting the Age to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>";
 
            /*
             if(age == 80)
             {
                 long elapsedTime = System.currentTimeMillis() - startTime;
                 System.out.println(elapsedTime);
             }
     */
             if(age == i){
 //            if(age == 13){
                 ageResults += "<br>* PASS *<br><br>";
                 ageResults += "Current Age is set to: " + age + " years old (age should really be " + i + ")<br>";
                 failureCounter++;
             }
             else{
                 ageResults += "<br><font color = #ff0000>* FAIL *</font><br><br>";
                 ageResults += "Current Age is set to: " + age + " years old, but should be set to: " + i + " years old<br>";
             }
             mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
         }
         return ageResults;
     }
     //--------------------------------------------//
     //
     //Testing Weight
     //
     //--------------------------------------------//
 
     /*
     TODO: Future test can include testing invalid weights (MAX_WEIGHT< weight < MIN_WEIGHT) and validating default weight
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
 
         weightResults = "<br><br>------------------------WEIGHT TEST RESULTS------------------------<br><br>";
         weightResults += Calendar.getInstance().getTime() + "<br><br>";
 
         double weight;
 
 
         weight = hCmd.getWeight();
         weightResults += "The default weight is set to " + weight + " kilograms<br>";
         double diff;
 
         //Set weight to 50 kg and increment by 10 up to 175 kg (max is 400lbs = 181 kg)
         for(double i = 45.35; i <=175; i+=10) {
 
             ((WriteReadDataCmd) wrCmd.getCommand()).addWriteData(BitFieldId.WEIGHT, i);
             mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
             //need more time for weight controller
             Thread.sleep(1000);
 
             weightResults += "<br>Status of setting the Weight to " + i + ": " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>";
 
             weight = hCmd.getWeight();
             diff = Math.abs(weight - i);
 
           if(diff < i*.01) // if values are within 1% of each other
           {
                 weightResults += "<br>* PASS *<br><br>";
                 weightResults += "Current Weight is set to: " + weight + " kilograms should be set to: " + i + " kilograms<br>";
                 weightResults+="set and read values have a difference of "+diff+" which is within 1% tolerance<br>";
             }
             else{
                 weightResults += "<br><font color = #ff0000>* FAIL *</font><br><br>";
                 weightResults += "Current Weight is set to: " + weight + " kilograms, but should be set to: " + i + " kilograms<br>";
                 weightResults+="set and read values have a difference of "+diff+" which is outside the 1% tolerance<br>";
 
           }
         }
         mSFitSysCntrl.getFitProCntrl().removeCmd(wrCmd);
         return weightResults;
     }
 
     //--------------------------------------------//
     //
     //Testing System Configuration
     //
     //--------------------------------------------//
     public String testSystemConfiguration(String inputString) throws Exception{
         //outline for code support #951
         //read System Config data from Brainboard
         //try to output all values from System Device and Device Info
         String titleString;
         String systemString = null;
 
         titleString = "<br>----------------------SYSTEM CONFIGURATION TEST----------------------<br><br>";
         titleString += Calendar.getInstance().getTime() + "<br><br>";
 
         double maxIncline;
         double minIncline;
         double maxSpeed;
         double minSpeed;
 
         maxIncline = hCmd.getMaxIncline();
         minIncline = hCmd.getMinIncline();
 
         maxSpeed = hCmd.getMaxSpeed();
         minSpeed = hCmd.getMinSpeed();
         Thread.sleep(1000);
 
         //Need a new Device object for some of the device info
         Device device = new Device();
         String brainboardLines[] = new String[11];
         //Split the user's input string into separate strings to be compared, line by line ("<br>" is the delimiter)
         String inputLines[] = inputString.split("\r?<br>|\r");
 
         brainboardLines[0] = "Console Name: \"" + MainDevice.getSysDevInfo().getConsoleName() + "\"";
         brainboardLines[1] = "Model Number: \"" + MainDevice.getSysDevInfo().getModel() + "\"";
         brainboardLines[2] = "Part Number: \"" + MainDevice.getSysDevInfo().getPartNumber() + "\"";
         brainboardLines[3] = "Software Version: \"" + device.getInfo().getSWVersion() + "\"";
         brainboardLines[4] = "Hardware Version: \"" + device.getInfo().getHWVersion() + "\"";
         brainboardLines[5] = "Serial Number: \"" + device.getInfo().getSerialNumber() + "\"";
         brainboardLines[6] = "Manufacturing Number: \"" + device.getInfo().getManufactureNumber() + "\"";
         brainboardLines[7] = "Max Incline: \"" + maxIncline + "\"";
         brainboardLines[8] = "Min. Incline: \"" + minIncline + "\"";
         brainboardLines[9] = "Max Speed: \"" + maxSpeed + "\"";
         brainboardLines[10] = "Min Speed: \"" + minSpeed + "\"";
 
         //Comparing the User-entered configuration values (from PDM?) with what is stored on the Brainboard
         for(int i = 0; i < brainboardLines.length; i++)
         {
             if(brainboardLines[i].equals(inputLines[i]))
             {
                 systemString += "<br>Brainboard " + brainboardLines[i] + "<br>Keyboard Input " + inputLines[i] + "<br><font color = #00ff00>* PASS *</font><br><br>";
             }
             else
             {
                 systemString += "<br>Brainboard " + brainboardLines[i] + "<br> Keyboard Input " + inputLines[i] + "<br><br><font color = #ff0000>* FAIL *</font><br><br>";
             }
         }
 
         systemString = titleString + systemString;
 
         return systemString;
     }
     //--------------------------------------------//
     //
     //Testing Pause Timeout
     //
     //--------------------------------------------//
     public String testPauseIdleTimeout() throws Exception{
         //part of redmine #930
         //Set mode to Pause
         //Delay for 60 seconds
         //Verify Pause timeout by reading the mode and ensuring it is in Results mode
         System.out.println("NOW PAUSE/IDLE TIMEOUT TEST<br>");
 
         double pauseTimeout = 0;
         double idleTimeout = 0;
         String prevMode="";
 
         appendMessage("<br><br>------------------------PAUSE TIMEOUT TEST RESULTS------------------------<br><br>");
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
 
         appendMessage("Current pause timeout is: "+hCmd.getPauseTimeout()+"<br>");
         appendMessage("Current idle timeout is: "+hCmd.getIdleTimeout()+"<br>");
         appendMessage("Current Mode: " + hCmd.getMode()+"<br>");
 
 
         //Set pause timeout to 30 secs
         appendMessage("Setting the pause timeout to 30 secs...<br>");
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.PAUSE_TIMEOUT, 30);//set pause timeout to 30 secs
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
         appendMessage("<br>Status of setting the Pause timeout to 30 secs:  "+ (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
 
         pauseTimeout = hCmd.getPauseTimeout();
         idleTimeout = hCmd.getIdleTimeout();
         appendMessage("New pause timeout is: "+pauseTimeout+"<br>");
 
         //Set mode to Running
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
 
 
         appendMessage("Status of setting the Mode to Running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("Current Mode: "+hCmd.getMode()+"<br>");
 
         //Set mode to Pause
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
        mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
 
         appendMessage("Status of setting the Mode to Pause: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("Current Mode: "+hCmd.getMode()+"<br>");
 
         //Check each second to see if mode has changed from Pause mode. Also prevents from waiting for longer than the timeout if it doesn't work
         prevMode = hCmd.getMode().getDescription();
         for(long totalTime = 0; totalTime < pauseTimeout+5; totalTime++){
             Thread.sleep(1000);
             appendMessage("after " + totalTime +" sec(s)the mode is  " + hCmd.getMode().getDescription()+"<br>");
             if(hCmd.getMode().getDescription() != "Pause Mode"){
                 Thread.sleep(1000);//give time for mode to update before reading it
                 appendMessage("<br>The mode changed from " +prevMode+ " to " + hCmd.getMode().getDescription() + " after " + totalTime + " seconds<br>");
                 break;
             }
         }
         if(hCmd.getMode().getDescription() != "Pause Mode"){
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("Pause Mode timed out to " + hCmd.getMode().getDescription() + "<br>");
         }
         else{
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
             appendMessage("Pause Mode did not time out after "+pauseTimeout+ " seconds<br>");
         }
         //Now wait for the IDLE timeout to happen and change mode from RESULTS to IDLE
         //TODO: verify that bitfields are resetting after IDLE timeout
         prevMode = hCmd.getMode().getDescription();
         for(long totalTime = 0; totalTime < idleTimeout+5; totalTime++){
             Thread.sleep(1000);
             appendMessage("after " + totalTime +" sec(s)the mode is  " + hCmd.getMode().getDescription()+"<br>");
             if(hCmd.getMode().name() != "RESULTS"){
                 Thread.sleep(1000);//give time for mode to update before reading it
                 appendMessage("<br>The mode changed from " +prevMode+ " to " + hCmd.getMode().getDescription() + " after " + totalTime + " seconds<br>");
                 break;
             }
         }
 
         if(hCmd.getMode().name() != "RESULTS"){
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("Results Mode timed out to " + hCmd.getMode().getDescription() + "<br>");
         }
         else{
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>");
             appendMessage("Results mode did not time out after "+idleTimeout+" seconds<br>");
         }
 
         return res;
     }
 
     //--------------------------------------------//
     //
     //Testing Running Time
     //
     //--------------------------------------------//
     public String testRunningTime() throws Exception{
         //outline for code support #930 in redmine
         
         long runtime = 60; //time for running test (in secs)
         long pauseruntime = 23; //time for running test with pause
 
         System.out.println("RUNNING-TIME TEST<br>");
 
 
         appendMessage("<br><br>------------------------RUNNING TIME TEST RESULTS------------------------<br><br>");
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
 
         appendMessage("Current pause timeout is: "+hCmd.getPauseTimeout()+"<br>");
         appendMessage("Current Mode: "+hCmd.getMode()+"<br>");
 
         //set the pause timeout to 60
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.PAUSE_TIMEOUT,60);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
         appendMessage("Status of setting pause timeout to 60 secs: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("New pause timeout is: "+hCmd.getPauseTimeout()+"<br>");
 
         //set the mode to running
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(100);
         appendMessage("Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("Now wait "+runtime+ " seconds...<br>");
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
         }
         else {
             appendMessage("<br><br><font color = #ff0000>* FAIL *</font><br><br>");
         }
 
         appendMessage("The running time for this "+runtime+" second test was " + timeOfRunningTest + " seconds<br>");
 
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
         //start pause test
         //start running
         //set the mode to running
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE,ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(100);
         appendMessage("Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("Now wait "+pauseruntime+ " seconds...<br>");
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
         //wait 30 seconds
         Thread.sleep(30000);
         //go from pause to running
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(100);
         appendMessage("Status of setting mode to running: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");
         appendMessage("Now wait "+pauseruntime+ " seconds...<br>");
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
         }
         else {
             appendMessage("<br><br><font color = #ff0000>* FAIL *</font><br><br>");
             appendMessage("The total time for this "+pauseruntime+" sec test with 30 sec pause actually ran for " + timeOfPauseTest +"<br><br>");
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
 
     //--------------------------------------------//
     //                                            //
     //              Testing Max Speed             //
     //                                            //
     //--------------------------------------------//
     //the testMaxSpeedTime is planned to automate #59 of the software
     //checklist to time the amount of time it takes to go from 0 to max speed
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
 
         System.out.println("NOW RUNNING MAX SPEED TIME TEST<br>");

         appendMessage("<br>--------------------------MAX SPEED TEST--------------------------<br><br>");
         appendMessage(Calendar.getInstance().getTime() + "<br><br>");
 
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
         ((WriteReadDataCmd)wrCmd.getCommand()).addWriteData(BitFieldId.KPH, 16.0);//replace literal by maxSpeed later on
         mSFitSysCntrl.getFitProCntrl().addCmd(wrCmd);
         Thread.sleep(1000);
         appendMessage("Status of speed to 16kph: " + (wrCmd.getCommand()).getStatus().getStsId().getDescription() + "<br>");

         long elapsedTime = 0;
         double seconds = 0;
         long startime = System.nanoTime();
         //Read the actual speed and count elsaped time. Do this until speed has reached MAX
         while(currentActualSpeed < 16)
         {
             currentActualSpeed = hCmd.getActualSpeed();
             elapsedTime = System.nanoTime() - startime;
             seconds = elapsedTime / 1.0E09;
             Thread.sleep(1000);
             appendMessage("actual speed "+currentActualSpeed+" elapsed time " + seconds +" seconds<br>");
         }
 
 
         appendMessage("The max speed is " + maxSpeed + "<br>");
         appendMessage("The motor took " + seconds + " seconds to go to max speed<br>");
 
         if(maxSpeed < 10){
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The Max Speed was not properly read from the brainboard (Max Speed: "+maxSpeed+" kph)<br>");
         }
 
         //%5 pass standard with a 23 sec spec from #59
         if((seconds <= 22) || (seconds >= 24)) {
             appendMessage("<br><font color = #ff0000>* FAIL *</font><br><br>The motor was off by " + (seconds - 23) + " seconds<br>");
         }
 
         else {
             appendMessage("<br><font color = #00ff00>* PASS *</font><br><br>");
             appendMessage("The Max Speed was correctly read off of the brainboard and the speed up to Max Speed took "+
                     seconds+" seconds, which is within the 5% tolerance<br>");
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
     @Override
     public String runAll() {

         try {
            // this.testAge();
             this.testMaxSpeedTime();
            // this.testWeight();
             this.testRunningTime();
             this.testPauseIdleTimeout();
         }
         catch (Exception ex) {
             ex.printStackTrace();
         }
         return res;
     }
 
 
 }
