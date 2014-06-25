/**
 * Temp Testing callback object.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * This callback was made to validate the callback for the Command Callback.
 */
package com.ifit.sparky.fecp.tests.brute;

import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.SystemStatusListener;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;

public class TempFecpCallbacker implements OnCommandReceivedListener, SystemStatusListener {

    private boolean itWorks;
    private CommandId id;//temp id to make sure the command was sent
    private boolean isConnected;//just to test if connection works
    /**
     * simple constructor for the callback.
     */
    public TempFecpCallbacker()
    {
        this.itWorks = false;
        this.isConnected = false;
    }

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(Command cmd)
    {
        this.itWorks = (cmd.getCmdId() == this.id);
    }

    /**
     * sets the commandId to validate it is correct.
     * @param id The commandId to check
     */
    public void setCmdId(CommandId id)
    {
        this.id = id;
    }

    /**
     * gets the Test status, to make sure it passed
     * @return the boolean status
     */
    public boolean getWorksStatus()
    {
        return this.itWorks;
    }

    /**
     * Gets the connection status
     * @return the connection status
     */
    public boolean getIsConnectedStatus()
    {
        return this.isConnected;
    }

    /**
     * this method is called when the system is disconnected.
     */
    @Override
    public void systemDisconnected() {
        this.isConnected = false;
    }

    /**
     * This is called after system is connected
     *
     * @param dev the System device that is connected.
     */
    @Override
    public void systemDeviceConnected(SystemDevice dev) {

        this.isConnected = true;
    }

    /**
     * This will be called when the communication layer is connected. this is a lower level of
     * communication notification.
     */
    @Override
    public void systemCommunicationConnected() {
    }
}
