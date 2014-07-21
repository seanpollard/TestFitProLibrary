/**
 * Tests the Device Object.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * Tests the Device constructors, getters and setters, for any abnormal values.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.device;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.DeviceInfo;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashSet;

public class TestDevice extends TestCase {

    /**
     * Setups the TestRunner for Device.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for Device.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testConstructor_device() throws Exception{

        Device deviceObjOne;
        DeviceInfo info;
        HashSet<Command> cmdSet = new HashSet<Command>();
        ArrayList<Device> deviceList = new ArrayList<Device>();

        //test default
        deviceObjOne = new Device();
        info = new DeviceInfo();

        assertEquals(DeviceId.NONE,deviceObjOne.getInfo().getDevId());
        assertEquals(4, deviceObjOne.getCommandSet().size());
        assertEquals(0, deviceObjOne.getSubDeviceList().size());
        assertEquals(0, deviceObjOne.getInfo().getSupportedBitfields().size());
        assertNotNull(deviceObjOne.getInfo());//should match up

        //test second constructor
        deviceObjOne = new Device(DeviceId.INCLINE_TRAINER);
        info.setDevId(DeviceId.INCLINE_TRAINER);

        assertEquals(DeviceId.INCLINE_TRAINER,deviceObjOne.getInfo().getDevId());
        assertEquals(4, deviceObjOne.getCommandSet().size());
        assertEquals(0, deviceObjOne.getSubDeviceList().size());
        assertEquals(0, deviceObjOne.getInfo().getSupportedBitfields().size());
        assertNotNull(deviceObjOne.getInfo());//should match up

        //test Third constructor
        //add commands
        cmdSet.add(new InfoCmd(DeviceId.TREADMILL));
        //add subDevices
        deviceList.add(new Device());
        deviceList.add(new Device());

        deviceObjOne = new Device(cmdSet, deviceList, info);

        assertEquals(DeviceId.INCLINE_TRAINER,deviceObjOne.getInfo().getDevId());
        assertEquals(4, deviceObjOne.getCommandSet().size());
        assertEquals(2, deviceObjOne.getSubDeviceList().size());
        assertNotNull(deviceObjOne.getInfo());//should match up
    }

    /** Tests the getters and Setters.
     *
     * @throws Exception
     */
    public void testGettersSetters_device() throws Exception{

        Device deviceObjOne;
        DeviceInfo info;
        ArrayList<Device> deviceList = new ArrayList<Device>();
        HashSet<Command> cmdSet = new HashSet<Command>();

        //setup default
        deviceObjOne = new Device();
        info = new DeviceInfo();

        assertEquals(DeviceId.NONE,deviceObjOne.getInfo().getDevId());
        assertEquals(4, deviceObjOne.getCommandSet().size());
        assertEquals(0, deviceObjOne.getSubDeviceList().size());
        assertEquals(0, deviceObjOne.getInfo().getSupportedBitfields().size());
        assertNotNull(deviceObjOne.getInfo());//should match up

        //test set Sub device list
        deviceList.add(new Device());
        deviceList.add(new Device(DeviceId.TREADMILL));
        deviceObjOne.addAllSubDevice(deviceList);
        deviceObjOne.addSubDevice(new Device(DeviceId.INCLINE_TRAINER));

        assertEquals(DeviceId.NONE, deviceObjOne.getInfo().getDevId());
        assertEquals(4, deviceObjOne.getCommandSet().size());
        assertNotNull(deviceObjOne.getSubDevice(DeviceId.TREADMILL));
        assertNotNull(deviceObjOne.getSubDevice(0x05));//incline trainer
        assertEquals(3, deviceObjOne.getSubDeviceList().size());
        assertTrue(deviceObjOne.containsDevice(DeviceId.TREADMILL));

        //Test set command
        assertEquals(4,deviceObjOne.getCommandSet().size());
        //Test set Commands and get command and command by idVal
        cmdSet.add(new InfoCmd(DeviceId.INCLINE_TRAINER));
        assertEquals(4, deviceObjOne.getCommandSet().size());
        assertEquals(CommandId.GET_INFO,deviceObjOne.getCommand(CommandId.GET_INFO).getCmdId());
        assertEquals(CommandId.GET_INFO,deviceObjOne.getCommand(CommandId.GET_INFO.getVal()).getCmdId());

        //test the getInfo
        deviceObjOne.setDeviceInfo(info);
        assertEquals(DeviceId.NONE, deviceObjOne.getInfo().getDevId());

    }
}