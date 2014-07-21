/**
 * Interface for all communication types (e.g. usb, uart, blue tooth).
 * @author Ryan.Tensmeyer
 * @date 12/10/13
 * @version 1
 * Release Date
 * @date 12/10/13
 */

package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.error.ErrorReporting;

import java.nio.ByteBuffer;
import java.util.List;

public interface CommInterface {

//    public interface DeviceConnectionListener{
//        void onDeviceConnected();
//        void onDeviceDisconnected();
//    }

    /**
     * This interface will allow for an asynchronous callback to reply with all of the available
     * Devices this may connect to.
     */
    public interface ScanSystemListener {
        void onScanFinish(List<ConnectionDevice> devices);
    }

    /**
     * Initializes the connection to the communication items.
     */
    SystemDevice initializeCommConnection();

    /**
     * Handles multiple listeners so we can notify both ifit and the fecp controller.
     * @param listener the listener for the callbacks
     */
    void addConnectionListener(SystemStatusListener listener);

    /**
     * Removes all the Connection listeners,
     */
    void clearConnectionListener();

    /**
     * gets the list of System Status Listeners
     * @return list of all the System Status Listeners
     */
    List<SystemStatusListener> getSystemStatusListeners();


    /**
     * sends the command and waits for the reply to handle the buffer
     * @param buff the command buffer to send
     * @return a buffer with the message 0 if failed
     */
    ByteBuffer sendAndReceiveCmd(ByteBuffer buff);

    /**
     * Send and receive with a timeout
     * @param buff the buffer to send
     * @param timeout the max time you want to take till it is send
     * @return the buffer from the device 0 in the first byte for failed
     */
    ByteBuffer sendAndReceiveCmd(ByteBuffer buff, int timeout);

    /**
     * Needs to report error with the err
     * @param errReporterCallBack needs to be called to handle errors
     */
    void setupErrorReporting(ErrorReporting errReporterCallBack);

    /**
     * Used to determined if we should attempt to reconnect to the machine, or if nothing is going on.
     * @param active true for communicating, false for no communication.
     */
    void setCommActive(boolean active);

    /**
     * This allows the user to scan for all of the different devices, when finished scanning it will
     * Call the listener to allow them to select with
     * @param listener listener to be called after scanning is complete.
     */
    void scanForSystems(ScanSystemListener listener);
}
