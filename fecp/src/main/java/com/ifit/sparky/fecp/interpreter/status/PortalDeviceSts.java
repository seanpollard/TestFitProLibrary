/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 5/27/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class PortalDeviceSts extends Status implements Serializable {


    private static final int STS_LENGTH = 6;
    private SystemDevice mSysDev;
    public PortalDeviceSts(DeviceId devId) throws InvalidStatusException
    {
        super(StatusId.DEV_NOT_SUPPORTED, 0, CommandId.PORTAL_DEV_LISTEN, devId);//0 length due to unknown

    }

    public SystemDevice getmSysDev() {
        return mSysDev;
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception {
        //raw data that is the system device no extra data besides the Raw data
        buff.position(0);

        if(this.mSysDev == null)
        {
            this.mSysDev = new SystemDevice();
        }

        this.mSysDev.readObject(buff);
        this.setStsId(StatusId.DONE);
    }
}
