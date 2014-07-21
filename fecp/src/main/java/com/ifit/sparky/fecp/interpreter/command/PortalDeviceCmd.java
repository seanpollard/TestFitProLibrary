/**
 * This is a specicial command specifically for higher end devices.
 * @author Levi.Balling
 * @date 5/27/2014
 * @version 1
 * This is to simplify the communication to listeners.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;
import com.ifit.sparky.fecp.interpreter.status.PortalDeviceSts;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class PortalDeviceCmd extends Command implements Serializable {


    private static final int CMD_LENGTH = 4;

    public PortalDeviceCmd()throws Exception
    {
        super();
        this.setCmdId(CommandId.PORTAL_DEV_LISTEN);
        this.setStatus(new PortalDeviceSts(this.getDevId()));
        this.setLength(CMD_LENGTH);

        //no data to send down
    }

    public PortalDeviceCmd(DeviceId devId) throws InvalidCommandException, InvalidStatusException
    {
        super(new PortalDeviceSts(devId), CMD_LENGTH, CommandId.PORTAL_DEV_LISTEN, devId);

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
     * Gets a Cloned Copy of the Command
     *
     * @return a Cloned copy of the Command
     * @throws Exception
     */
    @Override
    public Command getCommandCopy() throws Exception {
        Command tempCmd = new PortalDeviceCmd(this.getDevId());
        return tempCmd;
    }

}
