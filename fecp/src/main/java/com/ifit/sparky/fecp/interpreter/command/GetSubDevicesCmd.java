/**
 * This is the command for Get Sub Devices
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * creates the command for getting the Devices that belong to the device that was
 * queried.
 * All the devices must support this.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetSubDevicesSts;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class GetSubDevicesCmd extends Command implements CommandInterface, Serializable {

    private static final int MIN_CMD_LENGTH = 4;

    /**
     * default constructor
     */
    public GetSubDevicesCmd() throws InvalidStatusException, InvalidCommandException
    {
        super();
        this.setCmdId(CommandId.GET_SUPPORTED_DEVICES);
        this.setStatus(new GetSubDevicesSts(this.getDevId()));
        this.setLength(MIN_CMD_LENGTH);
    }

    /**
     * constructor for the getSubDevices
     * @param devId the device id for the command
     * @throws Exception
     */
    public GetSubDevicesCmd(DeviceId devId) throws InvalidStatusException, InvalidCommandException
    {
        super(new GetSubDevicesSts(devId), MIN_CMD_LENGTH, CommandId.GET_SUPPORTED_DEVICES, devId);
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
        return new GetSubDevicesCmd(this.getDevId());
    }
}
