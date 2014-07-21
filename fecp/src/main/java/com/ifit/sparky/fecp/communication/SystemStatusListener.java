/**
 * Interface callback for if the system connects or disconnects.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * If there is a disconnect the method will be called.
 */
package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.SystemDevice;

public interface SystemStatusListener {

    /**
     * this method is called when the system is disconnected. this is the same as the Communications disconnect
     */
    void systemDisconnected();

    /**
     * This is called after system is connected, and the communications is setup.
     * @param dev the System device that is connected. null if attempt failed
     */
    void systemDeviceConnected(SystemDevice dev);

    /**
     * This will be called when the communication layer is connected. this is a lower level of
     * communication notification.
     */
    void systemCommunicationConnected();

    /**
     * This will be called when the system has been validated
     */
    void systemSecurityValidated();

}
