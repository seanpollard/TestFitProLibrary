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

public class BoolConverter extends BitfieldDataConverter implements Serializable {

    private boolean mData;
    public BoolConverter()
    {
        super();
        this.mData = true;
        this.mDataSize = 1;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException {


        long tempVal = this.getRawToInt();
        this.mData = tempVal != 0;
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        boolean temp;
        //object needs to be a double
        if(obj.getClass() == Boolean.class)
        {
            temp = (Boolean)obj;
        }
        else
        {
            throw new InvalidBitFieldException( boolean.class, obj );
        }

        return this.getRawFromData(temp);
    }


    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        if(this.mData)
        {
            stream.put((byte)0x01);
        }
        else
        {
            stream.put((byte)0x00);
        }
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        byte tempVal = stream.get();
        this.mData = tempVal != 0;
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public boolean getValue()
    {
        return this.mData;
    }
}
