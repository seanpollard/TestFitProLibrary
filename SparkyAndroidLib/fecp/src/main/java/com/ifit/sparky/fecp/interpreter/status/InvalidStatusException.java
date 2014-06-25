/**
 * Handles the Invalid Status Errors.
 * @author Levi.Balling
 * @date 12/10/13
 * @version 1
 * Handles the errors dealing with the Status.
 */
package com.ifit.sparky.fecp.interpreter.status;

public class InvalidStatusException extends Exception {

    /**
     * Invalid StatusId int was used.
     * @param badId bad int for the StatusId
     */
    public InvalidStatusException(int badId)
    {
        super("Invalid Status id ("+badId+").");
    }

    /**
     * Invalid StatusId int was used.
     * @param message invalid Status explanation.
     */
    public InvalidStatusException(String message)
    {
        super(message);
    }

    /**
     * If the checksums don't match throw an error.
     * @param expectedCheckSum The expected byte value
     * @param actualCheckSum the invalid byte value received
     */
    public InvalidStatusException(byte expectedCheckSum, byte actualCheckSum)
    {
        super("Invalid Status Checksum Expected or No Checksum("+ expectedCheckSum +") Actual("+actualCheckSum+")");

    }

    /**
     * If the checksums don't match throw an error.
     * @param expectedCheckSum The expected byte value
     * @param actualCheckSum the invalid byte value received
     */
    public InvalidStatusException(Status sts, byte expectedCheckSum, byte actualCheckSum)
    {
        super("Invalid Response from device("+ sts.toString() +") expected checksum("+ expectedCheckSum +") Actual("+actualCheckSum+")");

    }

}