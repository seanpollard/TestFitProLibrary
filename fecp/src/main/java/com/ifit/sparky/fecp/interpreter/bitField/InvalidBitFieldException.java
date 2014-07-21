/**
 * Invalid Bitfield Exception for errors dealing with the Bitfield.
 * @author Levi.Balling
 * @date 12/10/13
 * @version 1
 * If there is a Invalid Bitfield this is the exception to use for it.
 */
package com.ifit.sparky.fecp.interpreter.bitField;

import java.util.ArrayList;

public class InvalidBitFieldException extends Exception {

    /**
     * Handles an exception if there is a bad int used.
     * @param badId bad int used
     */
    public InvalidBitFieldException(int badId)
    {
        super("Invalid Bitfield id ("+badId+").");
    }



    /**
     * Handles an exception if there is a bad section or bit used.
     * @param section the section used
     * @param bit the bit used
     */
    public InvalidBitFieldException(int section, int bit)
    {
        super("Invalid Bitfield id Section("+section+") and Bit("+bit+").");
    }

    /**
     * Handles an exception if the array doesn't match the size
     * @param rawData The array that is suppose to match the size
     * @param size the number of bytes the array is suppose to have
     */
    public InvalidBitFieldException(ArrayList<Byte> rawData, int size)
    {
        super("Invalid Bitfield Size ("+rawData.size()+") needs to match ("+size+").");
    }

    /**
     * Handles an exception if the object types don't match up.
     * @param expectedObject the object type that was expected.
     * @param actualObject the object type that was submitted.
     */
    public InvalidBitFieldException(Object expectedObject ,Object actualObject)
    {
        super("Invalid Object Types ("+actualObject.toString()+") needs to match ("+expectedObject.toString()+").");
    }

    /**
     * Handles an exception if Data is to large to be converted.
     * @param data the data that is too large to fit.
     * @param type the type that it needs to fit.
     */
    public InvalidBitFieldException(int data, Object type)
    {
        super("Data("+data+")is to large needs to be value that fits in unsigned "
                +type.getClass().toString()+").");
    }

    /**
     * Handles an exception with error message passed into it.
     * @param message the error that occurred
     */
    public InvalidBitFieldException(String message)
    {
        super(message);
    }


}
