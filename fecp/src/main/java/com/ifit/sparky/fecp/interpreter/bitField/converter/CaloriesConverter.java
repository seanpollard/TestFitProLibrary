/**
 * Converts the Raw Calories data into double.
 * @author Levi.Balling
 * @date 5/6/14
 * @version 1
 * the units of calories are 100,000 equals 10.0000 calories.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class CaloriesConverter extends BitfieldDataConverter implements Serializable {

    private double mCalories;

    public CaloriesConverter()
    {
        super();
        this.mDataSize = 4;
        this.mCalories = 0;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException
    {
        this.mCalories = (double)this.getRawToInt();
        this.mCalories /= 10000;
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
        temp *= 10000;//convert to int
        return this.getRawFromData((int)temp);
    }


    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        stream.putDouble(this.mCalories);
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {
        this.mCalories = stream.getDouble();
    }
    public double getCalories()
    {
        return this.mCalories;
    }

}
