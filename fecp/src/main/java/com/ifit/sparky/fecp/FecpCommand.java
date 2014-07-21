/**
 * A command to issue to the System.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * This will hold all the values of the fecp command, that are needed to send and receive data.
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.communication.FecpCmdHandleInterface;
import com.ifit.sparky.fecp.interpreter.command.Command;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

public class FecpCommand extends Thread{

    //cmd and device
    private Command mCommand;//this holds the command and the status.
    private List<OnCommandReceivedListener> mOnCommandReceiveListeners;
    private int mTimeout;
    private int mFrequency;//time in between each call.
    private int mCmdSentCounter;
    private int mCmdReceivedCounter;
    private int mCmdIndexNum;//this is used by the command handler when it is added, and removed from the system.
    private long mCommSendReceiveTime;
    private FecpCmdHandleInterface mSendHandler;
    private ScheduledFuture<?> mFutureScheduleTask;

    /**
     * default constructor
     */
    public FecpCommand() throws Exception
    {
        this.fecpInitializer(null, null, 0,0);
    }

    /**
     * constructor for just a command, it will only be called once with no callback
     * @param cmd the command to send
     * @throws Exception
     */
    public FecpCommand(Command cmd) throws Exception
    {
        this.fecpInitializer(cmd, null, 0,0);
    }

    /**
     * Constructor for a simple command.
     * @param cmd the command for the device.
     * @param callback the callback after it is done.
     */
    public FecpCommand(Command cmd, OnCommandReceivedListener callback) throws Exception
    {
        this.fecpInitializer(cmd, callback, 0,0);
    }

    /**
     * Constructs a command with a timeout
     * @param cmd the command for the device.
     * @param callback the callback after it is done.
     * @param timeout the length of time it should receive a response.
     */
    public FecpCommand(Command cmd, OnCommandReceivedListener callback, int timeout) throws Exception
    {
        this.fecpInitializer(cmd, callback, timeout,0);
    }
    /**
     * Constructs a command with a timeout
     * @param cmd the command for the device.
     * @param callback the callback after it is done.
     * @param timeout the length of time it should receive a response.
     * @param frequency How frequent messages should be sent.
     */
    public FecpCommand(Command cmd, OnCommandReceivedListener callback, int timeout, int frequency) throws Exception
    {
        this.fecpInitializer(cmd, callback, timeout, frequency);
    }

    /**
     * Initializes all the values
     * @param cmd the command for the device.
     * @param callback the callback after it is done.
     * @param timeout the length of time it should receive a response.
     * @param frequency How frequent messages should be sent.
     */
    private void fecpInitializer(Command cmd,
                                 OnCommandReceivedListener callback,
                                 int timeout,
                                 int frequency) throws Exception
    {
        if(cmd != null)
        {
            this.mCommand = cmd.getCommandCopy();
        }
        this.mOnCommandReceiveListeners = new CopyOnWriteArrayList<OnCommandReceivedListener>();
        if(callback != null) {
            this.addOnCommandReceived(callback);
        }
        this.mTimeout = timeout;
        this.mFrequency = frequency;
        this.mCmdSentCounter = 0;
        this.mCmdReceivedCounter = 0;
        this.mCommSendReceiveTime = 0;
    }

    /*************
     *  GETTERS
     ************/

    /**
     * Gets the Command to be sent
     * @return the command
     */
    public Command getCommand()
    {
        return this.mCommand;
    }

    /**
     * Gets a list of the On Command Received Listeners
     * @return ArrayList of On Command Receive listeners
     */
    public List<OnCommandReceivedListener> getOnCommandReceiveListeners()
    {
        return this.mOnCommandReceiveListeners;
    }

    /**
     * Gets the timeout of the command
     * @return the timeout
     */
    public int getTimeout()
    {
        return this.mTimeout;
    }

    /**
     * Gets the frequency of the command
     * @return the frequency
     */
    public int getFrequency() {
        return this.mFrequency;
    }

    /**
     * Gets the number of times the command was sent.
     * @return the number of sends
     */
    public int getCmdSentCounter() {
        return this.mCmdSentCounter;
    }

    /**
     * gets the number of responds to this message
     * @return the receive count
     */
    public int getCmdReceivedCounter() {
        return this.mCmdReceivedCounter;
    }

    public long getCommSendReceiveTime()
    {
        return this.mCommSendReceiveTime;
    }

    public ScheduledFuture<?> getFutureScheduleTask() {
        return mFutureScheduleTask;
    }

    /**
     * Gets the id number of the Command in the Command handler
     * @return 0 if it isn't in the command handler
     */
    public int getCmdIndexNum() {
        return mCmdIndexNum;
    }

    /*************
     *  SETTERS
     ************/

    /**
     * Sets the command for the send
     * @param mCommand the command
     */
    public void setCommand(Command mCommand) throws Exception{
        this.mCommand = mCommand.getCommandCopy();
    }

    /**
     * Adds an OnReceived Listener to the Command. This is to share the same command results across multiple
     * objects
     * @param cmdReceivedListener the listener
     */
    public void addOnCommandReceived(OnCommandReceivedListener  cmdReceivedListener)
    {
        if(this.mOnCommandReceiveListeners.contains(cmdReceivedListener))
        {
            return;//already added, don't need duplicates
        }
        this.mOnCommandReceiveListeners.add(cmdReceivedListener);
    }

    /**
     * Removes the command from the List of listeners
     * @param cmdReceivedListener command listener to be removed
     * @return true if the item was in the list, false if nothing to be removed
     */
    public boolean removeOnCommandReceived(OnCommandReceivedListener  cmdReceivedListener)
    {
        return  this.mOnCommandReceiveListeners.remove(cmdReceivedListener);
    }


    /**
     * Sets the timeout for the command
     * @param mTimeout the timeout
     */
    public void setTimeout(int mTimeout) {
        this.mTimeout = mTimeout;
    }

    /**
     * sets the Frequency of the command sends
     * @param mFrequency the frequency of sends
     */
    public void setFrequency(int mFrequency) {
        this.mFrequency = mFrequency;
    }

    /**
     * sets the number of times the command was sent.
     * @param mCmdSentCounter the sent counter
     */
    public void setCmdSentCounter(int mCmdSentCounter) {
        this.mCmdSentCounter = mCmdSentCounter;
    }

    /**
     * Sets the number of times the command received a response
     * @param mCmdReceivedCounter the receive counter
     */
    public void setCmdReceivedCounter(int mCmdReceivedCounter) {
        this.mCmdReceivedCounter = mCmdReceivedCounter;
    }

    /**
     * increments the number of times the command was sent.
     */
    public void incrementCmdSentCounter() {
        this.mCmdSentCounter++;
    }

    /**
     * increments the number of times the command was received.
     */
    public void incrementCmdReceivedCounter() {
        this.mCmdReceivedCounter++;
    }

    /**
     * Approximate time in nano seconds for how long it took to send and receive the command
     * @param millisecondTime the time in nano seconds
     */
    public void setCommSendReceiveTime(long millisecondTime)
    {
        this.mCommSendReceiveTime = millisecondTime;
    }

    public void setSendHandler(FecpCmdHandleInterface sendHandler)
    {
        this.mSendHandler = sendHandler;
    }
    public void setFutureScheduleTask(ScheduledFuture<?> scheduledFut)
    {
        this.mFutureScheduleTask = scheduledFut;
    }

    /**
     * Gets the id of the command in the command handler 0 means it isn't apart of it
     * @param cmdIndexNum id of the command
     */
    public void setCmdIndexNum(int cmdIndexNum) {
        this.mCmdIndexNum = cmdIndexNum;
    }

    @Override
    public void run() {
        super.run();
        //this will add the command to the queue
        this.mSendHandler.processFecpCommand(this);

    }
}
