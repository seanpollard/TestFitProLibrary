/**
 * Tests the Device Id enum.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * Tests the Device Id enum for any abnormal values.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.device;

import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.InvalidDeviceException;

import junit.framework.TestCase;

public class TestDeviceId extends TestCase {

    /**
     * Setups the TestRunner for DeviceId.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for DeviceId.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * This tests the enums to make sure the values are correct
     */
    public void testEnum() throws Exception{
        DeviceId idOne = DeviceId.NONE;
        DeviceId idTwo = DeviceId.TREADMILL;

        assertEquals(DeviceId.NONE, idOne);
        assertEquals(0x00, idOne.getVal());

        assertEquals(DeviceId.TREADMILL, idTwo);
        assertEquals(0x04, idTwo.getVal());
        assertNotNull(DeviceId.TREADMILL.getDescription());
    }

    /**
     * Runs through the Tests for the Enum values
     * @throws Exception
     */
    public void testGetStatic_DeviceId() throws Exception{
        try
        {
            DeviceId idOne = DeviceId.getDeviceId(0);
            assertEquals(DeviceId.NONE, idOne);
        }
        catch (Exception ex)
        {
            fail();//shouldn't throw an exception
        }
        try
        {
            DeviceId.getDeviceId(257);
            fail();//should throw an exception before here
        }
        catch (InvalidDeviceException ex)
        {
            assertTrue(true);//this should throw an exception
        }
    }
}
