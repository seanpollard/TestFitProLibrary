/**
 * This status handles the Get System info, Things specific to the whole System.
 * @author Levi.Balling
 * @date 2/11/14
 * @version 1
 * The things that you can get with this command are the System unique part numbers, Console id.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.SystemDeviceInfo;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class GetSysInfoSts extends Status implements StatusInterface, Serializable {

    private static final int STS_LENGTH = 24;

    private SystemDeviceInfo mSysDevInfo;

    /**
     * Main constructor for the Info Status response
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public GetSysInfoSts(DeviceId devId) throws InvalidStatusException
    {
        super(StatusId.DEV_NOT_SUPPORTED, STS_LENGTH, CommandId.GET_SYSTEM_INFO, devId);
    }

    /**
     * gets the information about the System
     * @return System Device Info
     */
    public SystemDeviceInfo getSysDevInfo()
    {
        return this.mSysDevInfo;
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

        if(this.getStsId() == StatusId.DONE)
        {
            mSysDevInfo = new SystemDeviceInfo(buff);

        }
    }

}
