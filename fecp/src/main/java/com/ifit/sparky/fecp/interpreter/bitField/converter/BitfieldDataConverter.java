/**
 * The Data converter for data.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * This will provide a set of static require functions to convert raw byte data into the correct
 * format and type.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class BitfieldDataConverter implements Serializable {

    protected byte[] mRawData;
    protected int mDataSize;
    protected long mTimeRecieved;
    protected boolean mIsDirty;

    /**
     * Default Constructor.
     */
    public BitfieldDataConverter()
    {
        //this.mRawData = ByteBuffer.allocate(8);//max cap needed
        //this.mRawData.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Sets the rawData for the Converter
     * @param rawData the raw data to convert
     * @param size the number of data bytes
     * @throws InvalidBitFieldException if the sizes don't match up
     */
    public void setRawData(ByteBuffer rawData, int size) throws InvalidBitFieldException
    {
        this.mRawData = rawData.array();
        if(this.mRawData.length != size)
        {
            throw new InvalidBitFieldException(rawData, size);
        }

        this.mDataSize = size;
    }

    /**
     * Gets the Int value from the array of bytes
     * @return int value from the byte array
     * @throws InvalidBitFieldException if the number of bytes and the required size don't match up
     */
    protected long getRawToInt() throws InvalidBitFieldException
    {
        //depending on the size convert to int

        long rawLong;//to get all the values of the int correctly

        ByteBuffer buffer = ByteBuffer.allocate(this.mRawData.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(this.mRawData);
        buffer.position(0);

        if(this.mDataSize == 1)
        {
            //value needs to be unsigned
            rawLong = (buffer.get(0) & 0xFF);//require for unsigned values
        }
        else if(this.mDataSize == 2)
        {
            rawLong = (buffer.getShort(0) & 0xFFFF);
        }
        else if(this.mDataSize == 4)
        {
            rawLong = buffer.getInt(0);// & 0xFFFFFFFF;
        }
        else
        {
            throw new InvalidBitFieldException("DataSizeCurrentlyNotSupported");
        }
        return rawLong;
    }


    /**
     * Converts the data into the byte array
     * @param data the byte, short, or int to be converted
     * @return array of bytes
     */
    protected ByteBuffer getRawFromData(boolean data) throws InvalidBitFieldException
    {
        ByteBuffer tempBuff = ByteBuffer.allocate(this.mDataSize);//don't overwrite
        tempBuff.order(ByteOrder.LITTLE_ENDIAN);
        tempBuff.position(0);
        if(this.mDataSize == 1)
        {
            if(data)
            {
                tempBuff.put((byte)0x01);
            }
            else
            {
                tempBuff.put((byte)0x00);
            }
        }
        return tempBuff;
    }

    /**
     * Converts the data into the byte array
     * @param data the byte, short, or int to be converted
     * @return array of bytes
     */
    protected ByteBuffer getRawFromData(int data) throws InvalidBitFieldException
    {
        //depending on the size convert to int
        ByteBuffer tempBuff = ByteBuffer.allocate(this.mDataSize);//don't overwrite
        tempBuff.order(ByteOrder.LITTLE_ENDIAN);
        tempBuff.position(0);
        if(this.mDataSize == 1)
        {
            if(data > Byte.MAX_VALUE)
            {
                Byte b = 0;
                throw new InvalidBitFieldException(data, b);
            }
            tempBuff.put((byte) data);
        }
        else if(this.mDataSize == 2)
        {
            if(data > Short.MAX_VALUE)
            {
                Short s = 0;
                throw new InvalidBitFieldException(data, s);
            }
            tempBuff.putShort((short)data);
        }
        else if(this.mDataSize == 4)
        {
            //input always in, no need for checking
            tempBuff.putInt(data);
        }

        return tempBuff;
    }


    /**
     * Gets whether the data is dirty or not.
     * @return true for dirty and changed, false for same value
     */
    public boolean isIsDirty() {
        return mIsDirty;
    }

    /**
     * Sets the value to be dirty
     * @param isDirty true for dirty, false for unchanged
     */
    public void setIsDirty(boolean isDirty) {
        this.mIsDirty = isDirty;
    }

    public abstract BitfieldDataConverter getData()throws InvalidBitFieldException;

    public abstract ByteBuffer convertData(Object obj)throws InvalidBitFieldException;

    /**
     * This is to keep track of when the data was last received
     * @param time time sample in the form of a long
     */
    public void setTimeReceived(long time)
    {
        this.mTimeRecieved = time;
    }

    public abstract void writeObject(ByteBuffer stream) throws IOException;

    public abstract void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException;


}
