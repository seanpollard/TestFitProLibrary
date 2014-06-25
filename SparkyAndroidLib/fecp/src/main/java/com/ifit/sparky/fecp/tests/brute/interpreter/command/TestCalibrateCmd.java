/**
 * Tests the Update Command
 * @author Levi.Balling
 * @date 4/7/14
 * @version 1
 * This will test the constructor and all the formatting of the message to send.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.CalibrateCmd;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestCalibrateCmd extends TestCase {


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testCalibrate_Constructor() throws Exception{

        //check all the different options for generating the buffer
        CalibrateCmd cmd;

        cmd = new CalibrateCmd();
        //check default constructor
        assertEquals(5, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.CALIBRATE, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(0, cmd.getCalibrationType());
    }


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testCalibrateCmd_CopyConstructor() throws Exception{

        //check all the different options for generating the buffer
        CalibrateCmd cmd;
        CalibrateCmd copyCmd;

        cmd = new CalibrateCmd();
        //check default constructor
        assertEquals(5, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.CALIBRATE, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        copyCmd = (CalibrateCmd)cmd.getCommandCopy();
        //check default constructor
        assertEquals(5, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.CALIBRATE, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default

        //set the original to be different
        cmd.setDevId(DeviceId.INCLINE_TRAINER);
        assertEquals(5, cmd.getLength());
        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());//a little redundant
        assertEquals(CommandId.CALIBRATE, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        //check to make sure the copy didn't change
        assertEquals(5, copyCmd.getLength());
        assertEquals(DeviceId.NONE, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.CALIBRATE, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default
    }

    /**
     * Test the Get command Message function.
     * @throws Exception
     */
    public void testCalibrateCmd_getCmdMsg() throws Exception
    {

        //check all the different options for generating the buffer
        CalibrateCmd cmd;
        ByteBuffer buff;
        byte checkSum;
        cmd = new CalibrateCmd(DeviceId.INCLINE_TRAINER);
        buff = cmd.getCmdMsg();
        buff.position(0);

        assertEquals((byte)DeviceId.INCLINE_TRAINER.getVal(), buff.get());//check the device id
        assertEquals(5,buff.get());// check the length of the message
        assertEquals(CommandId.CALIBRATE, CommandId.getCommandId((buff.get() & 0xFF)));
        assertEquals(0,buff.get());// check if you want to read the MCU name
        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
        cmd.setCalibrationType(2);
        buff = cmd.getCmdMsg();
        buff.position(0);

        assertEquals((byte)DeviceId.INCLINE_TRAINER.getVal(), buff.get());//check the device id
        assertEquals(5,buff.get());// check the length of the message
        assertEquals(CommandId.CALIBRATE, CommandId.getCommandId((buff.get() & 0xFF)));
        assertEquals(2,buff.get());// check which task to read
        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
    }
}
