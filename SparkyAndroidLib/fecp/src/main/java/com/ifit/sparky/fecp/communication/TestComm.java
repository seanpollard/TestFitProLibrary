/**
 * This is a Empty Communication specificially for testing. It prevents all communication to anything.
 * @author Levi.Balling
 * @date 4/28/2014
 * @version 1
 * This puts all the system requirements on Testing, No part of the hardware will work with this.
 */
package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.error.ErrorReporting;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class TestComm implements CommInterface {

    private LinkedList<SystemStatusListener> mUsbConnectionListener;
    public TestComm()
    {
        //nothing to do.
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
     * sends the command and waits for the reply to handle the buffer
     *
     * @param buff the command buffer to send
     * @return a buffer with the message 0 if failed
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff) {
        return null;
    }

    /**
     * Send and receive with a timeout
     *
     * @param buff    the buffer to send
     * @param timeout the max time you want to take till it is send
     * @return the buffer from the device 0 in the first byte for failed
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff, int timeout) {
        return null;
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
        //currently does nothing.
    }

    /**
     * This allows the user to scan for all of the different devices, when finished scanning it will
     * Call the listener to allow them to select with
     *
     * @param listener listener to be called after scanning is complete.
     */
    @Override
    public void scanForSystems(ScanSystemListener listener) {

    }

    /**
     * gets the list of System Status Listeners
     *
     * @return list of all the System Status Listeners
     */
    @Override
    public List<SystemStatusListener> getSystemStatusListeners() {
        return this.mUsbConnectionListener;
    }
}
