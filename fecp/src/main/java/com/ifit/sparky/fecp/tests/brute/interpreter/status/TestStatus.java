/**
 * Tests all the items in the status SuperClass
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This class will test all the items of the status superclass. This includes the enums, invalid
 * inputs, and valid inputs
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.InfoSts;
import com.ifit.sparky.fecp.interpreter.status.Status;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

public class TestStatus extends TestCase{

    /**
     * Setups the TestRunner for Status.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    /**
     * Closes the TestRunner for Status.
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
    public void testConstructor_status() throws Exception{

        Status statusObjOne;
        Status statusObjTwo;

        statusObjOne = new Status();

        // assert default values
        assertEquals(StatusId.DEV_NOT_SUPPORTED, statusObjOne.getStsId());
        assertEquals(0, statusObjOne.getLength());
        assertEquals(CommandId.NONE, statusObjOne.getCmdId());
        assertEquals(DeviceId.NONE, statusObjOne.getDevId());

        //assert none default values
        statusObjTwo = new Status(StatusId.DONE, 1, CommandId.CONNECT, DeviceId.TREADMILL);

        assertEquals(StatusId.DONE, statusObjTwo.getStsId());
        assertEquals(1, statusObjTwo.getLength());
        assertEquals(CommandId.CONNECT, statusObjTwo.getCmdId());
        assertEquals(DeviceId.TREADMILL, statusObjTwo.getDevId());
    }

    /** This test method is to throw errors when the values are out of the limits
     * @throws Exception
     */
    public void testConstructorExceptions_status() throws Exception{

        Status statusObjOne;

        statusObjOne = new Status();
        //assign invalid values, and check exceptions
        try
        {
            //test
            statusObjOne.setLength(statusObjOne.MAX_MSG_LENGTH);
            assertTrue(true);
            statusObjOne.setLength(0);
            assertTrue(true);
        }
        catch (Exception ex)
        {
            fail();
        }
        try {

            statusObjOne.setLength(statusObjOne.MAX_MSG_LENGTH+1);
            fail();
        }
        catch (Exception ex)
        {
            assertTrue(true);
        }


    }

    /** Tests the setters for the status
     * @throws Exception
     */
    public void testSetters_status() throws Exception{

        Status statusObjOne;

        statusObjOne = new Status();
        //test setters
        statusObjOne.setStsId(StatusId.DONE);
        statusObjOne.setLength(1);
        statusObjOne.setCmdId(CommandId.CONNECT);
        statusObjOne.setDevId(DeviceId.TREADMILL);

        assertEquals(StatusId.DONE, statusObjOne.getStsId());
        assertEquals(1, statusObjOne.getLength());
        assertEquals(CommandId.CONNECT, statusObjOne.getCmdId());
        assertEquals(DeviceId.TREADMILL, statusObjOne.getDevId());
    }

    /**
     * Checks to make sure we can cast the objects correctly
     * @throws Exception
     */
    public void testSubStatus_status() throws Exception
    {
        Status blankSts = new Status();


    }
}
