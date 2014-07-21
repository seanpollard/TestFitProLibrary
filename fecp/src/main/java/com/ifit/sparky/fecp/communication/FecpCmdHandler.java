/**
 * This is the super class for handling which commands to send and to receive.
 * @author Levi.balling
 * @date 1/20/14
 * @version 1
 * handles the common items as far as what to send and what to receive.
 */
package com.ifit.sparky.fecp.communication;

import android.util.Log;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InvalidCommandException;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;
import com.ifit.sparky.fecp.testingUtil.CmdInterceptor;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class FecpCmdHandler implements FecpCmdHandleInterface, Runnable{

    private CommInterface mCommController;
    private Vector<FecpCommand> mProcessCmds;
    private Vector<FecpCommand> mPeriodicCmds;//this will use the thread scheduler
    private ScheduledExecutorService mThreadManager = Executors.newSingleThreadScheduledExecutor();//this will keep track of all the threads
    private Thread mCurrentThread;//this thread will be recreated when needed.
    private int idAssigner;
    private CmdInterceptor mInterceptor;
    private SystemDevice mSysDev;
    private long mSucessfulSendReceiveTime;
    private long mFailedSendReceiveTime;
    private int mSuccessfulCmds;
    private int mFailedCmds;
    private int mAverageCmdPeriodic = Integer.MAX_VALUE;

    private CopyOnWriteArrayList<Long> mAverageResponseTime;//running sum of 10 samples
    private boolean mCommSpeedLogging = false;

    private final int COMM_THREAD_PRIORITY = -17;

    public FecpCmdHandler(CommInterface commController, SystemDevice sysDev)
    {
        this.mCommController = commController;
        this.idAssigner = 1;//start with 1
        this.mProcessCmds =new Vector<FecpCommand>();
        this.mPeriodicCmds = new Vector<FecpCommand>();
        this.mAverageResponseTime = new CopyOnWriteArrayList<Long>();
        this.mSysDev = sysDev;
        this.mSucessfulSendReceiveTime = 0;
        this.mFailedSendReceiveTime = 0;
        this.mSuccessfulCmds = 0;
        this.mFailedCmds = 0;
        this.mAverageCmdPeriodic = Integer.MAX_VALUE;
    }

    /**
     * Gets the comm controller used for the system.
     *
     * @return the comm controller
     */
    @Override
    public CommInterface getCommController() {
        return this.mCommController;
    }

    /**
     * Adds the command to the list to be sent
     *
     * @param cmd the command to be sent.
     */
    @Override
    public void addFecpCommand(FecpCommand cmd) throws InvalidCommandException, InvalidBitFieldException
    {
        if(cmd.getCmdIndexNum() != 0)
        {
            throw new InvalidCommandException("Command Already Added, can't add the same command Twice");
        }

        //check if it is a portal or Raw command
        if(cmd.getCommand().getDevId() != DeviceId.PORTAL && cmd.getCommand().getCmdId() != CommandId.RAW)
        {

            //check if the device is a valid device
            if(!CmdValidator.ValidateDevice(this.mSysDev, cmd.getCommand().getDevId())) {
                throw new InvalidCommandException("Invalid Device(" + cmd.getCommand().getDevId().name()+":"+cmd.getCommand().getDevId().getVal() + "), System doesn't support this Device");
            }

            //check if the Command is supported by that device
            if(!CmdValidator.ValidateCommand(this.mSysDev, cmd.getCommand().getDevId(), cmd.getCommand().getCmdId())) {
                throw new InvalidCommandException("Invalid Command for the device("
                        + cmd.getCommand().getDevId().name()
                        + ":" + cmd.getCommand().getDevId().getVal()+ "),That Device doesn't support "
                        +cmd.getCommand().getCmdId().name() + " command("
                        + cmd.getCommand().getCmdId().getVal()+")");
            }
            //check if it is a writeReadData command, and check if it is valid
            if(cmd.getCommand().getCmdId() == CommandId.WRITE_READ_DATA)
            {
                CmdValidator.ValidateBitfieldCmd(this.mSysDev, (WriteReadDataCmd)cmd.getCommand());//throws exception if invalid
            }
        }



        //check if thread is set
        cmd.setSendHandler(this);
        //check if the thread is running
        if(cmd.getFrequency() != 0)
        {
            cmd.setCmdIndexNum(this.idAssigner++);//unique
            this.mPeriodicCmds.add(cmd);
            if(this.idAssigner == Integer.MAX_VALUE) {
                this.idAssigner = 1;//roll over gracefully
            }
            cmd.setFutureScheduleTask(this.mThreadManager.scheduleAtFixedRate(cmd, 0, cmd.getFrequency(), TimeUnit.MILLISECONDS));
        }
        else
        {
            this.processFecpCommand(cmd);
        }
    }

    /**
     * Adds the command to the list to be sent
     *
     * @param cmd the command to be sent.
     * @param highPriority the command to be sent.
     */
//    public void addFecpCommand(FecpCommand cmd, boolean highPriority) throws Exception
//    {
//        if(cmd.getCmdIndexNum() != 0)
//        {
//            return;//already in the list. don't add
//        }
//
//        //check if thread is set
//        cmd.setSendHandler(this);
//        //check if the thread is running
//        if(cmd.getFrequency() != 0)
//        {
//            cmd.setCmdIndexNum(this.idAssigner++);//unique
//            this.mPeriodicCmds.add(cmd);
//            if(this.idAssigner == Integer.MAX_VALUE) {
//                this.idAssigner = 1;//roll over gracefully
//            }
//            cmd.setFutureScheduleTask(this.mThreadManager.scheduleAtFixedRate(cmd, 0, cmd.getFrequency(), TimeUnit.MILLISECONDS));
//        }
//        else
//        {
//            this.processFecpCommand(cmd);
//        }
//
//    }

    /**
     * Removes the command if it matches the Command id and the Device ID.
     * If there are multiples in the command list it will remove both of them.
     *
     * @param devId Device id to check if the command matches
     * @param cmdId the command to be removed
     * @return true if it removed the element
     */
    @Override
    public boolean removeFecpCommand(DeviceId devId, CommandId cmdId) {

        boolean result = false;
        for(FecpCommand cmd : this.mPeriodicCmds)
        {
            if(cmd.getCommand().getCmdId() == cmdId && cmd.getCommand().getDevId() == devId)
            {
                cmd.getFutureScheduleTask().cancel(false);//cancels
                this.mPeriodicCmds.remove(cmd);
                cmd.setCmdIndexNum(0);
                result = true;
            }
        }
        return result;
    }

    /**
     * Removes the command
     * @param cmd the fecpCommand to remove
     * @return true if it removed the element
     */
    @Override
    public boolean removeFecpCommand(FecpCommand cmd) {

        //check if the cmd has the same command id, device id, time, and if databitfield the same also

        for (FecpCommand mPeriodicCmd : this.mPeriodicCmds) {
            if(mPeriodicCmd.getCmdIndexNum() == cmd.getCmdIndexNum()) {

                this.mPeriodicCmds.remove(mPeriodicCmd);
                cmd.setCmdIndexNum(0);
                mPeriodicCmd.setCmdIndexNum(0);//set both just in case
                mPeriodicCmd.getFutureScheduleTask().cancel(false);//stop calling it
                return true;
            }
        }
        return false;
    }

    /**
     * Sends the command to the Fecp Communication Controller
     *
     * @param cmd the command to the Device
     */
    private void sendCommand(FecpCommand cmd) throws Exception {
        long startTime;
        long endTime;
        cmd.incrementCmdSentCounter();
        ByteBuffer tempBuffer = cmd.getCommand().getCmdMsg();
        //send the command and handle the response.
        if (cmd.getTimeout() == 0) {
            startTime = System.currentTimeMillis();
            tempBuffer = this.mCommController.sendAndReceiveCmd(tempBuffer);
            endTime = System.currentTimeMillis();
        } else {
            startTime = System.currentTimeMillis();
            tempBuffer = this.mCommController.sendAndReceiveCmd(tempBuffer, cmd.getTimeout());
            endTime = System.currentTimeMillis();
        }
        long resultTime = endTime - startTime;
        cmd.setCommSendReceiveTime(resultTime);
        if (this.mAverageCmdPeriodic > cmd.getFrequency() && cmd.getFrequency() != 0) {
            this.mAverageCmdPeriodic = cmd.getFrequency();
        }
        //check if there was an error with the send. if so return Failed
        if (tempBuffer == null || tempBuffer.get(0) == 0) {
            //message failed
            this.mFailedSendReceiveTime += resultTime;
            cmd.getCommand().getStatus().setStsId(StatusId.FAILED);
            this.mFailedCmds++;
            return;
        }
        this.mSucessfulSendReceiveTime += resultTime;
        this.mSuccessfulCmds++;
        if(this.mAverageResponseTime.size() > 25)
        {
            this.mAverageResponseTime.remove(0);
        }
        this.mAverageResponseTime.add(resultTime);

        if (mCommSpeedLogging)
        {
            Log.d("COMMUNICATION", "cmd time:" + resultTime + "mSec");
        }
        cmd.getCommand().getStatus().handleStsMsg(tempBuffer);
        cmd.incrementCmdReceivedCounter();
    }

    @Override
    public void addInterceptor(CmdInterceptor interceptor) {
        this.mInterceptor = interceptor;
    }

    /**
     * This is to return the status on the commands
     * average, total time sent, total time
     *
     * @return String of all the details
     */
    @Override
    public String getCmdHandlingStats() {
        String details;
        double successRate = (this.mSuccessfulCmds + 0.0) / ((this.mSuccessfulCmds + this.mFailedCmds + 0.0));
        DecimalFormat df = new DecimalFormat("##.##%");
        DecimalFormat msTime = new DecimalFormat("##.##mSec");
        double responseTime =  ((this.mSucessfulSendReceiveTime + this.mFailedSendReceiveTime)/(this.mFailedCmds + this.mSuccessfulCmds));

        DecimalFormat freqF = new DecimalFormat("###Hz");
        double queryFreq = this.mAverageCmdPeriodic;// /(this.mFailedCmds + this.mSuccessfulCmds);
        queryFreq /= 1000;//convert to 0.001 ms
        //convert to Hz
        queryFreq = 1 / queryFreq;
        double averageResponseRate = 0.0;
        for (Long sample : this.mAverageResponseTime) {
            averageResponseRate += sample;
        }
        averageResponseRate /= this.mAverageResponseTime.size();

        details = "Success Rate:" + df.format(successRate) + "\n" +
                "Successful Cmds: " + this.mSuccessfulCmds + "\n" +
                "Failed Cmds: " + this.mFailedCmds + "\n" +
                "Average Response Cmd Time " + msTime.format(averageResponseRate) + "\n" +
                "Fastest Query Cmd Frequency: " + freqF.format(queryFreq);//this.mAverageCmdPeriodic /(this.mFailedCmds + this.mSuccessfulCmds) + " mSec";

        return details;
    }

    /**
     * adds the command to the queue, in order to be ready to send.
     *
     * @param cmd the command to be sent.
     */
    @Override
    public void processFecpCommand(FecpCommand cmd) {
        //add to list of commands to send as soon as possible
        //check if already in the list
        if(!this.mProcessCmds.contains(cmd))
        {
            this.mProcessCmds.add(cmd);
        }

        if(this.mCurrentThread == null || !this.mCurrentThread.isAlive())
        {
            this.mCurrentThread = new Thread(this);
            this.mCurrentThread.start();
        }
    }


    /**
     * implements runnable
     */
    @Override
    public void run() {
        //go through the list of commands and make the function calls to them.
        //yes this is an infinite loop.
        try
        {
            //communications should be a high priority
            int threadId = android.os.Process.myTid();
            android.os.Process.setThreadPriority(COMM_THREAD_PRIORITY);
            while(this.mProcessCmds.size() > 0)
            {
                //set comm active
                this.mCommController.setCommActive(true);

                FecpCommand tempCmd = this.mProcessCmds.get(0);
                if(this.mInterceptor != null && this.mInterceptor.isInterceptorEnabled())
                {
                    this.mInterceptor.interceptFecpCommand(tempCmd);
                }
                else {
                    this.sendCommand(tempCmd);
                }
                //if there is a callback call it
                List<OnCommandReceivedListener> listeners;
                listeners = tempCmd.getOnCommandReceiveListeners();
                if(listeners.size() != 0
                        && (tempCmd.getCommand().getStatus().getStsId() == StatusId.DONE
                        || tempCmd.getCommand().getStatus().getStsId() == StatusId.FAILED || tempCmd.getCommand().getStatus().getStsId() == StatusId.IN_PROGRESS))
                {

                    for (OnCommandReceivedListener listener : listeners) {
                        if(listener != null) {
                            listener.onCommandReceived(tempCmd.getCommand());//needs to be able to handle pass failed or in progress
                        }
                    }
                    if(tempCmd.getCommand().getCmdId() == CommandId.WRITE_READ_DATA)
                    {
                        //update the data
                        this.mSysDev.updateCurrentData((WriteReadDataSts)tempCmd.getCommand().getStatus());
                    }
                }
                //remove from this it will add it later when it needs to.
                this.mProcessCmds.remove(0);
            }
            //set comm inactive
            this.mCommController.setCommActive(false);

        }
        catch (Exception ex)
        {
            if(ex.getMessage() == null){
                Log.e("thread error, no message, FecpCmdHandler run()", "");
                ex.printStackTrace();
            }else{
                Log.e("thread error", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
