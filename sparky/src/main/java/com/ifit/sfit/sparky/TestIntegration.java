package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.util.Calendar;

/**
 * Created by jc.almonte on 7/2/14.
 */
public class TestIntegration {
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    public TestIntegration(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            hCmd = new HandleCmd(this.mAct);// Init handlers
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //--------------------------------------------//
    //
    //Testing Age
    //
    //--------------------------------------------//
    /*
    TODO: Future test can include input invalid ages. Valid age range is 5-95 years, validate default age
    TODO: Also check that test still work when changing units from Metric to English\
    TODO: Is minimum age 18?
    */
    public String testAge() throws Exception {
        //Redmine Support #937
        //Read the default Age
        //Set the Age
        //Read the Age
        //Validate the Age
        //Repeat 6 times with values in increments of 5 years
        String ageResults;

        ageResults = "\n\n------------------------AGE TEST RESULTS------------------------\n\n";
        ageResults += Calendar.getInstance().getTime() + "\n\n";

        double age;
        double prevAge;

        FecpCommand ageCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);

        ((WriteReadDataCmd)ageCommand.getCommand()).addReadBitField(BitFieldId.AGE);
       mSFitSysCntrl.getFitProCntrl().addCmd(ageCommand);
        Thread.sleep(25);

        ageCommand.getCommand().getDevId().getDescription();

        age = hCmd.getAge();
        ageResults += "The default age is set to " + age + " years old\n";

        //Set age to min=18 and increment by 1 up to maxage= 95

        int failureCounter = 0;
        long elapsedTime = 0;
        int i;
        long startTime;
        for(i = 18; i <=95; i+=1) {
            ((WriteReadDataCmd) ageCommand.getCommand()).addWriteData(BitFieldId.AGE, i);
            mSFitSysCntrl.getFitProCntrl().addCmd(ageCommand);
            age = hCmd.getAge();
            startTime = System.currentTimeMillis();
            //Keep reading the value until is the on you set it too or until it has try for long enough (25ms) that
            // we can conclude the reading has failed
            while(age!=i && elapsedTime < 25){
                age = hCmd.getAge();
                elapsedTime = System.currentTimeMillis() - startTime;
                }
            System.out.println(elapsedTime);

            ageResults += "Status of setting the Age to " + i + ": " + (ageCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

           /*
            if(age == 80)
            {
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println(elapsedTime);
            }
    */
            if(age == i){
//            if(age == 13){
                ageResults += "\n* PASS *\n\n";
                ageResults += "Current Age is set to: " + age + " years old (age should really be " + i + ")\n";
                failureCounter++;
            }
            else{
                ageResults += "\n* FAIL *\n\n";
                ageResults += "Current Age is set to: " + age + " years old, but should be set to: " + i + " years old\n";
            }
            mSFitSysCntrl.getFitProCntrl().removeCmd(ageCommand);
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
    TODO: Also check for conversions when changing untis from Metric to English
    TODO: Put tolerance range to avoid rounding issues --- DONE!
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

        weightResults = "\n\n------------------------WEIGHT TEST RESULTS------------------------\n\n";
        weightResults += Calendar.getInstance().getTime() + "\n\n";

        double weight;

        FecpCommand weightCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);
        ((WriteReadDataCmd)weightCommand.getCommand()).addReadBitField(BitFieldId.WEIGHT);
        mSFitSysCntrl.getFitProCntrl().addCmd(weightCommand);
        Thread.sleep(1000);

        weight = hCmd.getWeight();
        weightResults += "The default weight is set to " + weight + " kilograms\n";
        double diff;

        //Set weight to 50 kg and increment by 10 up to 175 kg (max is 400lbs = 181 kg)
        for(double i = 45.35; i <=175; i+=10) {

            ((WriteReadDataCmd) weightCommand.getCommand()).addWriteData(BitFieldId.WEIGHT, i);
            mSFitSysCntrl.getFitProCntrl().addCmd(weightCommand);
            //need more time for weight controller
            Thread.sleep(1000);

            weightResults += "\nStatus of setting the Weight to " + i + ": " + (weightCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

            weight = hCmd.getWeight();
            diff = Math.abs(weight - i);

          if(diff < i*.01) // if values are within 1% of each other
          {
                weightResults += "\n* PASS *\n\n";
                weightResults += "Current Weight is set to: " + weight + " kilograms should be set to: " + i + " kilograms\n";
                weightResults+="set and read values have a difference of "+diff+" which is within 1% tolerance\n";
            }
            else{
                weightResults += "\n* FAIL *\n\n";
                weightResults += "Current Weight is set to: " + weight + " kilograms, but should be set to: " + i + " kilograms\n";
                weightResults+="set and read values have a difference of "+diff+" which is outside the 1% tolerance\n";

          }
        }
        mSFitSysCntrl.getFitProCntrl().removeCmd(weightCommand);
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

        titleString = "\n----------------------SYSTEM CONFIGURATION TEST----------------------\n\n";
        titleString += Calendar.getInstance().getTime() + "\n\n";

        double maxIncline;
        double minIncline;
        double maxSpeed;
        double minSpeed;

        FecpCommand readMaxIncline = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), hCmd);//every 1 second
        ((WriteReadDataCmd)readMaxIncline.getCommand()).addReadBitField(BitFieldId.MAX_GRADE);
        mSFitSysCntrl.getFitProCntrl().addCmd(readMaxIncline);
        Thread.sleep(1000);

        //Check status of the command to receive the incline
        titleString += "Status of reading max incline: " + (readMaxIncline.getCommand()).getStatus().getStsId().getDescription() + "\n";

        maxIncline = hCmd.getMaxIncline();

        FecpCommand readMinIncline = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);//every 1 second
        ((WriteReadDataCmd)readMinIncline.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
        mSFitSysCntrl.getFitProCntrl().addCmd(readMinIncline);
        Thread.sleep(1000);

        //Check status of the command to receive the incline
        titleString += "Status of reading min incline: " + (readMaxIncline.getCommand()).getStatus().getStsId().getDescription() + "\n";

        minIncline = hCmd.getMinIncline();

        FecpCommand readMaxSpeed = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);//every 1 second
        ((WriteReadDataCmd)readMaxSpeed.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
        mSFitSysCntrl.getFitProCntrl().addCmd(readMaxSpeed);
        Thread.sleep(1000);

        //Check status of the command to receive the incline
        titleString += "Status of reading max speed: " + (readMaxSpeed.getCommand()).getStatus().getStsId().getDescription() + "\n";

        maxSpeed = hCmd.getMaxSpeed();
        minSpeed = hCmd.getMinSpeed();
        Thread.sleep(1000);

        //Need a new Device object for some of the device info
        Device device = new Device();
        String brainboardLines[] = new String[11];
        //Split the user's input string into separate strings to be compared, line by line ("\n" is the delimiter)
        String inputLines[] = inputString.split("\r?\n|\r");

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
                systemString += "\nBrainboard " + brainboardLines[i] + "\nKeyboard Input " + inputLines[i] + "\n\n* PASS *\n\n";
            }
            else
            {
                systemString += "\nBrainboard " + brainboardLines[i] + "\n Keyboard Input " + inputLines[i] + "\n\n* FAIL *\n\n";
            }
        }

        systemString = titleString + systemString;

        return systemString;
    }

}
