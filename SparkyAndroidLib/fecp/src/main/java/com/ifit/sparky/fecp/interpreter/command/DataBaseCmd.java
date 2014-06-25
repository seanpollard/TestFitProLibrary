/**
 * This is the base file that will handle all of the bitfield data structuring.
 * @author Levi.Balling
 * @date 1/13/14
 * @version 1
 * This will handle a list of data objects and bitfield data with them.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


public class DataBaseCmd implements Serializable {

    /**
     * the data to be sent or to be received
     */
    private TreeMap<BitFieldId, Object> mMsgData;

    private int mNumOfDataBytes;//this is the number of bytes that contain the individual bits

    /**
     * this will be used for a variety of commands
     */
    public DataBaseCmd()
    {
        this.mNumOfDataBytes = 0;
        this.mMsgData = new TreeMap<BitFieldId, Object>(new Comparator<BitFieldId>() {
            @Override
            public int compare(BitFieldId bitFieldId, BitFieldId bitFieldId2) {
                return bitFieldId.compareTo(bitFieldId2);
            }
        });
    }

    /**
     * Copy constructor for the DatabaseCmd
     * @param copyData the data to copy over
     * @throws Exception
     */
    public DataBaseCmd(DataBaseCmd copyData)
    {
        this.mNumOfDataBytes = copyData.mNumOfDataBytes;

        this.mMsgData = new TreeMap<BitFieldId, Object>(new Comparator<BitFieldId>() {
            @Override
            public int compare(BitFieldId bitFieldId, BitFieldId bitFieldId2) {
                return bitFieldId.compareTo(bitFieldId2);
            }
        });

        for(Map.Entry<BitFieldId, Object> entry : copyData.mMsgData.entrySet())
        {
            this.mMsgData.put(entry.getKey(), entry.getValue());//copies each object
        }
    }

    /**
     * Adds a bitfield and the data that you want to send with it.
     * it assumes that the data you want to send is in the correct data format(e.g. double, int, etc)
     * @param id the bitfield that you want to send.
     * @param obj the data that you want to send.
     */
    public void addBitfieldData(BitFieldId id, Object obj)
    {
        if(this.mNumOfDataBytes < id.getSection()+1)
        {
            this.mNumOfDataBytes = id.getSection()+1;
        }
        //obj is the value of the item to be converted
        this.mMsgData.put(id, obj);

    }

    /**
     * Removes the bitfield from the list of bitfields.
     * also changes the number of section bytes accordingly
     * @param id the bitfield that you want to remove.
     */
    public void removeBitfieldData(BitFieldId id)
    {
        if(this.mMsgData.containsKey(id))
        {
            //check if the number of bytes is the same
            this.mMsgData.remove(id);
            this.mNumOfDataBytes = 0;//recalculate
            for(BitFieldId newId : this.mMsgData.keySet())
            {
                if(this.mNumOfDataBytes < newId.getSection()+1)
                {
                    this.mNumOfDataBytes = newId.getSection()+1;
                }
            }
        }
    }

    /**
     * This is the portion of the bytes that need to be sent, from the number of bytes.
     * to the end of the data bits. no data is included in the
     * @return ByteBuffer of the data to be written
     */
    public ByteBuffer getMsgDataHeader()
    {
        //plus one for the number of bytes
        ByteBuffer buffer = ByteBuffer.allocate(this.mNumOfDataBytes +1);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);
        this.getMsgDataHeader(buffer);
        return buffer;
    }

    /**
     * this will populate the buffer with the data, and assumes the position and endian are correct.
     * @param buffer to be populated
     */
    public void getMsgDataHeader(ByteBuffer buffer)
    {
        //plus one for the number of bytes
        buffer.put((byte)this.mNumOfDataBytes);
        //add all the sections we need for the data bits
        for(int i = 0; i < this.mNumOfDataBytes; i++)
        {
            buffer.put(getHeaderDataBitBytes(i));
        }
    }

    /**
     * Gets the buffer from the start of the Number of bytes to the end of the last data object.
     * @return buffer formatted for the Write data portion of the command
     */
    public ByteBuffer getWriteMsgData() throws InvalidBitFieldException{
        int buffSize = 0;

        //add headerSize
        buffSize += this.mNumOfDataBytes +1;
        //add total Data size
        buffSize += this.getMsgDataBytesCount();

        //plus one for the number of bytes
        ByteBuffer buffer = ByteBuffer.allocate(buffSize);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);

        this.getWriteMsgData(buffer);
        return buffer;
    }

    /**
     * Gets the buffer from the start of the Number of bytes to the end of the last data object.
     */
    public void getWriteMsgData(ByteBuffer buffer) throws InvalidBitFieldException {
        ByteBuffer tempBuff;
        //populate the header
        getMsgDataHeader(buffer);
        //add the data from the objects to the buffer
        for(BitFieldId id : this.mMsgData.keySet())
        {
            tempBuff =id.getRawFromData(this.mMsgData.get(id));
            tempBuff.position(0);
            buffer.put(tempBuff);
        }
    }

    /**
     * gets the number of header data bytes that need to be sent.
     * @return the number of data bytes to be sent
     */
    public int getNumOfDataBytes() {
        return mNumOfDataBytes;
    }

    /**
     * Gets the total number data bytes, not the header number of bytes
     * @return  number of data bytes
     */
    public int getMsgDataBytesCount()
    {
        int result = 0;
        //go through all the keys and sum the number of bytes it takes
        for(BitFieldId id : this.mMsgData.keySet())
        {
            result += id.getSize();
        }
        return result;
    }

    /**
     * Gets the total number data bytes, not the header number of bytes
     * @return  number of data bytes
     */
    public boolean cmdContainsBitfield(BitFieldId id)
    {
        return  this.mMsgData.containsKey(id);
    }

    /**
     * Gets the TreeMap of all the data in the list
     * @return Tree map of all the data
     */
    public TreeMap<BitFieldId, Object> getMsgData() {
        return mMsgData;
    }

    /**
     * This will populate all the objects from the byte buffer, assumes position is set correctly
     * @param buffer that holds all the raw data.
     * @return Map(specifically a TreeMap) of all the BitfieldIds and BitfieldDataConverters received.
     */
    public Map<BitFieldId, BitfieldDataConverter> handleReadData(ByteBuffer buffer) throws InvalidBitFieldException
    {
        //todo change comparator to be in a different location.
        TreeMap<BitFieldId, BitfieldDataConverter> map;
        map =  new TreeMap<BitFieldId, BitfieldDataConverter>(new Comparator<BitFieldId>() {
            @Override
            public int compare(BitFieldId bitFieldId, BitFieldId bitFieldId2) {
                return bitFieldId.compareTo(bitFieldId2);
            }
        });

        //get the objects from the byte buffer
        for(Map.Entry<BitFieldId, Object> entry : this.mMsgData.entrySet())
        {

            ByteBuffer tempBuffer = ByteBuffer.allocate(entry.getKey().getSize());
            for(int i = 0; i < entry.getKey().getSize(); i++)
            {
                tempBuffer.put(buffer.get());
            }
            //sets bitfield converter as an object
            entry.setValue(entry.getKey().getData(tempBuffer));//the get data is always data converters
            map.put(entry.getKey(), (BitfieldDataConverter)entry.getValue());
        }
        return map;
    }

    /**
     * Loops through the HashMap of data bits and creates a byte for a specific section
     * @param section the section you need a byte from
     * @return byte of the section
     */
    private byte getHeaderDataBitBytes(int section)
    {
        byte result = 0;
        for(BitFieldId id : this.mMsgData.keySet())
        {
            if(id.getSection() == section)
            {
                result |= (byte)(1 << id.getBit());
            }
        }
        return result;
    }


    private void writeObject(ObjectOutputStream stream) throws IOException
    {
        stream.writeInt(this.mNumOfDataBytes);
        stream.writeInt(this.mMsgData.size());
        for (Map.Entry<BitFieldId, Object> entry : this.mMsgData.entrySet()) {

            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }

    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
    {
        this.mNumOfDataBytes = stream.readInt();
        int size = stream.readInt();
        if(this.mMsgData == null)
        {
            this.mMsgData = new TreeMap<BitFieldId, Object>();
        }

        for(int i = 0; i < size; i++)
        {
            BitFieldId key = (BitFieldId)stream.readObject();
            Object value = stream.readObject();
            this.mMsgData.put(key, value);
        }


    }


}
