/**
 * Handle the response of a raw data command.
 * @author Levi.Balling
 * @date 5/23/2014
 * @version 1
 * This will pass the raw data directly back to the source. Some work will need to be added to
 * only support specific commands based on the requests.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class RawDataSts extends Status implements Serializable {

    private ByteBuffer mRawReceiveBuffer;//comes from the System to Remote Source

    public RawDataSts()
    {
        super();
        this.setCmdId(CommandId.RAW);
    }

    /**
     * Returns the Raw buffer from the reply
     * @return Byte buffer with the reply
     */
    public ByteBuffer getRawBuffer()
    {
        return mRawReceiveBuffer;
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception {
        this.mRawReceiveBuffer = buff;
        //set status to be done
        this.setStsId(StatusId.DONE);//command received

    }
}
