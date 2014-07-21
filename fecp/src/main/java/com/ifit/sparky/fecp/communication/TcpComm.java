/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 5/16/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.communication;

import android.util.Log;

import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.error.ErrorReporting;
import com.ifit.sparky.fecp.interpreter.command.GetSysInfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpComm implements CommInterface {

    private Socket mSocket;
    private TcpConnectionDevice mConnectionDevice;
    private final int BUFF_SIZE = 64;
    private BufferedOutputStream mToMachine;
    private InetSocketAddress mIpAddress;
    private int mSendTimeout;
    private ScanSystemListener mScanListener;//returns with list of devices or none
    private boolean enableLogging = false;
    private int mValidConnectionCount = 0;
    private final int VALID_CONNECTION_COUNT_LIMIT = 18;

    private CopyOnWriteArrayList<SystemStatusListener> mConnectionListeners;

    public TcpComm()
    {
        //don't need anything this is specifically for the Scanning
    }

    public TcpComm(InetSocketAddress ipAddress, int defaultTimeout)
    {
        this.mIpAddress = ipAddress;
        this.mSendTimeout = defaultTimeout;
        if(this.mConnectionListeners == null)
        {
            this.mConnectionListeners = new CopyOnWriteArrayList<SystemStatusListener>();
        }
    }

    public TcpComm(TcpConnectionDevice mDev, int defaultTimeout)
    {
        this.mIpAddress = mDev.getIpAddress();
        this.mSocket = mDev.getSocket();
        this.mConnectionDevice = mDev;

        this.mSendTimeout = defaultTimeout;
        if(this.mConnectionListeners == null)
        {
            this.mConnectionListeners = new CopyOnWriteArrayList<SystemStatusListener>();
        }
    }

    /**
     * Initializes the connection to the communication items.
     */
    @Override
    public SystemDevice initializeCommConnection() {
        //makes a connection across port
        try {
            if(this.mConnectionDevice == null) {
                if (this.mSocket == null) {
                    this.mSocket = new Socket();
                    this.mSocket.setSendBufferSize(1024);
                    this.mSocket.setReceiveBufferSize(1024);
                    this.mSocket.connect(this.mIpAddress, 10000);
                }
                //set the timeout for 5 seconds, if so abandon command
//                this.mSocket.setSoTimeout(5000);
                this.mSocket.setTcpNoDelay(true);
                this.mToMachine = new BufferedOutputStream(this.mSocket.getOutputStream());
                Log.d("TCP_CONNECTION", "Initial Connection was successful");
            }
            else
            {
               this.mSocket = this.mConnectionDevice.getSocket();
            }
            //this.mSocket = new Socket(this.mIpAddress, this.mPort);
            //then connection speed, then bandwidth is lowest.

            if(this.mSocket.isConnected())
            {
                // check for the system device
                return SystemDevice.initializeSystemDevice(this);//get the System Device
            }

            for (SystemStatusListener listener : this.mConnectionListeners) {
                listener.systemDisconnected();
//                listener.onDeviceConnected();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Handles multiple listeners so we can notify both ifit and the fecp controller.
     *
     * @param listener the listener for the callbacks
     */
    @Override
    public void addConnectionListener(SystemStatusListener listener) {
        this.mConnectionListeners.add(listener);
    }

    /**
     * Removes all the Connection listeners,
     */
    @Override
    public void clearConnectionListener() {
        this.mConnectionListeners.clear();
    }

    /**
     * gets the list of System Status Listeners
     *
     * @return list of all the System Status Listeners
     */
    @Override
    public List<SystemStatusListener> getSystemStatusListeners() {
        return this.mConnectionListeners;
    }

    /**
     * sends the command and waits for the reply to handle the buffer
     *
     * @param buff the command buffer to send
     * @return a buffer with the message 0 if failed
     */
    @Override
    public ByteBuffer sendAndReceiveCmd(ByteBuffer buff) {

        return this.sendAndReceiveCmd(buff, this.mSendTimeout);
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
        ByteBuffer resultBuffer = ByteBuffer.allocate(64);
        resultBuffer.order(ByteOrder.LITTLE_ENDIAN);

        //check if disconnected
        if(!this.mSocket.isConnected() || VALID_CONNECTION_COUNT_LIMIT < this.mValidConnectionCount)
        {
            //if no connection
            try {
                if(!this.mSocket.isClosed())
                {
                    this.mSocket.close();
                }
                this.mSocket = new Socket();
                this.mSocket.connect(this.mIpAddress, 5000);
                //attempt to reopen the socket completely
//                this.mSocket.setSoTimeout(5000);
                this.mSocket.setTcpNoDelay(true);
                this.mToMachine = new BufferedOutputStream(this.mSocket.getOutputStream());
                for (SystemStatusListener listener : this.mConnectionListeners) {
                    listener.systemCommunicationConnected();
                }
                Log.d("TCP_CONNECTION", "Attempting reconnection was successful");
            }
            catch (IOException ex)
            {

                Log.d("TCP_CONNECTION", "Socket was disconnected, reconnect Failed");
                for (SystemStatusListener listener : this.mConnectionListeners) {
                    listener.systemDisconnected();
                }
                return null;
            }
        }

        try {
            this.mSocket.setSoTimeout(timeout);

            //read all previous data before beginning
            while(this.mSocket.getInputStream().available() > 0)
            {
                this.mSocket.getInputStream().read();//discard the unknown data
            }
            byte[] data = new byte[2000];//shouldn't every get to many
            buff.position(0);
            //clear input before sending
            this.mToMachine.write(buff.array());
            this.mToMachine.flush();
            Thread.sleep(5);//wait for the data to get there before jumping forward

            //assume it worked
            int bytesRead =this.mSocket.getInputStream().read(data);//at least 64 or more
            this.mValidConnectionCount = 0;
            //read all of the data available
            buff.position(0);
            if(data[0] == (byte)0x03 && buff.get() == (byte)0x03)//custom handle for special objects.
            {
                //get all of the data into a Byte buffer except the first byte
                //max size 2K bytes
//                int bytesRead = this.mSocket.getInputStream().read(sysObjectData);
                resultBuffer = ByteBuffer.allocate(bytesRead -1);
                resultBuffer.order(ByteOrder.LITTLE_ENDIAN);
                resultBuffer.put(data, 1, bytesRead-1);//exclude the Dev Id
            }
            else {
                resultBuffer.put(data, 0, 64);
            }

            if(enableLogging) {
                String result = "raw Server data size=" + resultBuffer.capacity() + " actual size=" + resultBuffer.position() + " data=\n";
                int counter = 0;
                int length = data[1];

                for (byte b : data) {
                    if (counter < length) {
                        result += "[" + counter++ + "]=" + b + "\n";
                    }
                }
                Log.d("IN_DATA", result);
            }

            resultBuffer.position(0);
            return resultBuffer;
            //log data that is received
        } catch (Exception e) {
            Log.e("tcp_comm", "Message dropped");
//            e.printStackTrace();
            this.mValidConnectionCount++;
        }
        return null;
    }

    /**
     * Needs to report error with the err
     *
     * @param errReporterCallBack needs to be called to handle errors
     */
    @Override
    public void setupErrorReporting(ErrorReporting errReporterCallBack) {
        //currently not implemented
    }

    /**
     * Used to determined if we should attempt to reconnect to the machine, or if nothing is going on.
     *
     * @param active true for communicating, false for no communication.
     */
    @Override
    public void setCommActive(boolean active) {
        //currently has no impact on communication,
    }

    /**
     * This allows the user to scan for all of the different devices, when finished scanning it will
     * Call the listener to allow them to select with
     *
     * @param listener listener to be called after scanning is complete.
     */
    @Override
    public void scanForSystems(ScanSystemListener listener) {

        //scans all of the different Ip addresses for any valid ones, then scans the default port for any
        //this is a multi threaded opperation
        this.mScanListener = listener;
        Thread scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //gets all the ip address available in this network.
                InetAddress currentIpAddress = getCurrentIpAddress();

                if(currentIpAddress == null)
                {
                    mScanListener.onScanFinish(new ArrayList<ConnectionDevice>());//return an empty array list
                    return;
                }

                byte[] rawIpAddress = currentIpAddress.getAddress();
                //get starter string
                if(rawIpAddress.length < 3)
                {
                    return;
                }
                String maskedIpStr = ((int)rawIpAddress[0] & 0xff) +"." + ((int)rawIpAddress[1] & 0xff) +"." + ((int)rawIpAddress[2] & 0xff) +".";
                //generate a list ip address besides this ip address to check is valid ip address

                int excludeNum = ((int)rawIpAddress[3] & 0xff);

                ArrayList<IpScanner> ipScanners = new ArrayList<IpScanner>();
                ArrayList<Thread> scanThreads = new ArrayList<Thread>();

                //UNLEASE THE HOUNDS
                for(int i = 2; i < Byte.MAX_VALUE; i++)//0 and 1 are always gateways
                {
                    //create ip address string

                    if(i != excludeNum) {

                        try {
                            IpScanner scanner = new IpScanner(new InetSocketAddress(maskedIpStr + i, 8090));
                            ipScanners.add(scanner);
                            Thread runThread = new Thread(scanner);
                            runThread.start();
                            scanThreads.add(runThread);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                // CALL THE DOGS BACK, THEY HAVE GUNS
                try {
                    for (Thread thread : scanThreads) {
                        thread.join();

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mScanListener.onScanFinish(new ArrayList<ConnectionDevice>());//return an empty array list
                }

                //all the dogs are back and safe.
                //now we can send data to the valid addresses if they exist
                ArrayList<ConnectionDevice> possibleDevices = new ArrayList<ConnectionDevice>();
                for (IpScanner scanner : ipScanners) {
                    if(scanner.isValidDevice)
                    {
                        possibleDevices.add(scanner.mDev);
                    }
                }
                mScanListener.onScanFinish(possibleDevices);
            }
        });
        scanThread.start();
    }

    private InetAddress getCurrentIpAddress()
    {
        //gets all the ip address available in this network.
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        return addr;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("No IP address", ex.getMessage());
                ex.printStackTrace();
        }
        return null;
    }

    private class IpScanner implements Runnable{

        private TcpConnectionDevice mDev;
        private boolean isValidDevice = false;
        public IpScanner(InetSocketAddress ipAddress)
        {
            this.mDev = new TcpConnectionDevice(ipAddress);
            this.mDev.setSocket(new Socket());
        }
        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            //uses the given Ip address to check if it is available
            try {

                //todo try with no ip checking
                if(this.mDev.mIpAddress.getAddress().isReachable(10000))
                {
                    //try to see if port is available

                    try {

                        this.mDev.getSocket().connect(this.mDev.mIpAddress, 10000);//start off with a 5 second timeout
                        this.mDev.getSocket().setTcpNoDelay(true);//disable Nagle's algorithm
                        //send message to get the System Info
                        try {
                            GetSysInfoCmd tempCmd = new GetSysInfoCmd(DeviceId.MAIN);

                            this.mDev.setSendStream(new BufferedOutputStream(this.mDev.getSocket().getOutputStream()));
                            this.mDev.setReadStream(this.mDev.getSocket().getInputStream());
                            this.mDev.getSocket().setSoTimeout(10000);//just for this initial check

                            byte[] data;
                            byte[] readData;
                            ByteBuffer resultBuffer;
                            data = new byte[BUFF_SIZE];//shouldn't ever be longer
                            readData = new byte[BUFF_SIZE];//shouldn't ever be longer
                            int bytesRead = 0;
                            resultBuffer = ByteBuffer.allocate(BUFF_SIZE);

                            //get command to send
                            ByteBuffer buff = tempCmd.getCmdMsg();
                            buff.position(0);

                            //copy Data to a 64 byte array
                            buff.get(data,0, buff.capacity());//copy all of the elements available

                            //send the data
                            this.mDev.getSendStream().write(data);
                            this.mDev.getSendStream().flush();//send the data now

                            bytesRead = this.mDev.getSocket().getInputStream().read(readData);

                            resultBuffer = ByteBuffer.allocate(data.length);
                            resultBuffer.order(ByteOrder.LITTLE_ENDIAN);
                            resultBuffer.position(0);
                            resultBuffer.put(readData, 0, readData.length);

                            if(bytesRead != -1) {

                                GetSysInfoSts sysInfoSts =  (GetSysInfoSts)tempCmd.getStatus();//.handleStsMsg(resultBuffer);
                                sysInfoSts.handleStsMsg(resultBuffer);
                                if(sysInfoSts.getStsId() == StatusId.DONE)//valid option to connect to
                                {
                                    this.isValidDevice = true;
                                    this.mDev.setSysInfoVal(sysInfoSts);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        Log.d("Invalid Socket", "Failed to connect to ipaddress:"+ this.mDev.getIpAddress().getHostName() + ":"+this.mDev.getIpAddress().getPort());
                    }finally {
                        try {
                            if(this.mDev.getSocket() != null)// && !this.isValidDevice)
                            {
//                                this.mDev.getSocket().setSoLinger(true, 0);
                                this.mDev.getSendStream().close();
                                this.mDev.getSocket().close();
                            }

                        } catch (IOException closeEx) {
                            Log.e("failed to Close", closeEx.getMessage());
                            closeEx.printStackTrace();
                        }
                    }
                }
            } catch (Exception ex) {
                //expected exception
                Log.d("Invalid IpAddress", "Failed to connect to ipaddress:"+ this.mDev.getIpAddress().getHostName() + ":"+this.mDev.getIpAddress().getPort());
            }
        }
    }
}
