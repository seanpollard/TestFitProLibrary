/**
 * Converts the data into its values.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * Converts the value into its exact value.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class ByteConverter extends BitfieldDataConverter implements Serializable {

    private int mData;
    public ByteConverter()
    {
        super();
        this.mData = 0;
        this.mDataSize = 1;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException {
        this.mData = (int)this.getRawToInt();
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

        return this.getRawFromData((int)temp);
    }


    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        stream.put((byte)this.mData);
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        this.mData = stream.get();
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public int getValue()
    {
        return this.mData;
    }
}
