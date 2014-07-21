/**
 * This is a dumbed version of the Device.
 * @author Levi.Balling
 * @date 5/29/2014
 * @version 1
 * This will have specific information about which device you would like to connect to.
 */
package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.SystemDeviceInfo;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;

public class ConnectionDevice {

    //this will be a very generic System Device,
    protected SystemDeviceInfo mSysDevInfo;
    protected DeviceId mDevId;
//    protected  SystemConfiguration mConfig;
//    protected  String mModel;
//    protected  String mPartNumber;
//    protected  String mConsoleName;//reconfigurable to allow user writing it.
    protected CommType mCommType;//this is what type of communication the Device uses

    /**
     * Default constructor
     */
    public ConnectionDevice()
    {
        this.initializeConnectionDevice(DeviceId.NONE, CommType.NONE);
    }

    /**
     * Creates a Connection Device with the Info that we need to now about.
     * @param id Device Id of the Specific System we are looking at
     * @param commType The type of communication the Device uses
     */
    public ConnectionDevice(DeviceId id, CommType commType)
    {
        this.initializeConnectionDevice(id, commType);
    }

    /**
     * This is used to initialize the Connection Device
     * @param id Device Id of the Specific System we are looking at
     * @param commType The type of communication the Device uses
     */
    protected void initializeConnectionDevice(DeviceId id, CommType commType)
    {
        this.mDevId = id;
        this.mCommType = commType;
    }

    // ************GETTERS****************

    /**
     * The Device id of the system
     * @return the device id
     */
    public DeviceId getDevId() {
        return mDevId;
    }

    /**
     * The type of communication of the system.
     * @return the communication type
     */
    public CommType getCommType() {
        return mCommType;
    }

    // ************SETTERS****************


    public void setSysInfoVal(GetSysInfoSts sts)
    {

        this.mDevId = sts.getDevId();
        this.mSysDevInfo = sts.getSysDevInfo();
//        this.mConfig = sts.getConfig();
//        this.mModel = sts.getModel() + "";
//        this.mPartNumber = sts.getPartNumber() + "";
//        this.mConsoleName = sts.getConsoleName();
    }

    /**
     * Sets the Device ID
     * @param devId the Device ID
     */
    public void setDevId(DeviceId devId) {
        this.mDevId = devId;
    }

    /**
     * Sets the Communication type of the system
     * @param commType the Communication type
     */
    public void setCommType(CommType commType) {
        this.mCommType = commType;
    }

    @Override
    public String toString() {
        return  mDevId.name() +
                ": " + this.mSysDevInfo.getConsoleName();
    }
}
