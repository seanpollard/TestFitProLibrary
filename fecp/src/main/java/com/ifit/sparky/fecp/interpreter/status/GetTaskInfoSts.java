/**
 * This status handles the Get Task info.
 * @author Levi.Balling
 * @date 2/11/14
 * @version 1
 * This will handle the messages for getting information about specific tasks on the Main Device.
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class GetTaskInfoSts extends Status implements StatusInterface, Serializable {

    private static final int MIN_STS_LENGTH = 28;

    private CpuTask mTask;//slave,master, or multi master

    /**
     * Main constructor for the Info Status response
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public GetTaskInfoSts(DeviceId devId) throws Exception
    {
        super(StatusId.DEV_NOT_SUPPORTED, MIN_STS_LENGTH, CommandId.GET_TASK_INFO, devId);
        this.mTask = new CpuTask();
    }

    /**
     * Gets the Task from the response
     * @return CpuTask with stats on the task
     */
    public CpuTask getTask() {
        return mTask;
    }

    public void setTask(CpuTask task)
    {
        this.mTask = task;
    }
    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception
    {
        super.handleStsMsg(buff);

        //now parse the data
        if(this.getStsId() == StatusId.DONE)
        {
            this.mTask.handleTaskBuff(buff);
        }
    }

}
