/**
 * Handles all the command SuperClass items.
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This class will handle the command items, mainly anything dealing with the sending to the system.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.InvalidDeviceException;
import com.ifit.sparky.fecp.interpreter.status.Status;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Command implements CommandInterface, Serializable{

    private final int MAX_MSG_LENGTH = 64;// this may change in the future, but for now this is it.

    private Status mStatus;
    private int mLength;
    private CommandId mCmdId;
    private DeviceId mDevId;

    /**
     * Constructor for the command object. This will handle all the things dealing with
     * sending a command.
     */
    public Command()
    {
        this.mStatus = new Status();
        this.mLength = 0;
        this.mCmdId = CommandId.NONE;
        this.mDevId = DeviceId.NONE;
    }

    /**
     * Constructor for the Command Object.
     * @param length of the message being sent
     * @param cmdId Command id for the message being sent
     * @param devId Device id for the message being sent.
     * @throws Exception if the length is out of bounds
     */
    public Command(int length, CommandId cmdId, DeviceId devId) throws InvalidCommandException
    {
        this.mStatus = new Status();
        if(length <= MAX_MSG_LENGTH && length >= 0)
        {
            this.mLength = length;
        }
        else
        {
            throw new InvalidCommandException("Invalid Length, Max =" + MAX_MSG_LENGTH + " Input Length="+length);
        }

        this.mCmdId = cmdId;
        this.mDevId = devId;
    }

    /**
     * Creates the Command object to get the message for sending the command.
     * @param sts the status, or reply from the equipment.
     * @param length the length of the command to send.
     * @param cmdId the id for the command being sent.
     * @param devId the device id for the command being sent.
     * @throws Exception if the length is to large.
     */
    public Command(Status sts, int length, CommandId cmdId, DeviceId devId) throws InvalidCommandException
    {
        this.mStatus = new Status();
        // if the sts command id doesn't match there is a mistake, there may be a few exceptions.
        if(sts.getCmdId() != cmdId)
        {
            throw new InvalidCommandException(cmdId, sts.getCmdId());
        }

        // The device Id can be different if they are sending
        // the Main device and the reply is different.

        this.mStatus = sts;

        if(length <= MAX_MSG_LENGTH && length >= 0)
        {
            this.mLength = length;
        }
        else
        {
            throw new InvalidCommandException("Invalid Length, Max =" + MAX_MSG_LENGTH + " Input Length="+length);
        }

        this.mCmdId = cmdId;
        this.mDevId = devId;
    }


    /**
     * Get the Command status, or reply. may be none as default
     * @return the Status of the command
     */
    public Status getStatus()
    {
        return this.mStatus; /*Returns the Status of the command */
    }

    /**
     * Gets the length of the message to send.
     * @return the length of the message to send
     */
    public int getLength()
    {
        return this.mLength;/* Returns the length of the message to send.*/
    }

    /**
     * Gets the commandId
     * @return the Command Id for the command
     */
    public CommandId getCmdId()
    {
        return this.mCmdId; /* Returns the Command Id for the command. */
    }

    /**
     * Gets the DeviceId for the command.
     * @return the Device Id for the command
     */
    public DeviceId getDevId()
    {
        return this.mDevId;/* Returns the Device Id for the command */
    }

    /**
     * Sets the Status of the command, this is the reply of the message sent.
     * @param sts the status of the command
     * @exception InvalidCommandException if cmdId's don't match
     */
    public void setStatus(Status sts) throws InvalidCommandException
    {
        // if the sts command id doesn't match there is a mistake, there may be a few exceptions.
        if(sts.getCmdId() != this.mCmdId)
        {
            throw new InvalidCommandException(this.mCmdId, sts.getCmdId());
        }

        this.mStatus = sts;
    }

    /**
     * sets the length of the message being sent.
     * @param length of the message being sent
     * @throws Exception if the length is out of bounds
     */
    public void setLength(int length) throws InvalidCommandException
    {
        if(length <= MAX_MSG_LENGTH && length >= 0)
        {
            this.mLength = length;
        }
        else
        {
            throw new InvalidCommandException("Invalid Length, Max =" + MAX_MSG_LENGTH + " Requested Length="+length);
        }
    }

    /**
     * Sets the Command Id for the message being sent.
     * @param id the commandId
     */
    public void setCmdId(CommandId id)
    {
        this.mCmdId = id;
        this.mStatus.setCmdId(this.mCmdId);
    }

    /**
     * Sets the Command Id,
     * @param idVal int value of the actual byte value.
     * @throws InvalidCommandException if the int value is invalid.
     */
    public void setCmdId(int idVal) throws InvalidCommandException
    {
        this.mCmdId = CommandId.getCommandId(idVal);// if invalid int it will throw exception.
        this.mStatus.setCmdId(this.mCmdId);
    }

    /**
     * sets the Device Id
     * @param id The DeviceId
     */
    public void setDevId(DeviceId id)
    {
        this.mDevId = id;
        this.mStatus.setDevId(id);//set so the match
    }

    /**
     * Sets the Device Id
     * @param idVal int value of the actual byte value
     * @throws InvalidDeviceException if the idVal isn't valid.
     */
    public void setDevId(int idVal) throws InvalidDeviceException
    {
        this.mDevId = DeviceId.getDeviceId(idVal); // if invalid int
    }

    /**
     * Gets a Cloned Copy of the Command
     * @return a Cloned copy of the Command
     * @throws Exception
     */
    public abstract Command getCommandCopy() throws Exception;

    /**
     * gets the checksum byte from the byte buffer. Excludes the last byte in the buffer
     * @param buff the Buffer with the command or status in it.
     * @return the checksum byte
     */
    public static byte getCheckSum(ByteBuffer buff)
    {
        byte checkSum = 0;
        byte msgLength;
        buff.position(1);
        msgLength = buff.get();
        buff.position(0);
        // loop through all but the last byte.
        for(int i = 0; i < msgLength-1; i++)
        {
            checkSum += buff.get();
        }
        return checkSum;
    }

    /**
     * This will only setup the first part of the byte buffer that is the same for all
     * buffers. the DeviceId, length, and the Command id
     *
     * @return the Command structured to be ready to send over the usb.
     */
    @Override
    public ByteBuffer getCmdMsg() throws InvalidCommandException, InvalidBitFieldException {

        ByteBuffer buff;
        buff = ByteBuffer.allocate(this.mLength);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.position(0);
        buff.put((byte)this.mDevId.getVal());
        buff.put((byte)this.mLength);
        buff.put((byte)this.mCmdId.getVal());

        return buff;
    }

    /**
     * Helper function to check if the length of the message is to large
     * @throws Exception if the message is to large
     */
    protected void checkMsgSize() throws InvalidCommandException
    {
        if(this.mLength > this.MAX_MSG_LENGTH)
        {
            throw new InvalidCommandException("Message is to large. Max=("
                    + this.MAX_MSG_LENGTH + ") your message size=(" + this.mLength + ")");
        }
    }

}
