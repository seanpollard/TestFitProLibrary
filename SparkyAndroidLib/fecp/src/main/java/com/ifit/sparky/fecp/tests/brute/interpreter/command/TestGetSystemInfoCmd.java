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
import com.ifit.sparky.fecp.interpreter.command.GetSysInfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestGetSystemInfoCmd extends TestCase {


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testGetSystemInfoCmd_Constructor() throws Exception{

        //check all the different options for generating the buffer
        GetSysInfoCmd cmd;

        cmd = new GetSysInfoCmd();
        //check default constructor
        assertEquals(6, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SYSTEM_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(false, cmd.getMcuName());
        assertEquals(false, cmd.getConsoleName());

        //check second constructor
        cmd = new GetSysInfoCmd(DeviceId.INCLINE_TRAINER);
        assertEquals(CommandId.GET_SYSTEM_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
        assertEquals(false, cmd.getMcuName());
        assertEquals(false, cmd.getConsoleName());

    }


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testGetSystemInfoCmd_CopyConstructor() throws Exception{

        //check all the different options for generating the buffer
        GetSysInfoCmd cmd;
        GetSysInfoCmd copyCmd;

        cmd = new GetSysInfoCmd();
        //check default constructor
        assertEquals(6, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SYSTEM_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        copyCmd = (GetSysInfoCmd)cmd.getCommandCopy();
        //check default constructor
        assertEquals(6, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SYSTEM_INFO, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default

        //set the original to be different
        cmd.setDevId(DeviceId.INCLINE_TRAINER);
        assertEquals(6, cmd.getLength());
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SYSTEM_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        //check to make sure the copy didn't change
        assertEquals(6, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.GET_SYSTEM_INFO, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default
    }

    /**
     * Test the Get command Message function.
     * @throws Exception
     */
    public void testGetSystemInfoCmd_getCmdMsg() throws Exception
    {
        GetSysInfoCmd cmd;
        ByteBuffer buff;
        byte checkSum;

        cmd = new GetSysInfoCmd(DeviceId.INCLINE_TRAINER);
        buff = cmd.getCmdMsg();
        buff.position(0);

        assertEquals((byte)DeviceId.INCLINE_TRAINER.getVal(), buff.get());//check the device id
        assertEquals(6,buff.get());// check the length of the message
        assertEquals(CommandId.GET_SYSTEM_INFO, CommandId.getCommandId((buff.get() & 0xFF)));
        assertEquals(0,buff.get());// check if you want to read the MCU name
        assertEquals(0,buff.get());// check if you want to read the Console name
        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
        cmd.setGetMcuName(true);
        cmd.setGetConsoleName(true);
        buff = cmd.getCmdMsg();
        buff.position(0);

        assertEquals((byte)DeviceId.INCLINE_TRAINER.getVal(), buff.get());//check the device id
        assertEquals(6,buff.get());// check the length of the message
        assertEquals(CommandId.GET_SYSTEM_INFO, CommandId.getCommandId((buff.get() & 0xFF)));
        assertEquals(1,buff.get());// check if you want to read the MCU name
        assertEquals(1,buff.get());// check if you want to read the Console name
        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
    }


}
