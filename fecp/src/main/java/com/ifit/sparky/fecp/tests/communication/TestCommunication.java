/**
 * Tests the Communication Object.
 * @author Ryan.Tensmeyer
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * Tests the communication functions
 */

package com.ifit.sparky.fecp.tests.communication;

import android.content.Context;
import android.content.Intent;
import android.test.ServiceTestCase;

import com.ifit.sparky.fecp.communication.UsbComm;

import junit.framework.TestCase;

import java.lang.reflect.Method;

public class TestCommunication extends TestCase {

    /**
     * Setups the TestRunner for Command.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for Command.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests the Constructor.
     * @throws Exception
     */
    public void testConstructor_communicationUsb() throws Exception{
        UsbComm usbCommObject;
//        Context c = getTestContext();
//        usbCommObject = new UsbComm(c, null);
//
//        //assert the object was created
//        assertNotNull(usbCommObject);
//
//        //get rid of warnings in UsbComm.java
//        Intent intent = Intent.getIntentOld("");
//        usbCommObject.onResumeUSB(intent);
//        usbCommObject.getDrop_Count();
//        usbCommObject.getEp1_RX_Count();
//        usbCommObject.getEp3_RX_Count();
    }

    /**
     * @return The {@link Context} of the test project.
     */
    private Context getTestContext()
    {
        try
        {
            Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
            return (Context) getTestContext.invoke(this);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
    }

}
