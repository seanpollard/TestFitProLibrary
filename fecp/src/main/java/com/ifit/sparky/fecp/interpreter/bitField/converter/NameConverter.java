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
import java.nio.charset.Charset;

public class NameConverter extends BitfieldDataConverter implements Serializable {

    private String mData;
    public NameConverter()
    {
        super();
        this.mData = "";
        this.mDataSize = 50;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException {
        //convert thisRawData into a string
        this.mData = new String(this.mRawData, Charset.forName("UTF-8"));
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        ByteBuffer buff = ByteBuffer.allocate(this.mDataSize);
        //object needs to be a double
        if(obj.getClass() == String.class)
        {
            buff.put(Charset.forName("UTF-8").encode((String)obj));

        }
        else
        {
            throw new InvalidBitFieldException( double.class, obj );
        }

        return buff;
    }


    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        stream.put((byte)this.mData.length());
        stream.put(Charset.forName("UTF-8").encode((String)this.mData));
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        int size = stream.get();
        byte[] arr = new byte[size];
        stream.put(arr,0,size);
        this.mData = new String(arr, Charset.forName("UTF-8"));
    }

    /**
     * gets the data as a String
     * @return the data as an String
     */
    public String getName()
    {
        return this.mData;
    }
}
