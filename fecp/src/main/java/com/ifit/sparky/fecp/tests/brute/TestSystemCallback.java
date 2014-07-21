/**
 * This is to test the SystemCallback interface.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * If there is a disconnect the disconnect method is called, same with connect.
 */
package com.ifit.sparky.fecp.tests.brute;

import com.ifit.sparky.fecp.communication.SystemStatusListener;

import junit.framework.TestCase;

public class TestSystemCallback extends TestCase {


    /**
     * tests the callback for the System callback
     * @throws Exception
     */
    public void testCallback_SystemCallback() throws Exception
    {
        SystemStatusListener callback = new TempFecpCallbacker();
        //test the connect callback
        assertEquals(false, ((TempFecpCallbacker)callback).getIsConnectedStatus());
        callback.systemDeviceConnected(null);
        assertEquals(true, ((TempFecpCallbacker)callback).getIsConnectedStatus());

        //test the disconnect callback
        callback.systemDisconnected();
        assertEquals(false, ((TempFecpCallbacker)callback).getIsConnectedStatus());
    }
}
