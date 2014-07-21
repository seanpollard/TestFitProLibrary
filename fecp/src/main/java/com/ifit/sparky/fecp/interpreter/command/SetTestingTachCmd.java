/**
 * This is the command for Testing. It is important that the code on this will only be used for testing.
 * @author Levi.Balling
 * @date 2/11/14
 * @version 1
 * This will allow testing to send a command to set a key down to the brain board.
 * This will disable all the regular key presses while it is enabled.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;
import com.ifit.sparky.fecp.interpreter.status.SetTestingKeySts;
import com.ifit.sparky.fecp.interpreter.status.SetTestingTachSts;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class SetTestingTachCmd extends Command implements CommandInterface, Serializable {

    private static final int CMD_LENGTH = 11;
    private boolean mTachOverride;
    private short mTachCount;
    private short mTachTime;
    private double mActualSpeed;

    /**
     * default constructor
     */
    public SetTestingTachCmd() throws InvalidCommandException, InvalidStatusException
    {
        super();
        this.setCmdId(CommandId.SET_TESTING_TACH);
        this.setStatus(new SetTestingKeySts(this.getDevId()));
        this.setLength(CMD_LENGTH);
        this.mTachCount = 0;
        this.mTachOverride = false;
        this.mTachTime = 0;
        this.mActualSpeed = 0.0;
    }

    /**
     * Creates a Testing Tach Command for writing the system's tach.
     * @param devId The device id which the command is being sent to.
     * @throws Exception invalid commands, and device ids
     */
    public SetTestingTachCmd(DeviceId devId) throws InvalidCommandException, InvalidStatusException
    {
        super(new SetTestingTachSts(devId),CMD_LENGTH,CommandId.SET_TESTING_TACH,devId);
        this.mTachCount = 0;
        this.mTachOverride = false;
        this.mTachTime = 0;
        this.mActualSpeed = 0.0;
    }

    public short getTachTime() {
        return mTachTime;
    }

    public void setTachTime(short mTachTime) {
        this.mTachTime = mTachTime;
    }

    public boolean isTachOverride() {
        return mTachOverride;
    }

    public void setTachOverride(boolean mTachOverride) {
        this.mTachOverride = mTachOverride;
    }

    public short getTachCount() {
        return mTachCount;
    }

    public void setTachCount(short mTachCount) {
        this.mTachCount = mTachCount;
    }

    public double getActualSpeed() {
        return mActualSpeed;
    }

    public void setActualSpeed(double mActualSpeed) {
        this.mActualSpeed = mActualSpeed;
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
        short tempActualSpeed;
        double tempRawSpeed = this.mActualSpeed;

        buff = super.getCmdMsg();
        //testing mode
        if(this.mTachOverride)
        {
            buff.put((byte)1);
        }
        else
        {
            buff.put((byte)0);
        }

        //tach count
        buff.putShort(this.mTachCount);


        //tach time
        buff.putShort(this.mTachTime);

        //actual speed highest priority
        //convert double to be a int
        tempRawSpeed *= 100;//converts the 0.01 to be 1
        //convert to short
        tempActualSpeed = (short)Math.round(tempRawSpeed);

        buff.putShort(tempActualSpeed);

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
        Command tempCmd = new SetTestingTachCmd(this.getDevId());
        ((SetTestingTachCmd)tempCmd).setActualSpeed(this.mActualSpeed);
        ((SetTestingTachCmd)tempCmd).setTachOverride(this.mTachOverride);
        ((SetTestingTachCmd)tempCmd).setTachTime(this.mTachTime);
        ((SetTestingTachCmd)tempCmd).setTachCount(this.mTachCount);
        return tempCmd;
    }
}
