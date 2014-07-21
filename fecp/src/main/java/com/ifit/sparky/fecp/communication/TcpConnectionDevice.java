/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 5/29/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.communication;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpConnectionDevice extends ConnectionDevice {

    protected InetSocketAddress mIpAddress;
    protected Socket mSocket;//socket to connect to

    protected BufferedOutputStream sendStream;
    protected InputStream readStream;

    /**
     * Default constructor of the Tcp Connection Device
     */
    public TcpConnectionDevice()
    {
        super();
        this.mCommType = CommType.TCP;
        this.mIpAddress = new InetSocketAddress("192.168.1.1", 8090);
        this.mSocket = new Socket();
    }

    /**
     * Creates a Tcp Connection device with ipaddress and port number
     * @param ipAddress ip address
     * @param portNumber port number
     */
    public TcpConnectionDevice(String ipAddress, int portNumber)
    {
        super();
        this.mCommType = CommType.TCP;
        this.mIpAddress = new InetSocketAddress(ipAddress, portNumber);
//        this.mSocket = new Socket();
    }

    /**
     * Creates a Tcp Connection device with the InetSocketAddress
     * @param ipAddress InetSocketAddress object
     */
    public TcpConnectionDevice(InetSocketAddress ipAddress)
    {
        super();
        this.mCommType = CommType.TCP;
        this.mIpAddress = ipAddress;
//        this.mSocket = new Socket();
    }

    /**
     * Gets the Ipaddress object
     * @return Ip socket address
     */
    public InetSocketAddress getIpAddress() {
        return mIpAddress;
    }

    /**
     * Sets the Ip Socket Address
     * @param ipAddress the Ip Socket Address
     */
    public void setIpAddress(InetSocketAddress ipAddress) {
        this.mIpAddress = ipAddress;
    }

    /**
     * Gets the currently connected socket
     * @return Socket that may be connected
     */
    public Socket getSocket() {
        return mSocket;
    }

    /**
     * Sets the socket that we are interested in
     * @param socket socket that the device is connected to.
     */
    public void setSocket(Socket socket) {
        this.mSocket = socket;
    }

    public BufferedOutputStream getSendStream() {
        return sendStream;
    }

    public void setSendStream(BufferedOutputStream sendStream) {
        this.sendStream = sendStream;
    }

    public InputStream getReadStream() {
        return readStream;
    }

    public void setReadStream(InputStream readStream) {
        this.readStream = readStream;
    }
}
