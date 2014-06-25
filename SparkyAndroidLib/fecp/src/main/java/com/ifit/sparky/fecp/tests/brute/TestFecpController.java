/**
 * Tests the fecp controller.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * This class should run through more tests than any other test.
 * This is where all the magic happens.
 */
package com.ifit.sparky.fecp.tests.brute;

import android.content.Context;
import android.test.ServiceTestCase;

import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import junit.framework.TestCase;

import java.lang.reflect.Method;

public class TestFecpController extends TestCase {

    /**
     * Tests the constructor of the fecpController
     * @throws Exception
     */
    public void testConstructor_fecpController() throws Exception
    {
        FecpController controller;
        Context c;
        TempFecpCallbacker callback;

        c = getTestContext();
        callback = new TempFecpCallbacker();

        controller = new FecpController(CommType.USB);

        assertEquals(CommType.USB, controller.getCommType());
        assertEquals(DeviceId.MAIN, controller.getSysDev().getInfo().getDevId());
        assertEquals(false, controller.getIsConnected());
    }

    public void testInitializeConnection_FecpController() throws Exception {
        //this will throw an error
        FecpController controller;
        Context c;
        TempFecpCallbacker callback;

        c = getTestContext();
        callback = new TempFecpCallbacker();
        controller = new FecpController(CommType.USB);

        assertEquals(CommType.USB, controller.getCommType());
        assertEquals(DeviceId.MAIN, controller.getSysDev().getInfo().getDevId());
        assertEquals(false, controller.getIsConnected());

    }

    /**
     * @return The {@link android.content.Context} of the test project.
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
