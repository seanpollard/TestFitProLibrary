/**
 * Tests the System device.
 * @author Levi.Balling
 * @date 12/18/13
 * @version 1
 * Tests the system device to make sure the specific items work the way we expect.
 */
package com.ifit.sparky.fecp.tests.brute;

import com.ifit.sparky.fecp.interpreter.device.SystemConfiguration;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;

public class TestSystemDevice  extends TestCase {

    /**
     * Setups the TestRunner for System Device.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for System Device.
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
    public void testConstructor_systemDevice() throws Exception{

        SystemDevice device;
        //test default constructor
        device = new SystemDevice();

        assertEquals(DeviceId.NONE, device.getInfo().getDevId());//just to test the super constructor
        assertEquals(SystemConfiguration.SLAVE, device.getSysDevInfo().getConfig());

        //test 2nd constructor
        device = new SystemDevice(DeviceId.INCLINE_TRAINER);
        assertEquals(DeviceId.INCLINE_TRAINER, device.getInfo().getDevId());//just to test the super constructor
        assertEquals(SystemConfiguration.SLAVE, device.getSysDevInfo().getConfig());
    }

    /** Tests the Getters and Setters.
     *
     * @throws Exception
     */
    public void testGetterSetter_systemDevice() throws Exception{

        SystemDevice device;
        device = new SystemDevice();

        //test set Config setter
        assertEquals(SystemConfiguration.SLAVE, device.getSysDevInfo().getConfig());//make sure it is slave first
//        device.setConfig(SystemConfiguration.MULTI_MASTER);

//        assertEquals(SystemConfiguration.MULTI_MASTER, device.getConfig());//make sure it is slave first


    }
}
