package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.FitProUsb;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.SystemStatusListener;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

import android.content.Context;
import android.content.Intent;


/**
 * Created by jc.almonte on 6/25/14.
 */
public class TestRunMotor {
    private FecpController mFecpController;
    private TestApp mAct;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    public TestRunMotor(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
        this.mFecpController = fecpController;
        this.mAct = act;
        this.mSFitSysCntrl = ctrl;
        MainDevice = this.mFecpController.getSysDev();
        }
    public void runMotor()
    {
        try {
            FecpCommand modeCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));

            ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);
             mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
            Thread.sleep(5000);
            ((WriteReadDataCmd) modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);
            mSFitSysCntrl.getFitProCntrl().addCmd(modeCommand);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



}
