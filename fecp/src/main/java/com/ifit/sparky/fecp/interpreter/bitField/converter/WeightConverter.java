/**
 * Converts the Raw Weight data into double.
 * @author Levi.Balling
 * @date 5/6/14
 * @version 1
 * the units of weight are 1000 equals 10.00 kilograms.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class WeightConverter extends BitfieldDataConverter implements Serializable {

    private double mWeight;

    public WeightConverter()
    {
        super();
        this.mDataSize = 2;
        this.mWeight = 0;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException
    {
        this.mWeight = (double)this.getRawToInt();
        this.mWeight /= 100;
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {
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
        temp *= 100;//convert to int
        return this.getRawFromData((int)temp);
    }

    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        stream.putDouble(this.mWeight);
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        this.mWeight = stream.getDouble();
    }

    public double getWeight()
    {
        return this.mWeight;
    }

}
