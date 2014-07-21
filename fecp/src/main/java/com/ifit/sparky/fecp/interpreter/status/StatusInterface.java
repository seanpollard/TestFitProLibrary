/**
 * Interface for all Status.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * They need to be able to handle a buffer that will interpret the data.
 */
package com.ifit.sparky.fecp.interpreter.status;

import java.nio.ByteBuffer;

public interface StatusInterface {

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     * @param buff the msg that came from the usb. only str
     */
    void handleStsMsg(ByteBuffer buff) throws Exception;
}
