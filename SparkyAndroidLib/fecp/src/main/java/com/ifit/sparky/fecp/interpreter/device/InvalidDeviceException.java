/**
 * Invalid Device Exception is to allow for better resources for what is wrong.
 * @author Levi.Balling
 * @date 12/10/13
 * @version 1
 * This will provide what is wrong, and allow for the user to quickly evaluate the issue at hand.
 */
package com.ifit.sparky.fecp.interpreter.device;

public class InvalidDeviceException extends Exception {

    /**
     * Handles an exception if there is a bad int used.
     * @param badId bad int used
     */
    public InvalidDeviceException(int badId)
    {
        super("Invalid Device id ("+badId+").");
    }

    /**
     * Handles an exception if there the device Id doesn't match the received device id byte
     * @param actualDev the received byte from the buffer
     * @param id the device id it should have been.
     */
    public InvalidDeviceException(byte actualDev, DeviceId id)
    {
        super("Invalid Device ID, Expected("+id.getDescription()+
                ": "+id.getVal() +") Actual("+actualDev+")");

    }
}
