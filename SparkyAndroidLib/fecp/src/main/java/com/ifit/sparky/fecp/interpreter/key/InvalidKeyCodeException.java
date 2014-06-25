/**
 * Invalid Key Code Exception handles any errors with the Key Code.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * Handles all the issues with the keycodes specifically if they don't exist.
 */
package com.ifit.sparky.fecp.interpreter.key;

public class InvalidKeyCodeException extends Exception {

    /**
     * Handles an exception if there is a bad int used.
     * @param badValue bad int used
     */
    public InvalidKeyCodeException(long badValue)
    {
        super("Invalid KeyCode ("+badValue+").");
    }
}