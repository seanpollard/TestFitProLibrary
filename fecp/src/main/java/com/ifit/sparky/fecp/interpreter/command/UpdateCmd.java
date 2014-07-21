/**
 * This is the command for Updating something
 * @author Levi.Balling
 * @date 4/7/14
 * @version 1
 * creates the command for updating the device.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;
import com.ifit.sparky.fecp.interpreter.status.UpdateSts;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class UpdateCmd extends Command implements CommandInterface, Serializable {

    private static final int CMD_LENGTH = 13;
    private static final int MAX_UPDATE_DATA_SIZE = 50;//raw update data
    private int mDataSize;//the size of the data that is being set
    private int mStartingAddress; //starting address for the data
    // The packet number being data being sent.
    // file size / MAX_UPDATE_DATA_SIZE = Number of packets
    private int mPacketNumber;
    private ByteBuffer mUpdateData;

    /**
     * default constructor
     */
    public UpdateCmd() throws InvalidCommandException, InvalidStatusException
    {
        super();
        this.setCmdId(CommandId.UPDATE);
        this.setStatus(new UpdateSts(this.getDevId()));
        this.setLength(CMD_LENGTH);

        this.mDataSize = 0;
        this.mStartingAddress = 0;
        this.mPacketNumber = 0;
        this.mUpdateData = ByteBuffer.allocate(MAX_UPDATE_DATA_SIZE);
    }

    /**
     * default constructor
     */
    public UpdateCmd(DeviceId devId) throws InvalidCommandException, InvalidStatusException
    {
        super(new UpdateSts(devId),CMD_LENGTH,CommandId.UPDATE,devId);

        this.mDataSize = 0;
        this.mStartingAddress = 0;
        this.mPacketNumber = 0;
        this.mUpdateData = ByteBuffer.allocate(MAX_UPDATE_DATA_SIZE);
    }

    public int getDataSize() {
        return mDataSize;
    }


    public int getStartingAddress() {
        return mStartingAddress;
    }


    public int getPacketNumber() {
        return mPacketNumber;
    }


    public ByteBuffer getUpdateData() {
        return mUpdateData;
    }

    public void setStartingAddress(int mStartingAddress) {
        this.mStartingAddress = mStartingAddress;
    }

    public void setPacketNumber(int mPacketNumber) {
        this.mPacketNumber = mPacketNumber;
    }

    public void setUpdateData(ByteBuffer updateData) throws InvalidCommandException {

        //throw exception if size is to big

        if(updateData.capacity() > this.MAX_UPDATE_DATA_SIZE)
        {
            throw new InvalidCommandException("Update data is too large ("+updateData.capacity() + ") Max is ("+ MAX_UPDATE_DATA_SIZE);
        }
        this.mUpdateData = updateData;
        this.mDataSize = updateData.capacity();
        this.setLength(this.CMD_LENGTH + this.mDataSize);
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

        // packet number
        buff.putInt(this.mPacketNumber);


        // start address
        buff.putInt(this.mStartingAddress);


        // number of bytes
        buff.put((byte)this.mDataSize);


        // data
        buff.put(this.mUpdateData);

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
    public Command getCommandCopy() throws InvalidCommandException, InvalidStatusException {
        Command tempCmd = new UpdateCmd(this.getDevId());

        ((UpdateCmd)tempCmd).setPacketNumber(this.mPacketNumber);
        ((UpdateCmd)tempCmd).setStartingAddress(this.mStartingAddress);
        ((UpdateCmd)tempCmd).setUpdateData(this.mUpdateData);
        return tempCmd;
    }
}
