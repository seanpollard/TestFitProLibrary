/**
 * Tests the System Configuration.
 * @author Levi.Balling
 * @date 12/18/13
 * @version 1
 * Checks to make sure the configurations are working correctly.
 */
package com.ifit.sparky.fecp.tests.brute;

import com.ifit.sparky.fecp.interpreter.device.SystemConfiguration;

import junit.framework.TestCase;

public class testSystemConfiguration   extends TestCase {

    /**
     * Setups the TestRunner for System Configuration.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for System Configuration.
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
    public void testConstructor_systemConfiguration() throws Exception{
        SystemConfiguration config;

        config = SystemConfiguration.SLAVE;

        assertEquals(SystemConfiguration.SLAVE, config);
        assertEquals(0, config.getVal());
        assertNotNull(config.getDescription());

        config = SystemConfiguration.MASTER;
        assertEquals(SystemConfiguration.MASTER, config);
        assertEquals(1, config.getVal());
        assertNotNull(config.getDescription());

        config = SystemConfiguration.MULTI_MASTER;
        assertEquals(SystemConfiguration.MULTI_MASTER, config);
        assertEquals(2, config.getVal());
        assertNotNull(config.getDescription());
    }
}