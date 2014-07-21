/**
 * This will handle all of the data of the Main System Info.
 * @author Levi.Balling
 * @date 6/13/2014
 * @version 1
 * This will be used to determine what system we are connecting to, when you scan for
 * devices it will be a bunch of SystemDeviceInfo.
 */
package com.ifit.sparky.fecp.interpreter.device;

import com.ifit.sparky.fecp.interpreter.bitField.converter.LanguageId;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class SystemDeviceInfo {

    private SystemConfiguration mConfig;//slave,master, or multi master
    private int mModel;
    private int mPartNumber;
    private double mCpuUse;
    private int mNumberOfTasks;
    private int mIntervalTime;
    private int mCpuFrequency;
    private int mPollingFrequency;//the min frequency to request in milliseconds
    private boolean mIsDefaultUnitMetric;//the min frequency to request in milliseconds
    private LanguageId mLanguage;//the default system Language
    private String mMcuName;
    private String mConsoleName;

    /**
     * The System may only be created from a getSystemInfoCommand
     * @param buff buffer that has the raw data
     */
    public SystemDeviceInfo(ByteBuffer buff)
    {
        int nameLength;
        //populates all of the items in the object
        //System config
        this.mConfig = SystemConfiguration.convert(buff.get());
        //model Number
        this.mModel = buff.getInt();
        //part number
        this.mPartNumber = buff.getInt();
        //Cpu usage
        this.mCpuUse = buff.getShort();//240 == 0.240
        this.mCpuUse /= 1000;
        //number Of tasks
        this.mNumberOfTasks = buff.get();
        //cpu Min interval
        this.mIntervalTime = buff.getShort();
        //cpu clk freq
        this.mCpuFrequency = buff.getInt();
        //Polling Freq
        this.mPollingFrequency = buff.getShort();
        //Get Default System Units
        this.mIsDefaultUnitMetric = (buff.get() != 0);//0(false) for English,1(true) for Metric
        //get the system's language
        this.mLanguage = LanguageId.getLanguageFromId(buff.get());
        //mcu name length
        nameLength = buff.get();
        //mcu name
        this.mMcuName = "";
        for(int i = 0; i < nameLength; i++)
        {
            this.mMcuName += buff.get();
        }
        //console name length
        nameLength = buff.get();
        //console name
        this.mConsoleName = "";
        for(int i = 0; i < nameLength; i++)
        {
            this.mConsoleName += buff.get();
        }
    }

    public SystemDeviceInfo()
    {
        this.mConfig = SystemConfiguration.SLAVE;
        this.mConsoleName = "";
        this.mModel = 0;
        this.mPartNumber = 0;
        this.mCpuUse = 0.0;
        this.mNumberOfTasks = 0;
        this.mIntervalTime = 0;
        this.mCpuFrequency = 0;
        this.mPollingFrequency = 0;
        this.mIsDefaultUnitMetric = false;
        this.mLanguage = LanguageId.NONE;
        this.mMcuName = "";
        this.mConsoleName = "";
    }



    /**
     * Gets the system configuration
     * @return the system configuration
     */
    public SystemConfiguration getConfig()
    {
        return this.mConfig;
    }

    /**
     * Gets the model
     * @return the Model Number
     */
    public int getModel() {
        return mModel;
    }

    /**
     * Gets the part number for the main system
     * @return gets the system's Part Number
     */
    public int getPartNumber() {
        return mPartNumber;
    }

    /**
     * Gets the Current CPU of the System
     * @return the CPU usage
     */
    public double getCpuUse() {
        return mCpuUse;
    }

    /**
     * Gets the number of Tasks used
     * @return the number of tasks used
     */
    public int getNumberOfTasks() {
        return mNumberOfTasks;
    }

    /**
     * Gets the interval time in uSeconds
     * @return the interval time in uSeconds
     */
    public int getIntervalTime() {
        return mIntervalTime;
    }

    /**
     * Gets the CPU frequency
     * @return the CPU frequency in Hz
     */
    public int getCpuFrequency() {
        return mCpuFrequency;
    }

    /**
     * Gets the fastest interval to send commands for the specific system.
     * @return interval in milliseconds(ms)
     */
    public int getPollingFrequency() {
        return mPollingFrequency;
    }

    public boolean isDefaultUnitMetric() {
        return mIsDefaultUnitMetric;
    }

    /**
     * Gets the System's supported Language Id
     * @return The System's Language.
     */
    public LanguageId getLanguage() {
        return mLanguage;
    }

    /**
     * Gets the name of the Mcu
     * @return the Mcu name
     */
    public String getMcuName() {
        return mMcuName;
    }

    /**
     * gets the Name of the Console according to the Main Device
     * @return Main Device
     */
    public String getConsoleName() {
        return mConsoleName;
    }

    /**
     * Writes the Stream of the systemDev Info
     * @param buff of the System Dev Info
     * @throws IOException if there is a issue adding the info to the stream.
     */
    public void writeObject(ByteBuffer buff)throws IOException
    {
        ByteBuffer tempBuff = ByteBuffer.allocate(2000);//we don't need all of this, but it will help
        tempBuff.order(ByteOrder.LITTLE_ENDIAN);
        //write the data we are concerned about

        if(this.mConfig== SystemConfiguration.MASTER)
        {
            buff.put((byte) SystemConfiguration.PORTAL_TO_MASTER.getVal());
        }
        else if(this.mConfig == SystemConfiguration.MULTI_MASTER)
        {
            buff.put((byte) SystemConfiguration.PORTAL_TO_MASTER.getVal());
        }
        else if(this.mConfig == SystemConfiguration.SLAVE)
        {
            //portal to slave
            buff.put((byte) SystemConfiguration.PORTAL_TO_SLAVE.getVal());
        }
        else
        {
            buff.put((byte) this.mConfig.getVal());
        }
        buff.putInt(this.mModel);
        buff.putInt(this.mPartNumber);
        buff.putDouble(this.mCpuUse);
        buff.putInt(this.mNumberOfTasks);
        buff.putInt(this.mIntervalTime);
        buff.putInt(this.mCpuFrequency);
        buff.putShort((short) this.mPollingFrequency);

        if(this.mIsDefaultUnitMetric)
        {
            buff.putInt(1);
        }
        else
        {
            buff.putInt(0);
        }

        buff.put((byte) this.mLanguage.getLanguageId());

        buff.put((byte)this.mMcuName.length());//length of string
        buff.put(this.mMcuName.getBytes());

        buff.put((byte)this.mConsoleName.length());//length of string
        buff.put(this.mConsoleName.getBytes());

    }

    public void readObject(ByteBuffer stream) throws IOException
    {
        this.mConfig = SystemConfiguration.convert(stream.get());
        this.mModel = stream.getInt();
        this.mPartNumber = stream.getInt();
        this.mCpuUse= stream.getDouble();
        this.mNumberOfTasks = stream.getInt();
        this.mIntervalTime = stream.getInt();
        this.mCpuFrequency = stream.getInt();
        this.mPollingFrequency = stream.getShort();

        //Get Default System Units
        this.mIsDefaultUnitMetric = (stream.getInt() != 0);//0(false) for English,1(true) for Metric

        this.mLanguage = LanguageId.getLanguageFromId(stream.get());

        int strLength = stream.get();
        byte[] strArr = new byte[strLength];
        stream.get(strArr, 0, strLength);
        String str = new String( strArr, Charset.forName("UTF-8") );
        this.mMcuName = str;

        strLength = stream.get();
        strArr = new byte[strLength];
        stream.get(strArr, 0, strLength);
        str = new String( strArr, Charset.forName("UTF-8") );

        this.mConsoleName = str;
    }
}
