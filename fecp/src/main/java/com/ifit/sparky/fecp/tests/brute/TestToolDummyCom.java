/**
 * Since the purpose of this file isn't to format messages, but to validate the FecpCmdHandler.
 * @author Levi.Balling
 * @date 2/6/14
 * @version 1
 * We will only send data, this class will hold onto the data till it is overwritten to.
 */
package com.ifit.sparky.fecp.tests.brute;

import android.util.Log;

import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.ConnectionDevice;
import com.ifit.sparky.fecp.communication.SystemStatusListener;
import com.ifit.sparky.fecp.error.ErrorReporting;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class TestToolDummyCom implements CommInterface {


    public TestToolDummyCom()
    {

    }
    private ByteBuffer mSendBuffer;

    /**
     * sends the command and waits for the reply to handle the buffer
     *
     * @param buff the command buffer to send
     * @return returns the array sent
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff, int timeout) {
        this.mSendBuffer = buff.duplicate();
        try {

            Thread.sleep(timeout);
        }
        catch (Exception ex)
        {
            Log.e("Sleep fail", ex.getMessage());
        }
        return this.mSendBuffer;
    }

    /**
     * Needs to report error with the err
     *
     * @param errReporterCallBack needs to be called to handle errors
     */
    @Override
    public void setupErrorReporting(ErrorReporting errReporterCallBack) {

    }

    /**
     * Used to determined if we should attempt to reconnect to the machine, or if nothing is going on.
     *
     * @param active true for communicating, false for no communication.
     */
    @Override
    public void setCommActive(boolean active) {

    }

    /**
     * This allows the user to scan for all of the different devices, when finished scanning it will
     * Call the listener to allow them to select with
     *
     * @param listener listener to be called after scanning is complete.
     */
    @Override
    public void scanForSystems(ScanSystemListener listener) {
        listener.onScanFinish(new ArrayList<ConnectionDevice>());
    }

    /**
     * Initializes the connection to the communication items.
     */
    @Override
    public SystemDevice initializeCommConnection() {

        return null;
    }

    /**
     * Handles multiple listeners so we can notify both ifit and the fecp controller.
     *
     * @param listener the listener for the callbacks
     */
    @Override
    public void addConnectionListener(SystemStatusListener listener) {

    }


    /**
     * Removes all the Connection listeners,
     */
    @Override
    public void clearConnectionListener() {

    }

    /**
     * gets the list of System Status Listeners
     *
     * @return list of all the System Status Listeners
     */
    @Override
    public List<SystemStatusListener> getSystemStatusListeners() {
        return null;
    }

    /**
     * sends the command and waits for the reply to handle the buffer
     *
     * @param buff the command buffer to send
     * @return returns the array sent
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff) {
        this.mSendBuffer = buff.duplicate();
        try {

            Thread.sleep(50);
        }
        catch (Exception ex)
        {
            Log.e("Sleep fail", ex.getMessage());
        }
        return this.mSendBuffer;
    }
}
