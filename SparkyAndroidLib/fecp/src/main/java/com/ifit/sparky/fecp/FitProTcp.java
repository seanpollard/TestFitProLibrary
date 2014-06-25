/**
 * Creates a connection to the Fitpro system.
 * @author Levi.Balling
 * @date 5/16/2014
 * @version 1
 * sets up the communication to the FitPro system.
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.TcpComm;
import com.ifit.sparky.fecp.communication.TcpConnectionDevice;

import java.net.InetSocketAddress;

public class FitProTcp extends FecpController {

    private InetSocketAddress mIpAddress;
    public final int DEFAULT_PORT = 8090;
    public final int DEFAULT_TIME_OUT = 2000;

    /**
     * This sets up the FitPro for the wifi connection over TCP
     * @param ipAddress the ip address
     * @param port port to communicate over
     * @throws Exception
     */
    public FitProTcp(String ipAddress, int port) throws Exception {
        super(CommType.TCP);
        this.mIpAddress = new InetSocketAddress(ipAddress, port);
        this.mCommController = new TcpComm(this.mIpAddress, DEFAULT_TIME_OUT);
    }

    /**
     * This sets up the FitPro for the wifi connection over TCP Uses default Port of 8090
     * @param ipAddress the ip address
     * @throws Exception
     */
    public FitProTcp(String ipAddress) throws Exception {
        super(CommType.TCP);
        this.mIpAddress = new InetSocketAddress(ipAddress, DEFAULT_PORT);
        this.mCommController = new TcpComm(this.mIpAddress, DEFAULT_TIME_OUT);
    }

    /**
     * This sets up the FitPro for the wifi connection over TCP
     * @param ipAddress the ip address
     * @throws Exception
     */
    public FitProTcp(InetSocketAddress ipAddress) throws Exception {
        super(CommType.TCP);
        this.mIpAddress = ipAddress;
        this.mCommController = new TcpComm(this.mIpAddress, DEFAULT_TIME_OUT);
    }

    /**
     * This sets up the FitPro for the wifi connection over TCP
     * @param connectionDev the Socket and the Device to connect to
     * @throws Exception
     */
    public FitProTcp(TcpConnectionDevice connectionDev) throws Exception {
        super(CommType.TCP);
        this.mIpAddress = connectionDev.getIpAddress();
        this.mCommController = new TcpComm(connectionDev, DEFAULT_TIME_OUT);
    }

    /**
     * Gets the Port for the socket communication
     * @return which port is used
     */
    public int getPort() {
        return this.mIpAddress.getPort();
    }

    /**
     * Gets the ip address of the connection
     * @return ip address
     */
    public InetSocketAddress getIpAddress() {
        return mIpAddress;
    }

    /**
     * Sets the port for the connection, doesn't do anything after initializing the connection
     * @param port port for the connection
     */
    public void setPort(int port) {
        this.mIpAddress = new InetSocketAddress(this.mIpAddress.getAddress(), port);
    }

    /**
     * the ip address for the connection, doesn't do anything after initializing the connection.
     * @param ipAddress ip address of the FitPro
     */
    public void setIpAddress(String ipAddress) {
        this.mIpAddress = new InetSocketAddress(ipAddress, this.mIpAddress.getPort());
    }
    /**
     * the ip address for the connection, doesn't do anything after initializing the connection.
     * @param ipAddress ip address of the FitPro
     */
    public void setIpAddress(InetSocketAddress ipAddress) {
        this.mIpAddress = ipAddress;
    }

    /**
     * Scans for all of the available Devices
     * @param listener callback for when it finishes scanning, may contain no Devices
     */
    public static void scanForSystems(CommInterface.ScanSystemListener listener) {
        new TcpComm().scanForSystems(listener);

    }
}
