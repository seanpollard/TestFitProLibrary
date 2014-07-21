/**
 * This status handles the reply for the Set test key command
 * @author Levi.Balling
 * @date 2/11/14
 * @version 1
 * this will handle the reply, and let you now if this particular key is supported.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class SetTestingKeySts extends Status implements StatusInterface, Serializable {

    private static final int MIN_STS_LENGTH = 6;

    private boolean mIsKeySupported;

    /**
     * Main constructor for the Info Status response
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public SetTestingKeySts(DeviceId devId) throws InvalidStatusException
    {
        super(StatusId.DEV_NOT_SUPPORTED, MIN_STS_LENGTH, CommandId.SET_TESTING_KEY, devId);
        this.mIsKeySupported = false;
    }

    public boolean isIsKeySupported() {
        return mIsKeySupported;
    }

    public void setIsKeySupported(boolean mIsKeySupported) {
        this.mIsKeySupported = mIsKeySupported;
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception {
        super.handleStsMsg(buff);

        //now parse the data
        if(this.getStsId() == StatusId.DONE)
        {
            if(buff.get() == 1)
            {
                this.mIsKeySupported = true;
            }
            else
            {
                this.mIsKeySupported = false;
            }
        }
    }

}
