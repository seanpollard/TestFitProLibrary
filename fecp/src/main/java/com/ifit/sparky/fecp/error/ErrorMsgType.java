/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 4/3/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.error;

public enum ErrorMsgType {
    DEFAULT(0),
    BITFIELD_ERR_MSG(2);

    private byte mErrorMsgType;

    ErrorMsgType(int msgType){
        mErrorMsgType = (byte)msgType;
    }

    public byte getErrorMsgType()
    {
        return this.mErrorMsgType;
    }

    static public ErrorMsgType getMsgType(byte msgType)
    {
        //go through all device ids and if it equals then return it.
        for (ErrorMsgType errMsgType : ErrorMsgType.values())
        {
            if(msgType == errMsgType.getErrorMsgType())
            {
                return errMsgType; // the Device ID
            }
        }

        return ErrorMsgType.DEFAULT;//resort to default
    }
}
