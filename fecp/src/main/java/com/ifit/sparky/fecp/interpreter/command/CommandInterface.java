/**
 * Interface that all commands need to implement.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * All commands need to be able to get the buffer to send to the device.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.nio.ByteBuffer;

public interface CommandInterface {
    /**
     * Gets the command message for all commands that extend this class.
     * When they want to get the command they have to get the command.
     * @return the Command structured to be ready to send over the usb.
     */
     ByteBuffer getCmdMsg() throws InvalidCommandException, InvalidBitFieldException;
}
