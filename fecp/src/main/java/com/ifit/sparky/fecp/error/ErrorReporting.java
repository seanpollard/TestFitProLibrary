/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 4/2/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.error;

import java.nio.ByteBuffer;

public interface ErrorReporting {

    //sends error Function

    /**
     * Sends the buffer that matches the online profile for Error messages
     * @param buffer buffer that is pointing to the start of the message.
     */
    void sendErrorObject(ByteBuffer buffer);

    /**
     * Adds a listener to the system so we can determine if there are any errors
     * @param errListener the listener that will be called when an error occurs
     */
    void addOnErrorEventListener(ErrorEventListener errListener);


    /**
     * Removes the listener from the system. so that it won't be called anymore
     * @param errListener the listener that you wish to remove
     */
    void removeOnErrorEventListener(ErrorEventListener errListener);

    /**
     * Clears the Listers from the system
     */
    void clearOnErrorEventListener();

}
