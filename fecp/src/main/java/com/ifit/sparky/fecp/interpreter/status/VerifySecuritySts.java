/**
 * This status handles the Validate Security Reply.
 * @author Levi.Balling
 * @date 7/1/14
 * @version 1
 * This will handle the status of the Verify Security command. It will allow the brain board to be written to.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class VerifySecuritySts extends Status implements StatusInterface, Serializable {

    private static final int STS_LENGTH = 5;
    private boolean isUnlocked;

    /**
     * Main constructor for the Calibration Status response
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public VerifySecuritySts(DeviceId devId) throws Exception
    {
        super(StatusId.DEV_NOT_SUPPORTED, STS_LENGTH, CommandId.VERIFY_SECURITY, devId);

        this.isUnlocked = false;
    }

    /**
     * System is unlocked if true, locked if false
     * @return true for unlocked, and false for invalid key or failed.
     */
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception {
        super.handleStsMsg(buff);

        //now parse the data
        if(this.getStsId() == StatusId.DONE)
        {
            this.isUnlocked = true;
        }
        else if(this.getStsId() == StatusId.FAILED)
        {
            this.isUnlocked = false;
        }
        else
        {
            //unknown so it failed
            this.isUnlocked = false;
        }
    }

}
