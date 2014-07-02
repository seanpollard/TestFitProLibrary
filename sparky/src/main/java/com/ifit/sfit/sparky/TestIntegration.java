package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

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

        int age;

        FecpCommand ageCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd);

        ((WriteReadDataCmd)ageCommand.getCommand()).addReadBitField(BitFieldId.AGE);
       mSFitSysCntrl.getFitProCntrl().addCmd(ageCommand);
        Thread.sleep(1000);

        ageCommand.getCommand().getDevId().getDescription();

        age = hCmd.getAge();
        ageResults += "The default age is set to " + age + " years old\n";

        //Set age to 20 and increment by 5 up to age 45
        for(int i = 20; i <=45; i+=5) {
            ((WriteReadDataCmd) ageCommand.getCommand()).addWriteData(BitFieldId.AGE, i);
            mSFitSysCntrl.getFitProCntrl().addCmd(ageCommand);
            Thread.sleep(2000);

            ageResults += "Status of setting the Age to " + i + ": " + (ageCommand.getCommand()).getStatus().getStsId().getDescription() + "\n";

            age = hCmd.getAge();

            if(age == i){
//            if(age == 13){
                ageResults += "\n* PASS *\n\n";
                ageResults += "Current Age is set to: " + age + " years old (age should really be " + i + ")\n";
            }
            else{
                ageResults += "\n* FAIL *\n\n";
                ageResults += "Current Age is set to: " + age + " years old, but should be set to: " + i + " years old\n";
            }
        }

        return ageResults;
    }

}
