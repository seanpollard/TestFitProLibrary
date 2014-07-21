/**
 * Holds all the main information about the device.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Holds all the information about the device, version numbers, supported information.
 */
package com.ifit.sparky.fecp.interpreter.device;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

public class DeviceInfo implements Serializable {

    private DeviceId mId;
    private int mSWVersion;
    private int mHWVersion;
    private int mSerialNumber;
    private int mManufactureNumber;
    private int mSections;
    private Set<BitFieldId> mSupportedBitFields; //list of available items

    /**
     * The default constructor
     */
    public DeviceInfo()
    {
        this.mId = DeviceId.NONE;
        this.mSWVersion = 0;
        this.mHWVersion = 0;
        this.mSerialNumber = 0;
        this.mManufactureNumber = 0;
        this.mSections = 0;
        this.mSupportedBitFields = new HashSet<BitFieldId>();
    }


    /**
     * Constructs all the different values in the Device info Object
     * @param id the Device Id
     * @param swVersion The software version
     * @param hwVersion the hardware version
     * @param serialNum the serial number
     * @param manufactureNum the manufacture number
     * @param bitFields the supported bitfields
     */
    public DeviceInfo(
            DeviceId id,
            int swVersion,
            int hwVersion,
            int serialNum,
            int manufactureNum,
            Collection<BitFieldId> bitFields)
    {
        this.mId = id;
        this.mSWVersion = swVersion;
        this.mHWVersion = hwVersion;
        this.mSerialNumber = serialNum;
        this.mManufactureNumber = manufactureNum;
        this.mSupportedBitFields = new HashSet<BitFieldId>();
        addAllBitfield(bitFields);
    }

    /**
     * gets the Device Id.
     * @return The Device Id
     */
    public DeviceId getDevId()
    {
        return this.mId; /* The Device Id */
    }

    /**
     * Gets the SW Version Number.
     * @return the SW Version Number
     */
    public int getSWVersion()
    {
        return this.mSWVersion;
    }

    /**
     * Gets the HW Version Number.
     * @return the HW Version Number
     */
    public int getHWVersion()
    {
        return this.mHWVersion;
    }

    /**
     * Gets the Serial Number.
     * @return the Serial Number
     */
    public int getSerialNumber()
    {
        return this.mSerialNumber;
    }

    /**
     * Gets the Manufacture Number
     * @return the Manufacture Number
     */
    public int getManufactureNumber()
    {
        return this.mManufactureNumber;
    }

    /**
     * Gets the Number sections
     * @return the Number of sections
     */
    public int getSections()
    {
        return this.mSections;
    }

    /**
     * gets the set of Supported Bitfields
     * @return set of BitfieldIds
     */
    public Set<BitFieldId> getSupportedBitfields()
    {
        return this.mSupportedBitFields; /* list of commands */
    }

    /**
     * gets the set of Supported Write enabled Bitfields
     * @return set of Writable BitfieldIds
     */
    public Set<BitFieldId> getSupportedWriteBitfields()
    {
        HashSet<BitFieldId> writeBitfields = new HashSet<BitFieldId>();
        for(BitFieldId bit : this.mSupportedBitFields)
        {
            if(!bit.getReadOnly())
            {
                writeBitfields.add(bit);
            }
        }
        return writeBitfields; /* set of Write Bitfields for the device */
    }

    /**
     * gets the set of Supported Read Only Bitfields
     * @return set of Read only BitfieldIds
     */
    public Set<BitFieldId> getSupportedReadOnlyBitfields()
    {
        HashSet<BitFieldId> readBitfields = new HashSet<BitFieldId>();
        for(BitFieldId bit : this.mSupportedBitFields)
        {
            if(bit.getReadOnly())
            {
                readBitfields.add(bit);
            }
        }
        return readBitfields; /* set of Write Bitfields for the device */
    }

    /**
     * Sets the device Id.
     * @param id the Device ID
     */
    public void setDevId(DeviceId id)
    {
        this.mId = id;
    }

    /**
     * Sets the SW Version Number
     * @param version the SW Version Number
     */
    public void setSWVersion(int version)
    {
        this.mSWVersion = version;
    }

    /**
     * Sets the HW Version Number
     * @param version the HW Version Number
     */
    public void setHWVersion(int version)
    {
        this.mHWVersion = version;
    }

    /**
     * Sets the Serial Number
     * @param number the Serial Number
     */
    public void setSerialNumber(int number)
    {
        this.mSerialNumber = number;
    }

    /**
     * Sets the Manufacture Number
     * @param number the Manufacture Number
     */
    public void setManufactureNumber(int number)
    {
        this.mManufactureNumber = number;
    }

    /**
     * Adds a bitfield item to the device, or what the device supports.
     * @param bitfield the bitfield
     */
    public void addBitfield(BitFieldId bitfield)
    {
        if(bitfield.getSection() > this.mSections)
        {
            this.mSections = bitfield.getSection();
        }

        this.mSupportedBitFields.add(bitfield);
    }

    /**
     * Adds a collection of bitfields to the device.
     * @param bitfields to add to the device.
     */
    public void addAllBitfield(Collection<BitFieldId> bitfields)
    {
        for(BitFieldId singleBit : bitfields)
        {
            this.addBitfield(singleBit);
        }
    }

    /**
     * Gets the info from the buffer from the current position
     * @param buff  the buffer that has the DeviceInfo
     */
    public void interpretInfo(ByteBuffer buff) throws InvalidBitFieldException {
        byte bits;
        //read Sw Version
        this.setSWVersion(buff.get());// read 1 byte

        //read Hw Version
        this.setHWVersion(buff.get());// read 1 byte

        //read Serial Number
        this.setSerialNumber(buff.getInt());//read 4 bytes

        //Read Manufacture Number
        this.setManufactureNumber(buff.getShort());//read 2 bytes

        //Read the number of Section bytes
        this.mSections = buff.get();

        //Read the data bits

        for(int i= 0; i < this.mSections; i++)
        {
            bits = buff.get();
            //loop through each bit and check if it 1 or not
            for(int j = 0; j < 8; j++)
            {
                if((bits & (1 << j)) != 0)//if 1
                {
                    this.addBitfield(BitFieldId.getBitFieldId(i,j));
                }
            }
        }
    }
}
