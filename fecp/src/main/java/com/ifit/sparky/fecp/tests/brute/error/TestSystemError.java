/**
 * Tests all the items in the Error Code enum
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This will valitade the the getting a value from the ErrorCode Static function
 */
package com.ifit.sparky.fecp.tests.brute.error;

import com.ifit.sparky.fecp.error.BitfieldError;
import com.ifit.sparky.fecp.error.ErrorCode;
import com.ifit.sparky.fecp.error.ErrorMsgType;
import com.ifit.sparky.fecp.error.SystemError;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TestSystemError extends TestCase{

    /** Tests the Constructors, sets, and Gets
     *
     * @throws Exception
     */
    public void testSystemError_Constructor() throws Exception{

        SystemError error = new SystemError();

        //default constructor
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 0);


        //skipped Msg type since we only have one
        error = new SystemError(ErrorMsgType.DEFAULT);

        //validate Msg
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 0);

        //skipped Msg type since we only have one
        error = new SystemError(ErrorMsgType.DEFAULT, ErrorCode.BAD_CHECKSUM_ERROR);

        //validate ErrorCode
        assertEquals(error.getErrCode(), ErrorCode.BAD_CHECKSUM_ERROR);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 0);


        error = new SystemError(ErrorMsgType.DEFAULT, ErrorCode.NONE, 123);
        //validate Line Number
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 123);

        error = new SystemError(ErrorMsgType.DEFAULT, ErrorCode.NONE, 123, "HELLO");
        //validate File Name
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "HELLO");
        assertEquals(error.getFunctionName(), "");
        assertEquals(error.getLineNumber(), 123);

        error = new SystemError(ErrorMsgType.DEFAULT, ErrorCode.NONE, 123, "HELLO", "WORLD");
        //validate Function
        assertEquals(error.getErrCode(), ErrorCode.NONE);
        assertEquals(error.getErrorNumber(), 0);
        assertEquals(error.getErrorTime().getTimeInMillis(), 0);
        assertEquals(error.getErrorType(), ErrorMsgType.DEFAULT);
        assertEquals(error.getFileName(), "HELLO");
        assertEquals(error.getFunctionName(), "WORLD");
        assertEquals(error.getLineNumber(), 123);

    }


    /** Tests the Bitfield error structure
     *
     * @throws Exception
     */
    public void testSystemError_BitFieldError() throws Exception{

        BitfieldError bitError = new BitfieldError();
        //validate system error items
        assertEquals(bitError.getErrorType(), ErrorMsgType.BITFIELD_ERR_MSG);
        assertEquals(bitError.getErrCode(), ErrorCode.INVALID_BITFIELD_ERROR);
        assertEquals(bitError.getLineNumber(), 0);
        assertEquals(bitError.getFileName(), "");
        assertEquals(bitError.getFunctionName(), "");
        //validate bitfield error items
        assertNull(bitError.getBitId());
        assertEquals(bitError.getDataSize(), 0);
        assertFalse(bitError.isInvalidRead());
        assertFalse(bitError.isInvalidWrite());
        //test the parsing of the raw data and the constructor

        //validate byte buffer parsing of data
        ByteBuffer buffer = ByteBuffer.allocate(11);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(ErrorMsgType.BITFIELD_ERR_MSG.getErrorMsgType());
        buffer.putShort((short)ErrorCode.INVALID_BITFIELD_ERROR.getErrorNumber());
        buffer.putShort((short) BitFieldId.WORKOUT_MODE.getVal());
        buffer.put((byte)0);//valid read
        buffer.put((byte)1);//invalid write
        buffer.put((byte)2);//2 bytes of data
        buffer.putShort((short)12345);//2 bytes of data

        //data filled
        bitError.handleErrorBuffer(buffer, 1);
        //validate interpretation

        assertEquals(bitError.getBitId(), BitFieldId.WORKOUT_MODE);
        assertEquals(bitError.getDataSize(), 2);
        assertFalse(bitError.isInvalidRead());
        assertTrue(bitError.isInvalidWrite());
        ByteBuffer resultBuffer = bitError.getRawData();
        resultBuffer.position(0);
        assertEquals(resultBuffer.getShort(), 12345);
    }

}
