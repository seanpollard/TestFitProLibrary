/**
 * Converts Resistance values into double values with 0.01 precision.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Converts the 2 byte value from a buffer into a Resistance value from  0 to 100.00%.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class ResistanceConverter extends BitfieldDataConverter implements Serializable {

    private double mResistance;

    /**
     * ResistanceConverter Constructor
     */
    public ResistanceConverter()
    {
        super();
        this.mDataSize = 2;
        this.mResistance = 0;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException
    {
        this.mResistance = this.getRawToInt();
        this.mResistance /= 100.0;
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {
        //data coming in as a double
        double temp;

        //object needs to be a double
        if(obj.getClass() == Double.class)
        {
            temp = (Double)obj;
        }
        else if(obj.getClass() == Integer.class)
        {
            temp = (Integer)obj + 0.0;
        }
        else
        {
            throw new InvalidBitFieldException( double.class, obj );
        }

        temp *= 100;

        return this.getRawFromData((int)temp);
    }


    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        stream.putDouble(this.mResistance);

    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        this.mResistance = stream.getDouble();
    }

    /**
     * Gets the resistance
     * @return the resistance as a double 0.00 to 100.00
     */
    public double getResistance()
    {
        return this.mResistance;
    }
}