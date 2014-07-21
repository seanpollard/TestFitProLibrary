/**
 * This status handles the Get Sub Devices Command
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * This status must receive the reply from the Get Sub Devices command
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class GetSubDevicesSts extends Status implements StatusInterface, Serializable {

    private static final int MIN_STS_LENGTH = 6;
    private HashSet<DeviceId> mDeviceList;

    /**
     * Main constructor for the Get Sub Devices
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public GetSubDevicesSts(DeviceId devId) throws InvalidStatusException
    {
        super(StatusId.DEV_NOT_SUPPORTED, MIN_STS_LENGTH, CommandId.GET_SUPPORTED_DEVICES, devId);
        this.mDeviceList = new HashSet<DeviceId>();
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception
    {
        super.handleStsMsg(buff);

        //now parse the data
        if(this.getStsId() == StatusId.DONE)
        {
            int numberOfDevices = buff.get();

            //check get the size of the data
            for(int i = 0; i < numberOfDevices; i++)
            {
                this.mDeviceList.add(DeviceId.getDeviceId(buff.get()));
            }
        }
    }

    /**
     * Gets the set of sub device list
     * @return the device ids from the command
     */
    public Set<DeviceId> getSubDevices()
    {
        return this.mDeviceList;
    }
}
