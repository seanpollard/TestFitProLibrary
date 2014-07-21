/**
 * Tests the Get Supported commands command.
 * @author Levi.Balling
 * @date 1/16/14
 * @version 1
 * This will test the constructor and all the formatting of the message to send.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.GetTaskInfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestGetTaskInfoCmd extends TestCase {


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testGetTaskInfoCmd_Constructor() throws Exception{

        //check all the different options for generating the buffer
        GetTaskInfoCmd cmd;

        cmd = new GetTaskInfoCmd();
        //check default constructor
        assertEquals(5, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_TASK_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(0, cmd.getTaskIndex());

        //check second constructor
        cmd = new GetTaskInfoCmd(DeviceId.INCLINE_TRAINER);
        assertEquals(CommandId.GET_TASK_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
        assertEquals(0, cmd.getTaskIndex());

    }


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testGetTaskInfoCmd_CopyConstructor() throws Exception{

        //check all the different options for generating the buffer
        GetTaskInfoCmd cmd;
        GetTaskInfoCmd copyCmd;

        cmd = new GetTaskInfoCmd();
        //check default constructor
        assertEquals(5, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_TASK_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        copyCmd = (GetTaskInfoCmd)cmd.getCommandCopy();
        //check default constructor
        assertEquals(5, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_TASK_INFO, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default

        //set the original to be different
        cmd.setDevId(DeviceId.INCLINE_TRAINER);
        assertEquals(5, cmd.getLength());
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_TASK_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        //check to make sure the copy didn't change
        assertEquals(5, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_TASK_INFO, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default
    }

    /**
     * Test the Get command Message function.
     * @throws Exception
     */
    public void testGetTaskInfoCmd_getCmdMsg() throws Exception
    {
        GetTaskInfoCmd cmd;
        ByteBuffer buff;
        byte checkSum;

        cmd = new GetTaskInfoCmd(DeviceId.INCLINE_TRAINER);
        buff = cmd.getCmdMsg();
        buff.position(0);

        assertEquals((byte)DeviceId.INCLINE_TRAINER.getVal(), buff.get());//check the device id
        assertEquals(5,buff.get());// check the length of the message
        assertEquals(CommandId.GET_TASK_INFO, CommandId.getCommandId((buff.get() & 0xFF)));
        assertEquals(0,buff.get());// check if you want to read the MCU name
        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
        cmd.setTaskIndex(2);
        buff = cmd.getCmdMsg();
        buff.position(0);

        assertEquals((byte)DeviceId.INCLINE_TRAINER.getVal(), buff.get());//check the device id
        assertEquals(5,buff.get());// check the length of the message
        assertEquals(CommandId.GET_TASK_INFO, CommandId.getCommandId((buff.get() & 0xFF)));
        assertEquals(2,buff.get());// check which task to read
        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
    }


}
