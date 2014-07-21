/**
 * This is the command for Calibrating a device
 * @author Levi.Balling
 * @date 4/22/14
 * @version 1
 * creates the command for calibrating a device.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.CalibrateSts;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class CalibrateCmd extends Command implements CommandInterface, Serializable {

    private static final int CMD_LENGTH = 5;
    private int mCalibrationType;//this is specific to the
    //range of 0 to 255. this is for specific devices. if you know what you need you can send down this value.

    /**
     * default constructor
     */
    public CalibrateCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.CALIBRATE);
        this.setStatus(new CalibrateSts(this.getDevId()));
        this.setLength(CMD_LENGTH);

        this.mCalibrationType = 0;
    }

    public CalibrateCmd(DeviceId devId) throws Exception
    {
        super(new CalibrateSts(devId), CMD_LENGTH, CommandId.CALIBRATE, devId);

        this.mCalibrationType = 0;
    }

    /**
     * Gets the Calibration type that was set
     * @return the cal type.
     */
    public int getCalibrationType() {
        return mCalibrationType;
    }

    /**
     * sets the cal type data. this is for specific devices, generally leave value 0.
     * @param mCalibrationType a value from 0 - 255
     */
    public void setCalibrationType(int mCalibrationType) {
        this.mCalibrationType = mCalibrationType;
    }

    /**
     * Gets the command message for all commands that extend this class.
     * When they want to get the command they have to get the command.
     *
     * @return the Command structured to be ready to send over the usb.
     */
    @Override
    public ByteBuffer getCmdMsg() throws InvalidCommandException, InvalidBitFieldException {

        ByteBuffer buff;
        buff = super.getCmdMsg();
        buff.put((byte)this.mCalibrationType);
        //get the checksum value
        buff.put(Command.getCheckSum(buff));
        return buff;
    }

    /**
     * Gets a cloned copy of the command
     * @return the cloned copy of the command
     * @throws Exception if
     */
    @Override
    public Command getCommandCopy() throws Exception {
        Command tempCmd = new CalibrateCmd(this.getDevId());

        ((CalibrateCmd)tempCmd).setCalibrationType(this.mCalibrationType);
        return tempCmd;
    }
}
