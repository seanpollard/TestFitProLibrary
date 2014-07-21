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
import com.ifit.sparky.fecp.interpreter.command.GetCmdsCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestGetCmdsCmd extends TestCase {

    /**
     * Setups the TestRunner for Status.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    /**
     * Closes the TestRunner for Status.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testGetCmdsCmd_Constructor() throws Exception{

        //check all the different options for generating the buffer
        GetCmdsCmd cmd;

        cmd = new GetCmdsCmd();
        //check default constructor
        assertEquals(4, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        //check second constructor
        cmd = new GetCmdsCmd(DeviceId.INCLINE_TRAINER);
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
    }


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testGetCmdsCmd_CopyConstructor() throws Exception{

        //check all the different options for generating the buffer
        GetCmdsCmd cmd;
        GetCmdsCmd copyCmd;

        cmd = new GetCmdsCmd();
        //check default constructor
        assertEquals(4, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        copyCmd = (GetCmdsCmd)cmd.getCommandCopy();
        //check default constructor
        assertEquals(4, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default

        //set the original to be different
        cmd.setDevId(DeviceId.INCLINE_TRAINER);
        assertEquals(4, cmd.getLength());
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        //check to make sure the copy didn't change
        assertEquals(4, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default
    }

    /**
     * Test the Get command Message function.
     * @throws Exception
     */
    public void testGetCmdsCmd_getCmdMsg() throws Exception
    {
        GetCmdsCmd cmd;
        ByteBuffer buff;
        byte checkSum;

        cmd = new GetCmdsCmd(DeviceId.INCLINE_TRAINER);
        buff = cmd.getCmdMsg();
        buff.position(0);
        assertEquals((byte)DeviceId.INCLINE_TRAINER.getVal(), buff.get());//check the device id
        assertEquals(4,buff.get());// check the length of the message
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, CommandId.getCommandId((buff.get() & 0xFF)));

        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
    }


}
