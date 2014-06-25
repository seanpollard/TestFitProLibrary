/**
 * Creates a command that will hold the raw data to send down to the brain board.
 * @author Levi.Balling
 * @date 5/23/2014
 * @version 1
 * This is the only command is the only command use for passing data straight through.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.status.RawDataSts;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class RawDataCmd extends Command implements Serializable {

    private ByteBuffer mRawSendBuffer;// Comes from remote source to System

    public RawDataCmd(ByteBuffer buffer)
    {
        this.setCmdId(CommandId.RAW);
        this.mRawSendBuffer = buffer;
        try {
            this.setStatus(new RawDataSts());

        } catch (InvalidCommandException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will only setup the first part of the byte buffer that is the same for all
     * buffers. the DeviceId, length, and the Command id
     *
     * @return the Command structured to be ready to send over the usb.
     */
    @Override
    public ByteBuffer getCmdMsg() throws InvalidCommandException, InvalidBitFieldException {
        return this.mRawSendBuffer;
    }

    /**
     * Gets a Cloned Copy of the Command
     *
     * @return a Cloned copy of the Command
     * @throws Exception
     */
    @Override
    public Command getCommandCopy() throws InvalidCommandException {
        Command tempCmd = new RawDataCmd(this.mRawSendBuffer);
        return tempCmd;
    }
}
