/**
 * This status handles the Calibration Reply.
 * @author Levi.Balling
 * @date 4/22/14
 * @version 1
 * This will handle the reply from a Calibration command. if the command replies back failed,
 * then the calibration failed. If it replies back Done, it passed.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class CalibrateSts extends Status implements StatusInterface, Serializable {

    private static final int STS_LENGTH = 6;
    private int mCalResponseData;//whatever may be useful from a reply from calibration 0 - 255

    /**
     * Main constructor for the Calibration Status response
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public CalibrateSts(DeviceId devId) throws Exception
    {
        super(StatusId.DEV_NOT_SUPPORTED, STS_LENGTH, CommandId.CALIBRATE, devId);

        this.mCalResponseData = 0;

    }

    /**
     * gets the data associated with the specific device. generally 0
     * @return data specific to the device from 0 - 255
     */
    public int getCalResponseData() {
        return mCalResponseData;
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
            //packet number
            this.mCalResponseData =buff.get();
            //this means it passed
        }
        //result may be failed
    }

}
