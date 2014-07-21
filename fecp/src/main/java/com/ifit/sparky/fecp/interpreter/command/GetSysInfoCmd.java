/**
 * This is the command for Get System Info.
 * @author Levi.Balling
 * @date 2/11/14
 * @version 1
 * creates the command for getting the System info from the device.
 * It will only work for the Main device.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class GetSysInfoCmd extends Command implements CommandInterface, Serializable {

    private static final int CMD_LENGTH = 6;
    private boolean mFetchMcuName;
    private boolean mFetchConsoleName;

    /**
     * default constructor
     */
    public GetSysInfoCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.GET_SYSTEM_INFO);
        this.setStatus(new GetSysInfoSts(this.getDevId()));
        this.setLength(CMD_LENGTH);
        this.mFetchMcuName = false;
        this.mFetchConsoleName = false;
    }

    /**
     * default constructor
     */
    public GetSysInfoCmd(DeviceId devId) throws InvalidCommandException, InvalidStatusException
    {
        super(new GetSysInfoSts(devId),CMD_LENGTH,CommandId.GET_SYSTEM_INFO,devId);
    }

    /**
     * Returns whether we are getting the MCU name in the command
     * @return true if we are getting the MCU name, false if not
     */
    public boolean getMcuName() {
        return mFetchMcuName;
    }

    /**
     * Returns whether we are getting the Console name in the command
     * @return true if we are getting the Console name, false if not
     */
    public boolean getConsoleName() {
        return mFetchConsoleName;
    }

    /**
     * sets whether we are getting the MCU Name
     * @param getMcuName True if we are getting the MCU name, false if not
     */
    public void setGetMcuName(boolean getMcuName) {
        this.mFetchMcuName = getMcuName;
    }

    /**
     * sets whether we are getting the Console Name
     * @param getConsoleName True if we are getting the Console name, false if not
     */
    public void setGetConsoleName(boolean getConsoleName) {
        this.mFetchConsoleName = getConsoleName;
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
        if(this.mFetchMcuName)
        {
            buff.put((byte)1);
        }
        else
        {
            buff.put((byte)0);
        }

        if(this.mFetchConsoleName)
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
    public Command getCommandCopy() throws InvalidStatusException, InvalidCommandException {
        Command tempCmd = new GetSysInfoCmd(this.getDevId());
        ((GetSysInfoCmd)tempCmd).setGetConsoleName(this.mFetchConsoleName);
        ((GetSysInfoCmd)tempCmd).setGetMcuName(this.mFetchMcuName);
        return tempCmd;
    }
}
