/**
 * This status handles the device's info, and everything about the device.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * this status must receive the Device Info.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.DeviceInfo;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class InfoSts extends Status implements StatusInterface, Serializable {

    private DeviceInfo mInfo;
    private static final int STS_LENGTH = 14;

    /**
     * Main constructor for the Info Status response
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public InfoSts(DeviceId devId) throws InvalidStatusException
    {
        //Min length is 14 bytes
        super(StatusId.DEV_NOT_SUPPORTED, STS_LENGTH, CommandId.GET_INFO, devId);
        this.mInfo = new DeviceInfo();
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
            this.mInfo.interpretInfo(buff);
            this.mInfo.setDevId(this.getDevId());//it is given that device id is known from the send
        }
    }

    /**
     * Gets the device info
     * @return the device info
     */
    public DeviceInfo getInfo()
    {
        return this.mInfo;
    }
}
