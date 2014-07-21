/**
 * This is Master and Commander for communication to fitness equipment.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * This controller will handle all the different aspects of the communication to the system.
 */
package com.ifit.sparky.fecp.communication;

import android.os.Looper;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.error.ErrorCntrl;
import com.ifit.sparky.fecp.error.ErrorEventListener;
import com.ifit.sparky.fecp.error.ErrorReporting;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.VerifySecurityCmd;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.device.SystemConfiguration;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.status.VerifySecuritySts;
import com.ifit.sparky.fecp.testingUtil.CmdInterceptor;

import java.nio.ByteBuffer;
import java.util.List;

public class FecpController implements ErrorReporting {
    //Fecp System Version number
    private final int VERSION = 1;
    private CommType mCommType;
    protected SystemDevice mSysDev;
    protected boolean mIsConnected;
    protected boolean mIsControlUnlocked;
    protected CommInterface mCommController;
    protected FecpCmdHandleInterface mCmdHandleInterface;
    protected ErrorCntrl mSysErrorControl;
    private TcpServer mTcpServer;

    /**
     * This is for Fecp connections that don't req
     * @param type Communication type
     *  callback callback for the system
     * @throws Exception
     */
    public FecpController(CommType type) throws Exception {

        this.mCommType = type;
        this.mSysDev = new SystemDevice(DeviceId.MAIN);//starts out as main
        this.mIsConnected = false;
        this.mIsControlUnlocked = false;
        this.mSysErrorControl = new ErrorCntrl(this);
    }

    /**
     * Initializes the connection and sets up the communication
     *
     * @param listener this listens for changes in the connection
     */
    public void initializeConnection(SystemStatusListener listener) throws Exception {
        this.initializeConnection(listener, null);//just doesn't use it.
    }

