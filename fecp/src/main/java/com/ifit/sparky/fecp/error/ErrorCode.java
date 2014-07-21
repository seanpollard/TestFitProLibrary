/**
 * Error Codes that match the Sparky Error code list at
 * http://github.ifit-dev.com/Sparky/Process-Training-And-Standards/wiki/Icon-error-codes
 * This is a list of the different types of error codes
 * @author Levi.Balling
 * @date 4/2/2014
 * @version 1
 *
 */
package com.ifit.sparky.fecp.error;

public enum ErrorCode {
    NONE(0),
    INVALID_DEVICE_ERROR(1), //!< INVALID_DEVICE_ERROR
    INVALID_COMMAND_ERROR(2),//!< INVALID_COMMAND_ERROR
    NO_DEVICE_ERROR(3),      //!< NO_DEVICE_ERROR
    NO_PERIPHERAL_ERROR(4),  //!< NO_PERIPHERAL_ERROR
    BAD_CHECKSUM_ERROR(5),   //!< BAD_CHECKSUM_ERROR
    WDT_RESET_ERROR(6),      //!< WDT_RESET_ERROR
    TIME_OUT_ERROR(7),        //!< TIME_OUT_ERROR
    OUT_OF_BOUNDS_ERROR(8),
    INVALID_BITFIELD_ERROR(9),
    INVALID_MODE_REQUEST_ERROR(10),;

    private int mErrorNumber;

    ErrorCode(int errNumber)
    {
        this.mErrorNumber = errNumber;
    }

    public int getErrorNumber()
    {
        return this.mErrorNumber;
    }

    /**
     * Converts a raw valid into a Error Code
     * @param errCodeNum raw error code value
     * @return the error code as enum. NONE if doesn't match
     */
    static public ErrorCode getErrorCode(short errCodeNum)
    {
        //go through all device ids and if it equals then return it.
        for (ErrorCode errCode : ErrorCode.values())
        {
            if(errCodeNum == (short)errCode.getErrorNumber())
            {
                return errCode; // the Device ID
            }
        }

        return ErrorCode.NONE;//resort to default
    }
}
