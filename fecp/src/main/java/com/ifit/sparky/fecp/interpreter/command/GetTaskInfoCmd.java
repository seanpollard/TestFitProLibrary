/**
 * This is the command for Get Task Info.
 * @author Levi.Balling
 * @date 2/11/14
 * @version 1
 * creates the command for getting the Task info from the device.
 * It will only work for the Main device, It will allow you to get information about specific tasks.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.CpuTask;
import com.ifit.sparky.fecp.interpreter.status.GetTaskInfoSts;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class GetTaskInfoCmd extends Command implements CommandInterface, Serializable {

    private static final int CMD_LENGTH = 5;
    private int mTaskIndex;

    /**
     * default constructor
     */
    public GetTaskInfoCmd() throws Exception
    {
        super();
        this.setCmdId(CommandId.GET_TASK_INFO);
        this.setStatus(new GetTaskInfoSts(this.getDevId()));
        this.setLength(CMD_LENGTH);
        this.mTaskIndex = 0;
    }

    /**
     * default constructor
     */
    public GetTaskInfoCmd(DeviceId devId) throws Exception
    {
        super(new GetTaskInfoSts(devId),CMD_LENGTH,CommandId.GET_TASK_INFO,devId);
    }

    /**
     * Gets the Task Index
     * @return the Index of the Task
     */
    public int getTaskIndex() {
        return mTaskIndex;
    }

    /**
     * Sets the Task Index to get more info on
     * @param taskIndex the task index
     */
    public void setTaskIndex(int taskIndex) {
        this.mTaskIndex = taskIndex;
    }

    /**
     * Gets the command message for all commands that extend this class.
     * When they want to get the command they have to get the command.
     *
     * @return the Command structured to be ready to send over the usb.
     */
    @Override
    public ByteBuffer getCmdMsg() throws InvalidCommandException, InvalidBitFieldException {

        ByteBuffer buff;

        buff = super.getCmdMsg();
        buff.put((byte)this.mTaskIndex);
        //get the checksum value
        buff.put(Command.getCheckSum(buff));
        return buff;
    }

    /**
     * Gets a cloned copy of the command
     * @return the cloned copy of the command
     * @throws Exception if
     */
    @Override
    public Command getCommandCopy() throws Exception {
        CpuTask tempTask;
        Command tempCmd = new GetTaskInfoCmd(this.getDevId());
        ((GetTaskInfoCmd)tempCmd).setTaskIndex(this.mTaskIndex);
        tempTask = ((GetTaskInfoSts)this.getStatus()).getTask();
        ((GetTaskInfoSts)tempCmd.getStatus()).setTask(new CpuTask(tempTask));
        return tempCmd;
    }
}
