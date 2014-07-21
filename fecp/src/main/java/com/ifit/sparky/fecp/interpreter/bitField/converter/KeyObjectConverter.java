/**
 * Converts the raw data into a KeyObject.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * Converts the raw data into a KeyObject, that holds the Cooked keycode, the rawKeyCode data,
 * the time it was pressed in respects to the beginning of the workout, and how long the button was
 * held for in milliseconds.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.key.InvalidKeyCodeException;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class KeyObjectConverter extends BitfieldDataConverter implements Serializable {

    private KeyObject mKey;

    /**
     * the default constructor
     */
    public KeyObjectConverter()
    {
        super();
        this.mDataSize = 10;
        this.mKey = new KeyObject();
    }

    @Override
    public BitfieldDataConverter getData() throws InvalidBitFieldException
    {
        ByteBuffer buff;
        buff = ByteBuffer.allocate(this.mRawData.length);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.put(this.mRawData);
        buff.position(0);

        //convert the Next 2 bytes into the CookedKeycode
        try {
            this.mKey.setCode(buff.getShort());
        } catch (InvalidKeyCodeException e) {
            throw new InvalidBitFieldException(e.getMessage());
        }

        //convert the first 4 bytes into the rawKeycode value
        this.mKey.setRawKeyCode(buff.getLong());

        //convert the Next 2 bytes into the Time it was pressed in seconds
        this.mKey.setTimePressed(buff.getShort());

        //converts the Next 2 Bytes into how long it was held
        this.mKey.setTimeHeld(buff.getShort());

        return this;
    }

    @Override
    public ByteBuffer convertData(Object obj) throws InvalidBitFieldException {

        throw new InvalidBitFieldException("KeyCodes bitfield doesn't support converting " +
                "into Raw Data");
    }


    @Override
    public void writeObject(ByteBuffer stream) throws IOException {

        this.mKey.writeObject(stream);
    }

    @Override
    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {

        this.mKey.readObject(stream);
    }

    /**
     * gets the KeyObject
     * @return the KeyObject
     */
    public KeyObject getKeyObject()
    {
        return this.mKey;
    }

}