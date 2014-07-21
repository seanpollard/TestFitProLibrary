/**
 * This error is specific to Bitfield error messages.
 * @author Levi.Balling
 * @date 5/15/2014
 * @version 1
 * This has to deal with the way you access the data or write to the data.
 * If you are writing to the data, or reading in a way that shouldn't be done this is the
 * error you will receive.
 */
package com.ifit.sparky.fecp.error;

import android.text.format.DateFormat;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BitfieldError extends SystemError {

    protected BitFieldId mBitId;
    protected boolean mInvalidRead;
    protected boolean mInvalidWrite;
    protected int mDataSize;
    protected ByteBuffer mRawData;

    /**
     * Constructs the Bitfield error
     */
    public BitfieldError()
    {
        super(ErrorMsgType.BITFIELD_ERR_MSG, ErrorCode.INVALID_BITFIELD_ERROR);
        this.mBitId = null;//start off with null
        this.mDataSize = 0;
        this.mInvalidRead = false;
        this.mInvalidWrite = false;
    }

    /**
     * Gets the bitfield that caused the error
     * @return bitfield id of concerned
     */
    public BitFieldId getBitId() {
        return mBitId;
    }

    /**
     * Invalid Read of bitfield
     * @return true is an invalid read
     */
    public boolean isInvalidRead() {
        return mInvalidRead;
    }

    /**
     * Invalid write of the bitfield
     * @return true if invalid write
     */
    public boolean isInvalidWrite() {
        return mInvalidWrite;
    }

    /**
     * Gets the size of the raw data  if it was a write
     * @return size of data
     */
    public int getDataSize() {
        return mDataSize;
    }

    /**
     * Gets a Buffer of the raw data that was being written
     * @return raw Byte Buffer of data
     */
    public ByteBuffer getRawData() {
        return mRawData;
    }

    /**
     * Sets the Bit Id of the error
     * @param bitId error occur with this
     */
    public void setBitId(BitFieldId bitId) {
        this.mBitId = bitId;
    }

    /**
     * Sets the State of the Invalid read
     * @param invalidRead boolean invalid read value
     */
    public void setInvalidRead(boolean invalidRead) {
        this.mInvalidRead = invalidRead;
    }

    /**
     * Sets the Invalid Write
     * @param invalidWrite True if invalid
     */
    public void setInvalidWrite(boolean invalidWrite) {
        this.mInvalidWrite = invalidWrite;
    }

    /**
     * Sets the Size of the raw data
     * @param dataSize size of the raw data
     */
    public void setDataSize(int dataSize) {
        this.mDataSize = dataSize;
    }

    /**
     * Sets the Raw data of the error
     * @param rawData the raw data
     */
    public void setRawData(ByteBuffer rawData) {
        this.mRawData = rawData;
    }

    @Override
    public void handleErrorBuffer(ByteBuffer buffer, int errorNumber) {
        //find the type of error msg it is
        buffer.position(0);

        Calendar currentTime = GregorianCalendar.getInstance();
        this.setErrorTime(currentTime);
        this.setErrorType(ErrorMsgType.getMsgType(buffer.get()));
        this.setErrorNumber(errorNumber);

        this.setErrCode(ErrorCode.getErrorCode(buffer.getShort()));

        //Bitfield id
        try {
            this.mBitId = BitFieldId.getBitFieldId((int)buffer.getShort());
        } catch (InvalidBitFieldException e) {
            e.printStackTrace();
        }
        //Invalid Read
        this.mInvalidRead = buffer.get() != 0;

        //invalid Write
        this.mInvalidWrite = buffer.get() != 0;

        //Size of raw data
        this.mDataSize = (int)buffer.get();

        //Raw data
        this.mRawData = ByteBuffer.allocate(this.mDataSize);
        this.mRawData.order(ByteOrder.LITTLE_ENDIAN);
        this.mRawData.put(buffer.array(), buffer.position(), this.mDataSize);

    }

    @Override
    public String toString() {
        String str = "Error " + this.mErrorNumber +
                ": type=" + mErrorType +
                ", Code=" + mErrCode +
                ", Bitfield ID=" + this.mBitId.getDescription() +
                ", invalid Read=" + this.mInvalidRead +
                ", invalid Write=" + this.mInvalidWrite +
                ", Raw Data=";
        str += Arrays.toString(this.mRawData.array());// formats as an array af numbers
        str += ", Time" + DateFormat.format("MM/dd/yyyy h:mm:ss", this.mErrorTime);
        return str;
    }
}
