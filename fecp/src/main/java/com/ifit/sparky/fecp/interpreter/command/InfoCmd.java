/**
 * This is the command for Get Device Info.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * creates the command for getting the device info from the device.
 * All the devices must support this.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class InfoCmd extends Command implements CommandInterface, Serializable {

    private static final int CMD_LENGTH = 4;
    /**
     * default constructor
     */
    public InfoCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.GET_INFO);
        this.setStatus(new InfoSts(this.getDevId()));
        this.setLength(CMD_LENGTH);
    }

    /**
     * default constructor
     */
    public InfoCmd(DeviceId devId) throws InvalidStatusException, InvalidCommandException
    {
        super(new InfoSts(devId),CMD_LENGTH,CommandId.GET_INFO,devId);
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
        return new InfoCmd(this.getDevId());
    }
}
