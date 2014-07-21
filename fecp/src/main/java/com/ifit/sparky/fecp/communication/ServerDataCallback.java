/**
 * Interface to the server to get feedback about the status of the server.
 * @author Levi.Balling
 * @date 6/10/2014
 * @version 1
 * This will allow for easier server debug. Since the server is the most import portion of the communication.
 */
package com.ifit.sparky.fecp.communication;

public interface ServerDataCallback {

    /**
     * This will be called after every server message.
     * This will allaw for easier debug
     * @param stats status about the Server
     */
    void serverStats(String stats);

}
