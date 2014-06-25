/**
 * This will combine the BitfieldId and the convert to allow a more fluid use.
 * @author Levi.Balling
 * @date 6/11/2014
 * @version 1
 * This will keep track of whether it has be accessed, or dirtied.
 */
package com.ifit.sparky.fecp.interpreter.bitField;

import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;

import java.io.Serializable;

public class BitfieldObject implements Serializable{

    private BitFieldId mId;
    private BitfieldDataConverter mConverter;
    private boolean mDirty;//just changed.

    public BitfieldObject(BitFieldId id, BitfieldDataConverter converter)
    {
        this.mId = id;
        this.mConverter = converter;
    }

    public BitFieldId getId() {
        return mId;
    }

    public BitfieldDataConverter getConverter() {
        return mConverter;
    }

    public boolean isDirty() {
        return mDirty;
    }

    public void setDirty(boolean mDirty) {
        this.mDirty = mDirty;
    }
}
