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
import java.nio.ByteOrder;
import java.util.ArrayList;

public class AudioSourceConverter extends BitfieldDataConverter implements Serializable{

    private AudioSourceId mAudioSrc;
    private ArrayList<AudioSourceId> supportedAudioSrcs;

    /**
     * Initializes the Audio Source converter
     */
    public AudioSourceConverter()
    {
        super();
        this.mAudioSrc = AudioSourceId.NONE;
        this.supportedAudioSrcs = new ArrayList<AudioSourceId>();
        this.mDataSize = 3;
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException {


//        int temp = (int)this.getRawToInt();
        int temp = this.mRawData[0];//audio source
        this.mAudioSrc = AudioSourceId.values()[temp];


        //get the supported sources
        ByteBuffer buffer = ByteBuffer.allocate(this.mRawData.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(this.mRawData);
        buffer.position(1);
        temp = buffer.getShort();

        //byte 1 is bits from 8 - 16
        //byte 2 is bits form 0 - 7
        for(int i = 0; i < AudioSourceId.values().length; i++)//all of the available sources
        {
            int result = temp %2;
            temp /= 2;
            if(result != 0)
            {
                this.supportedAudioSrcs.add(AudioSourceId.values()[i+1]);//+1 for no None source
            }
        }

        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        ByteBuffer tempBuff = ByteBuffer.allocate(this.mDataSize);//don't overwrite
        tempBuff.order(ByteOrder.LITTLE_ENDIAN);
        tempBuff.position(0);
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

        tempBuff.put((byte)this.mAudioSrc.getValue());
        tempBuff.putShort((short)0x0000);//ignore available sources when writing

        return tempBuff;
    }

    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        stream.put((byte)this.mAudioSrc.getValue());
        stream.put((byte)this.supportedAudioSrcs.size());
        for (AudioSourceId audioSrc : this.supportedAudioSrcs) {
            stream.put((byte)audioSrc.getValue());
        }
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        this.mAudioSrc = AudioSourceId.getEnumFromId(stream.get());//just the raw value
        int size = stream.get();
        for(int i = 0; i < size; i++)
        {
            //add the items into the list
            this.supportedAudioSrcs.add(AudioSourceId.values()[stream.get()]);
        }
    }

    /**
     * gets the data as an int regardless of size
     * @return the data as an int
     */
    public AudioSourceId getAudioSource()
    {
        return this.mAudioSrc;
    }

    public ArrayList<AudioSourceId> getSupportedAudioSrcs()
    {
        return this.supportedAudioSrcs;
    }

}
