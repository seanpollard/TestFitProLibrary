/**
 * Tests the Command Get Sub Devices.
 * @author Levi.Balling
 * @date 1/16/14
 * @version 1
 * Tests the constructor and the message formatting of the Command.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.GetSubDevicesCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class testGetSubDevicesCmd extends TestCase {

    /**
     * Setups the TestRunner for Command.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    /**
     * Closes the TestRunner for Command.
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
        GetSubDevicesCmd cmd;

        cmd = new GetSubDevicesCmd();
        //check default constructor
        assertEquals(4, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        //check second constructor
        cmd = new GetSubDevicesCmd(DeviceId.INCLINE_TRAINER);
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
    }

    /** Tests the Copy Constructors.
     *
     * @throws Exception
     */
    public void testGetCmdsCmd_CopyConstructor() throws Exception{

        //check all the different options for generating the buffer
        GetSubDevicesCmd cmd;
        GetSubDevicesCmd copyCmd;

        cmd = new GetSubDevicesCmd();
        //check default constructor
        assertEquals(4, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        copyCmd = (GetSubDevicesCmd)cmd.getCommandCopy();
        //check default constructor
        assertEquals(4, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default

        //set the original to be different
        cmd.setDevId(DeviceId.INCLINE_TRAINER);
        assertEquals(4, cmd.getLength());
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        //check to make sure the copy didn't change
        assertEquals(4, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default
    }

    /**
     * Test the Get Sub Devices Message function.
     * @throws Exception
     */
    public void testGetCmdsCmd_getCmdMsg() throws Exception
    {
        GetSubDevicesCmd cmd;
        ByteBuffer buff;
        byte checkSum;

        cmd = new GetSubDevicesCmd(DeviceId.INCLINE_TRAINER);
        buff = cmd.getCmdMsg();
        buff.position(0);
        assertEquals((byte)DeviceId.INCLINE_TRAINER.getVal(), buff.get());//check the device id
        assertEquals(4,buff.get());// check the length of the message
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, CommandId.getCommandId((buff.get() & 0xFF)));

        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
    }

}
