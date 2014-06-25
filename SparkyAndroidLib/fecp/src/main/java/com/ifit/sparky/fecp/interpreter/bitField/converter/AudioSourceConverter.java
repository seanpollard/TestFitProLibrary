/**
 * The audio source of the System.
 * @author Levi.Balling
 * @date 2/19/14
 * @version 1
 * Depending on the system you may have more that one source available.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class AudioSourceConverter extends BitfieldDataConverter implements Serializable{
    private AudioSourceId mAudioSrc;

    /**
     * Initializes the Audio Source converter
     */
    public AudioSourceConverter()
    {
        super();
        this.mAudioSrc = AudioSourceId.NONE;
        this.mDataSize = 1;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException {
        int temp = (int)this.getRawToInt();
        this.mAudioSrc = AudioSourceId.values()[temp];
        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        //object needs to be a double
        if(obj.getClass() == AudioSourceId.class)
        {
            this.mAudioSrc = (AudioSourceId)obj;
        }
        else if(obj.getClass() == Integer.class)
        {
            this.mAudioSrc = AudioSourceId.values()[(Integer)obj];
        }
        else
        {
            throw new InvalidBitFieldException( AudioSourceId.class, obj );
        }

        return this.getRawFromData(this.mAudioSrc.getValue());
    }

    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        stream.put((byte)this.mAudioSrc.getValue());
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        this.mAudioSrc = AudioSourceId.getEnumFromId(stream.get());//just the raw value
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public AudioSourceId getAudioSource()
    {
        return this.mAudioSrc;
    }

}
