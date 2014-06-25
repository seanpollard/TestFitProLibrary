/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 4/2/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.error;

import com.ifit.sparky.fecp.communication.FecpController;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class ErrorCntrl implements ErrorReporting{

    private LinkedList<SystemError> mSystemErrors;
    private FecpController mSystem;//use for reporting error data
    private int mErrorCount;
    private LinkedList<ErrorEventListener> mErrorEventListeners;

    public ErrorCntrl (FecpController system)
    {
        this.mSystem = system;
        this.mSystemErrors = new LinkedList<SystemError>();
        this.mErrorEventListeners = new LinkedList<ErrorEventListener>();
        this.mErrorCount = 0;//todo change to be from a file.

    }

    /**
     * Sends the buffer that matches the online profile for Error messages
     *
     * @param buffer buffer that is pointing to the start of the message.
     */
    @Override
    public void sendErrorObject(ByteBuffer buffer) {
        //handles the message and converts it into a System Error
        //find the type of error msg it is
        ErrorMsgType errType;
        errType = ErrorMsgType.getMsgType(buffer.get());
        SystemError sysError;

        if(errType == ErrorMsgType.BITFIELD_ERR_MSG)
        {
            sysError = new BitfieldError();
        }
        else// default
        {
            sysError = new SystemError();
        }
        sysError.handleErrorBuffer(buffer, this.mErrorCount++);
        this.mSystemErrors.add(sysError);

        //the list only holds the last 10 errors
        //todo figure a solution for more errors
        if(this.mSystemErrors.size() > 10)
        {
            this.mSystemErrors.removeFirst();
        }

        //notify Higher ups
        for (ErrorEventListener listener : this.mErrorEventListeners) {
            listener.onErrorEventListener(sysError);
        }

    }

    @Override
    public String toString() {

        String errorList = "Errors \n";

        for (SystemError error : this.mSystemErrors) {
            errorList += error.toString() +"\n";
        }

        return errorList;
    }

    /**
     * Adds a listener to the system so we can determine if there are any errors
     * @param errListener the listener that will be called when an error occurs
     */
    @Override
    public void addOnErrorEventListener(ErrorEventListener errListener)
    {
        this.mErrorEventListeners.add(errListener);
    }

    /**
     * Removes the listener from the system. so that it won't be called anymore
     * @param errListener the listener that you wish to remove
     */
    @Override
    public void removeOnErrorEventListener(ErrorEventListener errListener)
    {
        if(this.mErrorEventListeners.contains(errListener)) {
            this.mErrorEventListeners.remove(errListener);
        }
    }

    /**
     * Clears the Listers from the system
     */
    @Override
    public void clearOnErrorEventListener()
    {
        this.mErrorEventListeners.clear();
    }

}
