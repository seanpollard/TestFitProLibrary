/**
 * This is the command for unlocking control of the system.
 * @author Levi.Balling
 * @date 7/1/14
 * @version 1
 * creates the command for unlocking control a device.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.CalibrateSts;
import com.ifit.sparky.fecp.interpreter.status.VerifySecuritySts;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VerifySecurityCmd extends Command implements CommandInterface, Serializable {

    private static final int CMD_LENGTH = 36;
    private ByteBuffer mUnlockKey;// the key to unlock the system.

    /**
     * default constructor
     */
    public VerifySecurityCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.VERIFY_SECURITY);
        this.setStatus(new VerifySecuritySts(this.getDevId()));
        this.setLength(CMD_LENGTH);

        this.mUnlockKey = ByteBuffer.allocate(32);//length of Key
        this.mUnlockKey.order(ByteOrder.LITTLE_ENDIAN);
    }

    public VerifySecurityCmd(DeviceId devId) throws Exception
    {
        super(new VerifySecuritySts(devId), CMD_LENGTH, CommandId.VERIFY_SECURITY, devId);

        this.mUnlockKey = ByteBuffer.allocate(32);//length of Key
        this.mUnlockKey.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Gets the submitted key that was requested
     * @return buffer with key in it.
     */
    public ByteBuffer getUnlockKey() {
        return mUnlockKey;
    }

    /**
     * Sets the Key that you wish to send to unlock the system.
     * @param buffer Buffer you wish to use for the key
     */
    public void setUnlockKey(ByteBuffer buffer)
    {
        this.mUnlockKey.position(0);
        this.mUnlockKey = buffer;
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
        buff.put(this.mUnlockKey);
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
        Command tempCmd = new VerifySecurityCmd(this.getDevId());
        ((VerifySecurityCmd)tempCmd).setUnlockKey(this.mUnlockKey.duplicate());
        return tempCmd;
    }
}