    /**
     * Initializes the connection and sets up the communication
     *
     * @param listener this listens for changes in the connection
     * @param dataCallback a callback to get data about the server
     */
    public void initializeConnection(SystemStatusListener listener, final ServerDataCallback dataCallback) throws Exception {

         if(listener == null)
        {
            throw new Exception("SystemStatusListener callback is null, Can't be null");
        }

        this.mCommController.addConnectionListener(listener);
        //start a thread to initialize the connection

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mSysDev = mCommController.initializeCommConnection();//start initializing communications

                if(mSysDev == null ||mSysDev.getInfo().getDevId() == DeviceId.NONE)
                {
                    mIsConnected = false;
                    for (SystemStatusListener statusListener : mCommController.getSystemStatusListeners()) {
                        statusListener.systemDeviceConnected(mSysDev);
                    }
                    return;
                }
                mIsConnected = true;
                mCmdHandleInterface = new FecpCmdHandler(mCommController, mSysDev);

                if(mSysDev.getSysDevInfo().getConfig() == SystemConfiguration.MASTER || mSysDev.getSysDevInfo().getConfig() == SystemConfiguration.MULTI_MASTER ) {
                    //on port 8090.
                    mTcpServer = new TcpServer(mCmdHandleInterface, mSysDev, dataCallback);//currently accepting connections
                    mTcpServer.startServer();//start the server
                }

                mCommController.setupErrorReporting(mSysErrorControl);
                mCommController.setCommActive(false);
                for (SystemStatusListener statusListener : mCommController.getSystemStatusListeners()) {
                    statusListener.systemDeviceConnected(mSysDev);
                }

                Looper.myLooper().quit();
            }
        }).start();
    }

    /**
     * Gets the version of the Fecp Controller
     *
     * @return the version
     */
    public int getVersion() {
        return this.VERSION;
    }

    /**
     * Gets the Communication type
     *
     * @return the communication type
     */
    public CommType getCommType() {
        return this.mCommType;
    }

    /**
     * Gets the Main System Device, or the head of the tree
     *
     * @return the System Device
     */
    public SystemDevice getSysDev() {

        return this.mSysDev;
    }

    /**
     * Unlocks the system to allow users to send commands with write data.
     * @param systemKey Key to unlock the system, or send Write Read Data Commands
     * @throws Exception Invalid Key Structure
     */
    public void unlockSystem(ByteBuffer systemKey) throws Exception
    {
        //some quick checks with the key to make sure it will work
        if(systemKey.capacity() != 32)
        {
            throw new Exception("Invalid Security Key Length");
        }

        //check if the system is connected
        if(!this.mIsConnected)
        {
            throw new Exception("System Isn't connected");
        }

        //add the key to a command to unlock it.
        VerifySecurityCmd verifyCmd = (VerifySecurityCmd)this.mSysDev.getCommand(CommandId.VERIFY_SECURITY);
        systemKey.position(0);
        verifyCmd.setUnlockKey(systemKey);

        FecpCommand cmd = new FecpCommand(verifyCmd, new OnCommandReceivedListener() {
            @Override
            public void onCommandReceived(Command cmd) {
                if(((VerifySecuritySts)cmd.getStatus()).isUnlocked())
                {
                    for (SystemStatusListener listener : mCommController.getSystemStatusListeners()) {
                        listener.systemSecurityValidated();
                    }
                    mIsControlUnlocked = true;
                }
                else
                {
                    mIsControlUnlocked = false;
                }
            }
        });

        this.mCmdHandleInterface.addFecpCommand(cmd);//add the command for validation
    }


    /**
     * Gets the connection status
     *
     * @return the connection status true for connected
     */
    public boolean getIsConnected() {
        return this.mIsConnected;
    }

    /**
     * Gets the status whether the system is unlocked.
     * @return true if unlocked, false if locked
     */
    public boolean isControlUnlocked() {
        return mIsControlUnlocked;
    }

    /**
     * Adds a command to send to the device
     *
     * @param cmd the command to send to the device
     */
    public void addCmd(FecpCommand cmd) throws Exception {

        //smarter Command validity detection
        //this will compare it with the System device
        //check if system has been unlocked


        if(cmd.getCommand().getCmdId() == CommandId.WRITE_READ_DATA && !this.mIsControlUnlocked)
        {
            //check if they are trying to write to the system.
            if(((WriteReadDataCmd)cmd.getCommand()).getWriteBitData().getNumOfDataBytes() != 0) {
                //you can't send this command if you haven't unlocked it yet.
                throw new Exception("Sorry the system hasn't been unlocked yet");
            }
        }

        this.mCmdHandleInterface.addFecpCommand(cmd);
    }

    /**
     * Removes a command from the list to send
     *
     * @param cmd the command you wish to remove
     */
    public void removeCmd(FecpCommand cmd) {
        this.mCmdHandleInterface.removeFecpCommand(cmd);
    }

    /**
     * Removes all of the commands with the same device Id and Command Id
     *
     * @param devId the device you wish to remove the command from
     * @param cmdId the command from the device that you wish to remove.
     */
    public void removeCmd(DeviceId devId, CommandId cmdId) {
        this.mCmdHandleInterface.removeFecpCommand(devId, cmdId);
    }

    /**
     * Sends the buffer that matches the online profile for Error messages
     * Don't use if you don't now what it does
     * @param buffer buffer that is pointing to the start of the message.
     */
    @Override
    public void sendErrorObject(ByteBuffer buffer) {
        this.mSysErrorControl.sendErrorObject(buffer);
    }

    /**
     * Adds a listener to the system so we can determine if there are any errors
     *
     * @param errListener the listener that will be called when an error occurs
     */
    @Override
    public void addOnErrorEventListener(ErrorEventListener errListener) {
        this.mSysErrorControl.addOnErrorEventListener(errListener);
    }

    /**
     * Removes the listener from the system. so that it won't be called anymore
     *
     * @param errListener the listener that you wish to remove
     */
    @Override
    public void removeOnErrorEventListener(ErrorEventListener errListener) {
        this.mSysErrorControl.removeOnErrorEventListener(errListener);
    }

    /**
     * Clears the Listers from the system
     */
    @Override
    public void clearOnErrorEventListener() {
        this.mSysErrorControl.clearOnErrorEventListener();

    }
    /**
     * Gets the List of System Status connection listeners
     * @return list of System Status Connection listeners
     */
    public List<SystemStatusListener> clearConnectionListener() {
        return mCommController.getSystemStatusListeners();
    }

    public String getCommunicationStats()
    {
        if(this.mCmdHandleInterface != null)
        {
            return this.mCmdHandleInterface.getCmdHandlingStats();
        }
        return "";
    }

    /**
     * Adds an interceptor to the Fecp Controller, redirecting all commands to the CmdInterceptor.
     * This command is meant for testing Ifit code, not the fecp controller or the brain board.
     * @param interceptor interceptor to handle all commands going to the device.
     */
    public void addInterceptor(CmdInterceptor interceptor)
    {
        this.mCmdHandleInterface.addInterceptor(interceptor);
        //this will get the data from fecp controller that the interceptor needs
    }

    /**
     * This is a loophole for Testing Ifits code. It is apart of the interceptor process.
     * @param device The system that ifit will be communicating with.
     */
    public void testingSetSystemDevice(SystemDevice device)
    {
        this.mSysDev = device;
    }

}
