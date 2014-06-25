/**
 * This will allow quick access to data directly from the device.
 * @author Levi.Balling
 * @date 5/23/2014
 * @version 1
 * This will handle data with as little footprint on both apps, and the communication interface.
 */
package com.ifit.sparky.fecp.communication;

import android.util.Log;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.interpreter.device.SystemConfiguration;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.RawDataCmd;
import com.ifit.sparky.fecp.interpreter.status.RawDataSts;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

//local class as to prevent any external control of starting it or stopping it.
class TcpServer implements SystemStatusListener {

    private ServerSocket mServerSock;
    private Thread mServerThread;
    private int mServerPort = 8090;//default
    private FecpCmdHandleInterface mCmdHandler;
    private SystemDevice mSysDev;
    private boolean mCommLogging = false;
    private final int COMM_THREAD_PRIORITY = -17;//major priority
    private ServerDataCallback mServerDataCallback;

    /**
     * Creates a Server with the System device to allob for
     * @param cmdHandler Handler for the commands. Sending and receiving.
     * @param sysDev System device for other devices to query information about the machine.
     */
    public TcpServer(FecpCmdHandleInterface cmdHandler, SystemDevice sysDev)
    {
        initializeServer(cmdHandler, sysDev, this.mServerPort, null);
    }

    /**
     * Creates a server with the different port number to broadcast on.
     * @param cmdHandler Handler for the commands. Sending and receiving.
     * @param sysDev System device for other devices to query information about the machine.
     * @param portNumber The new port for the Communication
     */
    public TcpServer(FecpCmdHandleInterface cmdHandler, SystemDevice sysDev, int portNumber)
    {
        initializeServer(cmdHandler, sysDev, portNumber, null);
    }

    /**
     * This creates a server that can handle
     * @param cmdHandler handles sending messages directly to the machine.
     * @param sysDev System device for other devices to query information about the machine.
     * @param callback feeds status information through the callback
     */
    public TcpServer(FecpCmdHandleInterface cmdHandler, SystemDevice sysDev, ServerDataCallback callback)
    {
        initializeServer(cmdHandler, sysDev, this.mServerPort, callback);
    }

    /**
     * This creates a server that can handle
     * @param cmdHandler handles sending messages directly to the machine.
     * @param sysDev System device for other devices to query information about the machine.
     * @param portNumber the port number for the systems
     * @param callback feeds status information through the callback
     */
    public TcpServer(FecpCmdHandleInterface cmdHandler, SystemDevice sysDev, int portNumber, ServerDataCallback callback) {
        initializeServer(cmdHandler, sysDev, portNumber, callback);
    }

    /**
     * initializes all of the items that are apart of the TCP server
     * @param cmdHandler the command handler for messages from clients
     * @param sysDev the system device to give to clients
     * @param portNumber the port number for the systems
     * @param callback callback to give data back to those whom implement
     */
    private void initializeServer(FecpCmdHandleInterface cmdHandler, SystemDevice sysDev, int portNumber, ServerDataCallback callback) {

        this.mCmdHandler = cmdHandler;
        this.mSysDev = sysDev;
        this.mServerPort = portNumber;
        this.mCmdHandler.getCommController().addConnectionListener(this);
        if(callback != null) {
            this.mServerDataCallback = callback;
        }

        this.mServerThread = new Thread(new ServerThread());
    }

    /**
     * Starts the TCP server Thread if it hasn't already started
     * @return true it started successfully, false if it is already running or
     */
    public boolean startServer()
    {
        if(this.mServerThread.isAlive())
        {
            return false;
        }
        this.mServerThread.start();

        return true;
    }

