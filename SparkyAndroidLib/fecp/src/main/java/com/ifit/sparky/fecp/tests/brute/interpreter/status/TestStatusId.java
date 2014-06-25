/**
 * Tests the StatusId enum Object.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * To make sure the StatusId works properly we will use it to test comparisons, get descriptions
 * and more.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

public class TestStatusId extends TestCase {

    /**
     * Setups the TestRunner for StatusId
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for StatusId
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests the StatusId enum values to make sure they work correctly
     */
    public void testEnum() throws Exception{
        StatusId idOne = StatusId.DEV_NOT_SUPPORTED;
        StatusId idTwo = StatusId.DONE;

        assertEquals(StatusId.DEV_NOT_SUPPORTED, idOne);
        assertEquals(0x00, idOne.getVal());

        assertEquals(StatusId.DONE, idTwo);
        assertEquals(0x02, idTwo.getVal());
        assertNotNull(StatusId.DEV_NOT_SUPPORTED.getDescription());

    }

    /**
     * Runs through the Tests for the Enum values
     * @throws Exception
     */
    public void testGetStatic_DeviceId() throws Exception{
        try
        {
            StatusId idOne = StatusId.getStatusId(0);
            assertEquals(StatusId.DEV_NOT_SUPPORTED, idOne);
        }
        catch (Exception ex)
        {
            fail();//shouldn't throw an exception
        }
        try
        {
            StatusId.getStatusId(257);
            fail();//should throw an exception before here
        }
        catch (InvalidStatusException ex)
        {
            assertTrue(true);//this should throw an exception
        }
    }

}
