/**
 * Writes and reads the data from the device.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * First the command will write the data to the device, and the reply will hold
 * all of the items to read.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

public class WriteReadDataCmd extends Command implements CommandInterface, Serializable {

    private static final int MIN_CMD_LENGTH = 6;//2 section bytes

    // The data to be sent down to the device
    private DataBaseCmd mData;

    /**
     * Default constructor for the write data command no device specified
     * @throws Exception
     */
    public WriteReadDataCmd() throws InvalidCommandException, InvalidStatusException
    {
        super();
        this.setCmdId(CommandId.WRITE_READ_DATA);
        this.mData = new DataBaseCmd();
        this.setStatus(new WriteReadDataSts(this.getDevId()));
        this.setLength(MIN_CMD_LENGTH);//length varies
    }

    /**
     * Constructor that includes the DeviceId
     * @param devId the device id of the command
     * @throws Exception
     */
    public WriteReadDataCmd(DeviceId devId) throws InvalidCommandException, InvalidStatusException
    {
        super(new WriteReadDataSts(devId), MIN_CMD_LENGTH, CommandId.WRITE_READ_DATA, devId);
        this.mData = new DataBaseCmd();
    }

    /**
     * Constructor for helping initialize all the data that you want to send
     * @param devId the device id of the command
     * @param writeData the data that you want to write to on the device
     * @throws Exception
     */
    public WriteReadDataCmd(DeviceId devId, Map<BitFieldId, Object> writeData) throws InvalidCommandException, InvalidStatusException, InvalidBitFieldException
    {
        super(new WriteReadDataSts(devId), MIN_CMD_LENGTH, CommandId.WRITE_READ_DATA, devId);
        this.mData = new DataBaseCmd();
        this.addWriteData(writeData);
    }

    /**
     * Constructor for helping initialize all the data that you want to send
     * @param devId the device id of the command
     * @param writeData the data that you want to write to on the device
     * @param readBitIds the data that is to be read from the device
     * @throws Exception
     */
    public WriteReadDataCmd(DeviceId devId,
                            Map<BitFieldId,
                            Object> writeData,
                            Collection<BitFieldId> readBitIds) throws  InvalidCommandException, InvalidStatusException, InvalidBitFieldException
    {
        super(new WriteReadDataSts(devId), MIN_CMD_LENGTH, CommandId.WRITE_READ_DATA, devId);
        this.mData = new DataBaseCmd();
        this.addWriteData(writeData);
        this.addReadBitField(readBitIds);
    }

    /**
     * Adds a Bitfield Id to the command for Writing the data
     * @param id of the BitField to get the info
     * @param data the data to write
     * @exception Exception
     */
    public void addWriteData(BitFieldId id, Object data) throws InvalidBitFieldException, InvalidCommandException
    {
        DataBaseCmd readData;
        int tempLength;
        if(id.getReadOnly())
        {
            throw new InvalidBitFieldException("Invalid BitfieldId "+ id.getDescription()+" This bitfield is read only");
        }

        readData = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        this.mData.addBitfieldData(id, data);
        tempLength = this.mData.getNumOfDataBytes();
        tempLength += MIN_CMD_LENGTH;
        tempLength += this.mData.getMsgDataBytesCount();
        //add read data also to the length
        tempLength += readData.getNumOfDataBytes();
        setLength(tempLength);
        this.checkMsgSize();
    }

    /**
     * Adds a Bitfield Id to the command for Writing the data
     * @param writeData map of all the data of bitfield ids,
     *                  and the object to write(int,double, etc..).
     * @exception Exception
     */
    public void addWriteData(Map<BitFieldId, Object> writeData) throws InvalidBitFieldException, InvalidCommandException
    {
        for(Map.Entry<BitFieldId, Object> entry : writeData.entrySet())
        {
            this.addWriteData(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds a Bitfield Id to the command for getting the data
     * @param id of the BitField to get the info
     */
    public void addReadBitField(BitFieldId id) throws InvalidBitFieldException, InvalidCommandException
    {
        int tempLength;
        DataBaseCmd readData = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        readData.addBitfieldData(id, 0);//always 0 for reading

        tempLength = this.mData.getNumOfDataBytes();
        tempLength += MIN_CMD_LENGTH;
        tempLength += this.mData.getMsgDataBytesCount();
        //add read data also to the length
        tempLength += readData.getNumOfDataBytes();
        setLength(tempLength);
        this.checkMsgSize();
    }

    /**
     * Adds a Bitfield Id to the command for getting the data
     * @param bitFieldList of the BitField to get the info
     */
    public void addReadBitField(Collection<BitFieldId> bitFieldList) throws InvalidBitFieldException, InvalidCommandException
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.addReadBitField(tempId);
        }
    }

    /**
     * Removes a bitfield from the write
     * @param id of the BitField
     */
    public void removeWriteDataField(BitFieldId id) throws InvalidCommandException
    {
        int tempLength;
        DataBaseCmd readData = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        this.mData.removeBitfieldData(id);

        tempLength = this.mData.getNumOfDataBytes();
        tempLength += MIN_CMD_LENGTH;
        tempLength += this.mData.getMsgDataBytesCount();
        //add read data also to the length
        tempLength += readData.getNumOfDataBytes();
        setLength(tempLength);

        this.checkMsgSize();
    }

    /**
     * Removes a bitfield and data from the write
     * @param bitFieldList of the BitField ids
     */
    public void removeWriteDataField(Collection<BitFieldId> bitFieldList) throws InvalidCommandException
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.removeWriteDataField(tempId);
        }
    }

    /**
     * Removes a bitfield from the desired read
     * @param id of the BitField
     */
    public void removeReadDataField(BitFieldId id) throws InvalidCommandException
    {
        int tempLength;
        DataBaseCmd readData = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        readData.removeBitfieldData(id);//always 0 for reading


        tempLength = this.mData.getNumOfDataBytes();
        tempLength += MIN_CMD_LENGTH;
        tempLength += this.mData.getMsgDataBytesCount();
        //add read data also to the length
        tempLength += readData.getNumOfDataBytes();
        setLength(tempLength);

        this.checkMsgSize();
    }

    /**
     * Removes a bitfield from the desired read
     * @param bitFieldList of the BitField to get the info
     */
    public void removeReadDataField(Collection<BitFieldId> bitFieldList) throws InvalidCommandException
    {
        for(BitFieldId tempId : bitFieldList)
        {
            this.removeReadDataField(tempId);
        }
    }

    /**
     * Checks if the bitfield is already set to be read from
     * @param bitId thi bitfield to check
     * @return boolean value of whether it is in the list to read.
     */
    public boolean readContainsBitField(BitFieldId bitId)
    {
        DataBaseCmd data = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        return data.cmdContainsBitfield(bitId);
    }

    /**
     * Checks if the bitfield is already set to be Written to
     * @param bitId thi bitfield to check
     * @return boolean value of whether it is in the list to written to.
     */
    public boolean writeContainsBitField(BitFieldId bitId)
    {
        return this.mData.cmdContainsBitfield(bitId);
    }


    /**
     * clears all of the read bitfields
     */
    public void clearReadBitField()
    {
        DataBaseCmd data = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        data.clearData();
    }

    /**
     * clears all of the write Bitfields
     */
    public void clearWriteBitField()
    {
        this.mData.clearData();
    }


    /**
     * Gets the data that is being written to the System.
     * @return list of bits, and data) that are going to be written.
     */
    public DataBaseCmd getWriteBitData()
    {
        return this.mData;
    }

    /**
     * This will setup the command to be ready to be sent.
     * It adds the number of section bytes, the bits in the sections.
     * @return the Command structured to be ready to sent.
     */
    @Override
    public ByteBuffer getCmdMsg() throws InvalidCommandException, InvalidBitFieldException{

        ByteBuffer buff;
        DataBaseCmd data = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        //load the default items
        buff = super.getCmdMsg();

        //load the Write Data Header and Data
        this.mData.getWriteMsgData(buff);
        //load the Read Data Header
        data.getMsgDataHeader(buff);
        //get the checksum value
        buff.put(Command.getCheckSum(buff));
        return buff;
    }

    /**
     * Gets a cloned copy of the command
     * @return the cloned copy of the command
     * @throws Exception
     */
    @Override
    public Command getCommandCopy() throws InvalidCommandException, InvalidBitFieldException, InvalidStatusException {
        WriteReadDataCmd cmdCopy = new WriteReadDataCmd(this.getDevId());
        cmdCopy.mData = new DataBaseCmd(this.mData);//adds a copy of all the data

        DataBaseCmd data = ((WriteReadDataSts)this.getStatus()).getBitFieldReadData();
        cmdCopy.addReadBitField(data.getMsgData().keySet());
        return cmdCopy;
    }
}
