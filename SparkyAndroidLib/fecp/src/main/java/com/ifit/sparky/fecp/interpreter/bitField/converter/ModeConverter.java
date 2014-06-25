/**
 * The mode of the System.
 * @author Levi.Balling
 * @date 2/19/14
 * @version 1
 * Depending on the mode, you can set specific items.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class ModeConverter extends BitfieldDataConverter implements Serializable {
    private ModeId mMode;

    /**
     * Initializes the Mode converter
     */
    public ModeConverter()
    {
        super();
        this.mMode = ModeId.UNKNOWN;
        this.mDataSize = 1;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException {
        int temp = (int)this.getRawToInt();
        this.mMode = ModeId.values()[temp];
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        //object needs to be a double
        if(obj.getClass() == ModeId.class)
        {
            this.mMode = (ModeId)obj;
        }
        else if(obj.getClass() == Integer.class)
        {
            this.mMode = ModeId.values()[(Integer)obj];
        }
        else if(obj.getClass() == Double.class)
        {
            Double temp = (Double)obj;
            this.mMode = ModeId.values()[temp.intValue()];
        }
        else
        {
            throw new InvalidBitFieldException( ModeId.class, obj );
        }

        return this.getRawFromData(this.mMode.getValue());
    }

    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        stream.put((byte)this.mMode.getValue());
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        this.mMode = ModeId.getEnumFromId(stream.get());
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public ModeId getMode()
    {
        return this.mMode;
    }
}
