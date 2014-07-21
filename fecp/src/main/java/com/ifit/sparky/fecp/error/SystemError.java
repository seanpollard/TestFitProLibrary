/**
 * The System Error is for Hardware Error reporting Not Android.
 * @author Levi.Balling
 * @date 4/2/2014
 * @version 1
 * This will contain the error that we are reporting .
 */
package com.ifit.sparky.fecp.error;

import android.text.format.DateFormat;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SystemError {

    protected ErrorMsgType mErrorType;
    protected ErrorCode mErrCode;
    protected int mLineNumber;
    protected String mFileName;
    protected String mFunctionName;
    protected Calendar mErrorTime;
    protected int mErrorNumber;//index of the Error


    /**
     * Default Constructor for an System Error
     */
    public SystemError()
    {
        initializeSystemError(ErrorMsgType.DEFAULT, ErrorCode.NONE, 0, "", "");
    }

    /**
     * System error with only error codes
     * @param msgType Type of Message
     */
    public SystemError(ErrorMsgType msgType)
    {
        initializeSystemError(msgType, ErrorCode.NONE, 0, "", "");
    }
    /**
     * System error with only error codes
     * @param msgType Type of Message
     * @param code Error Number
     */
    public SystemError(ErrorMsgType msgType, ErrorCode code)
    {
        initializeSystemError(msgType,code, 0, "", "");
    }

    /**
     * System error for reporting the number line and the error code
     * @param msgType Type of Message
     * @param code Error Number
     * @param numberLine the line number the error occurred on.
     */
    public SystemError(ErrorMsgType msgType, ErrorCode code, int numberLine)
    {
        initializeSystemError(msgType, code, numberLine, "", "");
    }

    /**
     * System error for reporting the file, number line, and error code.
     * @param msgType Type of Message
     * @param code Error Number
     * @param numberLine the line number the error occurred on.
     * @param filename the file the error occur in.
     */
    public SystemError(ErrorMsgType msgType, ErrorCode code, int numberLine, String filename)
    {
        initializeSystemError(msgType, code, numberLine, filename, "");
    }

    /**
     * System error for reporting the file, number line, function name, and error code.
     * @param msgType Type of Message
     * @param code Error Number
     * @param numberLine the line number the error occurred on.
     * @param filename the file the error occur in.
     * @param functionName The name of the function
     */
    public SystemError(ErrorMsgType msgType, ErrorCode code, int numberLine, String filename, String functionName)
    {
        initializeSystemError(msgType, code, numberLine, filename, functionName);
    }

    /**
     * Initializer for the System error for reporting the file, number line, function name, and error code.
     * @param msgType Type of Message
     * @param code Error Number
     * @param numberLine the line number the error occurred on.
     * @param filename the file the error occur in.
     * @param functionName The name of the function
     */
    private void initializeSystemError(ErrorMsgType msgType, ErrorCode code, int numberLine, String filename, String functionName)
    {
        this.mErrorType = msgType;
        this.mErrCode = code;
        this.mLineNumber = numberLine;
        this.mFileName = filename;
        this.mFunctionName = functionName;
        this.mErrorNumber = 0;
        this.mErrorTime = new GregorianCalendar();
        this.mErrorTime.setTimeInMillis(0);

    }

    public ErrorMsgType getErrorType() {
        return mErrorType;
    }

    public ErrorCode getErrCode() {
        return mErrCode;
    }

    public int getLineNumber() {
        return mLineNumber;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFunctionName() {
        return mFunctionName;
    }

    public Calendar getErrorTime() {
        return mErrorTime;
    }

    public int getErrorNumber() {
        return mErrorNumber;
    }

    public void setErrorType(ErrorMsgType mErrorType) {
        this.mErrorType = mErrorType;
    }

    public void setErrCode(ErrorCode errorCode) {
        this.mErrCode = errorCode;
    }

    public void setLineNumber(int numberLine) {
        this.mLineNumber = numberLine;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public void setFunctionName(String functionName) {
        this.mFunctionName = functionName;
    }

    public void setErrorTime(Calendar mErrorTime) {
        this.mErrorTime = mErrorTime;
    }

    public void setErrorNumber(int mErrorNumber) {
        this.mErrorNumber = mErrorNumber;
    }

    public void handleErrorBuffer(ByteBuffer buffer, int errorNumber)
    {
        //handles the message and converts it into a System Error
        //find the type of error msg it is
        buffer.position(0);

        Calendar currentTime = GregorianCalendar.getInstance();
        this.setErrorTime(currentTime);
        this.setErrorType(ErrorMsgType.getMsgType(buffer.get()));
        this.setErrorNumber(errorNumber);

        this.setErrCode(ErrorCode.getErrorCode(buffer.getShort()));
        this.setLineNumber(buffer.getShort());
        //get the file for the buffer
        String tempStr = "";

        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            char tempValue = (char)buffer.get();
            if(tempValue == ':')
            {
                break;
            }
            tempStr += tempValue;

        }
        this.setFileName(tempStr);
        tempStr = "";
        for(int i = buffer.position(); i < buffer.capacity(); i++)
        {
            char tempValue = (char)buffer.get();
            tempStr += tempValue;
        }

        this.setFunctionName(tempStr);

    }

    @Override
    public String toString() {

        return "Error " + this.mErrorNumber +
                ": type=" + mErrorType +
                ", Code=" + mErrCode +
                ", Line=" + mLineNumber +
                ", File=" + mFileName +
                ", Function=" + mFunctionName +
                ", Time" + DateFormat.format("MM/dd/yyyy h:mm:ss", this.mErrorTime);
    }
}
