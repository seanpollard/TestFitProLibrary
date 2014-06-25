/**
 * Runs through all the tests for the Device Info.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Tests the device info constructors and getters and setters.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.device;

import com.ifit.sparky.fecp.interpreter.bitField.*;
import com.ifit.sparky.fecp.interpreter.device.*;

import junit.framework.TestCase;

import java.util.HashSet;

public class TestDeviceInfo extends TestCase {

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
    public void testConstructor_deviceInfo() throws Exception{

        DeviceInfo info;
        HashSet<BitFieldId> bitSet = new HashSet<BitFieldId>();
        bitSet.add(BitFieldId.KPH);
        bitSet.add(BitFieldId.GRADE);

        info = new DeviceInfo();

        assertEquals(DeviceId.NONE, info.getDevId());
        assertEquals(0,info.getSWVersion());
        assertEquals(0,info.getHWVersion());
        assertEquals(0,info.getSerialNumber());
        assertEquals(0,info.getManufactureNumber());
        assertEquals(0,info.getSupportedBitfields().size());

        info = new DeviceInfo(DeviceId.TREADMILL, 1, 2, 3, 4, bitSet);

        assertEquals(DeviceId.TREADMILL, info.getDevId());
        assertEquals(1,info.getSWVersion());
        assertEquals(2,info.getHWVersion());
        assertEquals(3,info.getSerialNumber());
        assertEquals(4,info.getManufactureNumber());
        assertEquals(2,info.getSupportedWriteBitfields().size());
        assertEquals(0,info.getSupportedReadOnlyBitfields().size());

    }

    /** Tests the getters and Setters.
     *
     * @throws Exception
     */
    public void testGettersSetters_device() throws Exception{
        DeviceInfo info;
        HashSet<BitFieldId> bitSet = new HashSet<BitFieldId>();
        bitSet.add(BitFieldId.GRADE);


        info = new DeviceInfo();
        //new DeviceInfo(DeviceId.TREADMILL, 1, 2, 3, 4, bitSet);

        //set values
        info.setDevId(DeviceId.TREADMILL);
        info.setSWVersion(1);
        info.setHWVersion(2);
        info.setSerialNumber(3);
        info.setManufactureNumber(4);
        info.addBitfield(BitFieldId.KPH);
        info.addAllBitfield(bitSet);

        //get and check
        assertEquals(DeviceId.TREADMILL, info.getDevId());
        assertEquals(1,info.getSWVersion());
        assertEquals(2,info.getHWVersion());
        assertEquals(3,info.getSerialNumber());
        assertEquals(4,info.getManufactureNumber());
        assertEquals(2,info.getSupportedBitfields().size());
        assertEquals(2,info.getSupportedWriteBitfields().size());
        assertEquals(0,info.getSupportedReadOnlyBitfields().size());
    }

}