    /**
     * Stops the TCP server Thread
     * @return true if stopped successfully, false if failed to disconnect
     */
    public boolean stopServer()
    {
        if(this.mServerThread.isAlive())
        {
            try {
                this.mServerSock.close();//close the socket
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * this method is called when the system is disconnected. this is the same as the Communications disconnect
     */
    @Override
    public void systemDisconnected() {
        //stop the server thread
        try {
            this.mServerSock.close();//can't have a connection if there is no device
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is called after system is connected, and the communications is setup.
     *
     * @param dev the System device that is connected. null if attempt failed
     */
    @Override
    public void systemDeviceConnected(SystemDevice dev) {

        //don't start the server waiting for validation on the machine type
    }

    /**
     * This will be called when the communication layer is connected. this is a lower level of
     * communication notification.
     */
    @Override
    public void systemCommunicationConnected() {

        //don't start the server waiting for validation on the machine type
    }


    private class ServerThread implements Runnable{

        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            Socket socket = null;
            try {
                mServerSock = new ServerSocket(mServerPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = mServerSock.accept();
                    socket.setKeepAlive(false);
                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class CommunicationThread implements Runnable, OnCommandReceivedListener {


        private Socket clientSocket;
        private BufferedOutputStream mToClient;
        private int mSuccessfulMsg;
        private int mFailedMsg;
        private ArrayList<Long> mRunningSendTimeSum;//running sum of 10 samples
        private ArrayList<Long> mRunningReceiveTimeSum;//running sum of 10 samples
        private long mCurrentSendTime;
        private long mCurrentReceiveTime;

        private FecpCommand mRawFecpCmd;
        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;
            this.mRunningSendTimeSum = new ArrayList<Long>();
            this.mRunningReceiveTimeSum = new ArrayList<Long>();
            try {
                this.clientSocket.setSendBufferSize(1024);
                this.clientSocket.setReceiveBufferSize(1024);
                this.clientSocket.setTcpNoDelay(true);//disable Nagle's Algorithm
                this.mToClient = new BufferedOutputStream(this.clientSocket.getOutputStream());
                this.clientSocket.setSoTimeout(1000);//timeout after 5 secs

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {

            int runningCheck = 0;//if it has been to long close socket
            long startTime;//start to send is the read time, and from send to end is the send time
            long sendTime;
            long endTime;
            //increase the thread priority to for faster response
            int threadId = android.os.Process.myTid();
            Log.d("SERVER", "previous Thread(" + threadId+ ") Priority=" + android.os.Process.getThreadPriority(threadId));
            android.os.Process.setThreadPriority(COMM_THREAD_PRIORITY);
            Log.d("SERVER", "post Thread Priority=" + android.os.Process.getThreadPriority(threadId));

            while (!Thread.currentThread().isInterrupted())
            {

                startTime = System.currentTimeMillis();
                try {

                    if(runningCheck > 5)//5 seconds of timeout disconnect
                    {
                        //system is disconnected
                        this.clientSocket.close();
                        this.mToClient.close();
                        return;
                    }

                    byte[] data = new byte[64];
                    int readCount = this.clientSocket.getInputStream().read(data, 0, 64);
                    sendTime = System.currentTimeMillis();
                    if(readCount == -1)
                    {
                        runningCheck++;
                    }
                    else
                    {
                        runningCheck = 0;
                    }

                    if(mCommLogging) {
                        String result = "raw client " + this.clientSocket.getInetAddress().getHostAddress() + ":" + this.clientSocket.getPort() + "data=\n";
                        int counter = 0;
                        int length = data[1];

                        for (byte b : data) {
                            if (counter < length) {
                                result += "[" + counter++ + "]=" + b + "\n";
                            }
                        }
                        Log.d("IN_DATA", result);
                    }
                    this.handleRequest(data);

                    endTime = System.currentTimeMillis();
                    this.mSuccessfulMsg++;
                    if(mCommLogging) {
                        //log data that is received
                        Log.d("SERVER_SEND_TIME", "Server responseTime:" + (endTime - startTime) + "mSec");
                    }




                } catch (Exception e) {
                    sendTime = System.currentTimeMillis();
                    endTime = System.currentTimeMillis();
                    runningCheck++;
                    Log.d("NO_COMM", "Nothing to receive, Time was:" + (endTime - startTime) + "mSec" );
                    this.mFailedMsg++;
//                  e.printStackTrace();
                }

                //send stats
                if(mServerDataCallback != null)
                {
                    this.mCurrentSendTime = (endTime -sendTime);
                    this.mCurrentReceiveTime = (sendTime - startTime);
                    if(this.mRunningReceiveTimeSum.size() > 10)
                    {
                        this.mRunningReceiveTimeSum.remove(0);
                    }
                    if(this.mRunningSendTimeSum.size() > 10)
                    {
                        this.mRunningSendTimeSum.remove(0);
                    }
                    this.mRunningReceiveTimeSum.add(this.mCurrentReceiveTime);
                    this.mRunningSendTimeSum.add(this.mCurrentSendTime);

                    long sendTimeSum = 0;
                    long receiveTimeSum = 0;

                    for (Long sample : this.mRunningSendTimeSum) {
                        sendTimeSum += sample;
                    }
                    for (Long sample : this.mRunningReceiveTimeSum) {
                        receiveTimeSum += sample;
                    }

                    double sendAverageTime = (sendTimeSum + 0.0)/ this.mRunningSendTimeSum.size();
                    double receiveAverageTime = (receiveTimeSum + 0.0)/ this.mRunningReceiveTimeSum.size();

                    String ServerStatus;
                    ServerStatus = "Server Stats: "+ "IpAddress"+ this.clientSocket.getInetAddress().getHostAddress()+"\n"
                            + "total msgs:" + (this.mSuccessfulMsg + this.mFailedMsg) + "\n"
                            + "SuccessRate:" + (100*(this.mSuccessfulMsg / (this.mFailedMsg + this.mSuccessfulMsg + 0.0))) + "\n"
                            + "AverageSendTime: " + sendAverageTime + "mSec\n"
                            + "AverageReceiveTime: " + receiveAverageTime + "mSec\n";
                    mServerDataCallback.serverStats(ServerStatus);
                }



            }
        }

        private void handleRequest(byte[] buff)
        {

            byte[] data = buff;
            try {
                //created the command
                //check what the command is, and my current master configuration

                if(data[0] == (byte)0x02 && data[2] == (byte)0x82)//addressing the Main device for sys info
                {
                    //read the rest of the data
                    //int readCount = this.inFromClient.read(data, 3, 61);//read the rest of the data in the command
                    //return System Info command with appropriate system configuration
                    ByteBuffer reply = mSysDev.getSysInfoSts().getReplyBuffer();

                    reply.position(0);
//                    reply.put(0, (byte) 0x03);//portal device
                    if(mSysDev.getSysDevInfo().getConfig() == SystemConfiguration.SLAVE || mSysDev.getSysDevInfo().getConfig() == SystemConfiguration.PORTAL_TO_SLAVE) {
                        reply.put(4, (byte)SystemConfiguration.PORTAL_TO_SLAVE.ordinal());//portal device
                    }
                    else if(mSysDev.getSysDevInfo().getConfig() == SystemConfiguration.MASTER || mSysDev.getSysDevInfo().getConfig() == SystemConfiguration.MULTI_MASTER) {
                        reply.put(4, (byte)SystemConfiguration.PORTAL_TO_MASTER.ordinal());//portal device
                    }

                    byte length = reply.get(1);
                    reply.position(length-1);
                    reply.put(Command.getCheckSum(reply));
                    reply.position(0);
                    this.mToClient.write(reply.array());
                    this.mToClient.flush();
                    //they then use Listen command and single not repeat commands

                }
                else if (data[0] == (byte)0x03 && data[2] == (byte)0x01)//get System Device Command
                {
                    //reply with specific command
//                    int readCount = this.inFromClient.read(data, 3, 61);//read the rest of the data in the command
                   // int readCount = this.clientSocket.getInputStream().read(data, 3, 61);

                    this.mToClient.write(0x03);
                    mSysDev.writeObject(this.mToClient);
                    this.mToClient.flush();
                    //this.mToClient.write(dataObjectArray);//write object to client
                }
                else {

                    int readCount = 0;//read the rest of the data in the command
//                    try {
//                        //readCount = this.inFromClient.read(data, 3, 61);
//                        //readCount = this.clientSocket.getInputStream().read(data, 3, 61);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        ByteBuffer errBuff = ByteBuffer.allocate(64);
//                        errBuff.order(ByteOrder.LITTLE_ENDIAN);
//                        String errMessage = "Error with the message 0 data was send";
//                        errBuff.put(errMessage.getBytes());
//                        errBuff.position(0);
//                        this.mToClient.write(errBuff.array(), 0, 64);//error with message reply
//                        this.mToClient.flush();
//                    }
                    if(readCount == -1 || (data[0] == 0 && data[1] == 0))
                    {
                        //read to the end of the input stream
//                        while(this.inFromClient.available()>0)
//                        {
//                            this.inFromClient.read();
//
//
//                        }
                        ByteBuffer errBuff = ByteBuffer.allocate(64);
                        errBuff.order(ByteOrder.LITTLE_ENDIAN);
                        String errMessage = "Error with the message 0 data was send";
                        errBuff.put(errMessage.getBytes());
                        errBuff.position(0);
//                        this.mToClient.write(errBuff.array(), 0, 64);//error with message reply
//                        this.mToClient.flush();
                        return;
                    }
                    this.mRawFecpCmd = new FecpCommand(new RawDataCmd(ByteBuffer.wrap(data)), this);

                    //set to be a higher priority
                    //check if it is a master command
                    //send to FecpCmdHandler
                    mCmdHandler.addFecpCommand(this.mRawFecpCmd);
                }

                //clear everything from in buffer
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Handles the reply from the device
         *
         * @param cmd the command that was sent.
         */
        @Override
        public void onCommandReceived(Command cmd) {

            ByteBuffer buffer = ((RawDataSts)cmd.getStatus()).getRawBuffer();
            if(buffer == null)
            {
                return;//nothing to send invalid data
            }
            buffer.position(0);
            try {
                this.mToClient.write(buffer.array());//write the reply back to the server
                this.mToClient.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}
