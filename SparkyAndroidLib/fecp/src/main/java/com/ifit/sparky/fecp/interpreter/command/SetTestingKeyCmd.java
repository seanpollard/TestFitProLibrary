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
import com.ifit.sparky.fecp.interpreter.key.KeyCodes;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;
import com.ifit.sparky.fecp.interpreter.status.SetTestingKeySts;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class SetTestingKeyCmd extends Command implements CommandInterface, Serializable {

    private static final int CMD_LENGTH = 10;
    private boolean mKeyOverride;
    private boolean mIsSingleClick;
    private int mTimeHeld;
    private KeyCodes mKeyCode;

    /**
     * default constructor
     */
    public SetTestingKeyCmd() throws Exception
    {
        super();
        this.setDevId(DeviceId.KEY_PRESS);//only device this can go to.
        this.setCmdId(CommandId.SET_TESTING_KEY);
        this.setStatus(new SetTestingKeySts(this.getDevId()));
        this.setLength(CMD_LENGTH);
        this.mKeyOverride = false;
        this.mIsSingleClick = false;
        this.mTimeHeld = 0;
        this.mKeyCode = KeyCodes.NO_KEY;
    }

    /**
     * default constructor
     */
    public SetTestingKeyCmd(DeviceId devId) throws InvalidStatusException, InvalidCommandException
    {
        super(new SetTestingKeySts(devId),CMD_LENGTH,CommandId.SET_TESTING_KEY,devId);
        this.mKeyOverride = false;
        this.mIsSingleClick = false;
        this.mTimeHeld = 0;
        this.mKeyCode = KeyCodes.NO_KEY;
    }

    public boolean isKeyOverride() {
        return mKeyOverride;
    }

    public boolean isIsSingleClick() {
        return mIsSingleClick;
    }

    public int getTimeHeld() {
        return mTimeHeld;
    }

    public KeyCodes getKeyCode() {
        return mKeyCode;
    }

    public void setKeyOverride(boolean mKeyOverride) {
        this.mKeyOverride = mKeyOverride;
    }

    public void setIsSingleClick(boolean mIsSingleClick) {
        this.mIsSingleClick = mIsSingleClick;
    }

    public void setTimeHeld(int mTimeHeld) {
        this.mTimeHeld = mTimeHeld;
    }

    public void setKeyCode(KeyCodes mKeyCode) {
        this.mKeyCode = mKeyCode;
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
        if(this.mKeyOverride)
        {
            buff.put((byte)1);
        }
        else
        {
            buff.put((byte)0);
        }

        buff.putShort((short)this.mKeyCode.getVal());

        buff.putShort((short)this.mTimeHeld);

        if(this.mIsSingleClick)
        {
            buff.put((byte)1);
        }
        else
        {
            buff.put((byte)0);
        }

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
    public Command getCommandCopy() throws InvalidCommandException, InvalidStatusException {
        Command tempCmd = new SetTestingKeyCmd(this.getDevId());
        ((SetTestingKeyCmd)tempCmd).setIsSingleClick(this.mIsSingleClick);
        ((SetTestingKeyCmd)tempCmd).setTimeHeld(this.mTimeHeld);
        ((SetTestingKeyCmd)tempCmd).setKeyCode(this.mKeyCode);
        ((SetTestingKeyCmd)tempCmd).setKeyOverride(this.mKeyOverride);
        ((SetTestingKeySts)tempCmd.getStatus()).setIsKeySupported(((SetTestingKeySts)this.getStatus()).isIsKeySupported());
        return tempCmd;
    }
}
