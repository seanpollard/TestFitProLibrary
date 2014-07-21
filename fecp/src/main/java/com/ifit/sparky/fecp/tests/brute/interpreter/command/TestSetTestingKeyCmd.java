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
import com.ifit.sparky.fecp.interpreter.command.SetTestingKeyCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.key.KeyCodes;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestSetTestingKeyCmd extends TestCase {


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testSetTestingKeyCmd_Constructor() throws Exception{

        //check all the different options for generating the buffer
        SetTestingKeyCmd cmd;

        cmd = new SetTestingKeyCmd();
        //check default constructor
        assertEquals(10, cmd.getLength());
        assertEquals(DeviceId.KEY_PRESS, cmd.getDevId());//a little redundant
        assertEquals(CommandId.SET_TESTING_KEY, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(false, cmd.isKeyOverride());
        assertEquals(false, cmd.isIsSingleClick());
        assertEquals(KeyCodes.NO_KEY, cmd.getKeyCode());
        assertEquals(0, cmd.getTimeHeld());

        //check second constructor
        cmd = new SetTestingKeyCmd(DeviceId.KEY_PRESS);
        assertEquals(10, cmd.getLength());
        assertEquals(DeviceId.KEY_PRESS, cmd.getDevId());//a little redundant
        assertEquals(CommandId.SET_TESTING_KEY, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(false, cmd.isKeyOverride());
        assertEquals(false, cmd.isIsSingleClick());
        assertEquals(KeyCodes.NO_KEY, cmd.getKeyCode());
        assertEquals(0, cmd.getTimeHeld());

    }


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testSetTestingKeyCmd_CopyConstructor() throws Exception{

        //check all the different options for generating the buffer
        SetTestingKeyCmd cmd;
        SetTestingKeyCmd copyCmd;

        cmd = new SetTestingKeyCmd();
        //check default constructor
        assertEquals(10, cmd.getLength());
        assertEquals(DeviceId.KEY_PRESS, cmd.getDevId());//a little redundant
        assertEquals(CommandId.SET_TESTING_KEY, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(false, cmd.isKeyOverride());

        copyCmd = (SetTestingKeyCmd)cmd.getCommandCopy();
        //check default constructor
        assertEquals(10, copyCmd.getLength());
        assertEquals(DeviceId.KEY_PRESS, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.SET_TESTING_KEY, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default
        assertEquals(false, cmd.isKeyOverride());


        //set the original to be different
        cmd.setKeyOverride(true);
        assertEquals(10, cmd.getLength());
        assertEquals(DeviceId.KEY_PRESS, cmd.getDevId());//a little redundant
        assertEquals(CommandId.SET_TESTING_KEY, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(true, cmd.isKeyOverride());

        //check to make sure the copy didn't change
        assertEquals(10, copyCmd.getLength());
        assertEquals(DeviceId.KEY_PRESS, copyCmd.getDevId());//a little redundant
        assertEquals(CommandId.SET_TESTING_KEY, copyCmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, copyCmd.getStatus().getStsId());//default
        assertEquals(false, copyCmd.isKeyOverride());
    }

    /**
     * Test the Get command Message function.
     * @throws Exception
     */
    public void testSetTestingKeyCmd_getCmdMsg() throws Exception
    {
        SetTestingKeyCmd cmd;
        ByteBuffer buff;
        byte checkSum;

        cmd = new SetTestingKeyCmd();
        buff = cmd.getCmdMsg();
        buff.position(0);

        assertEquals((byte)DeviceId.KEY_PRESS.getVal(), buff.get());//check the device id
        assertEquals(10,buff.get());// check the length of the message
        assertEquals(CommandId.SET_TESTING_KEY, CommandId.getCommandId((buff.get() & 0xFF)));
        assertEquals(0, buff.get());//Key enabled
        assertEquals(KeyCodes.NO_KEY, (KeyCodes.getKeyCode(buff.getShort())));//Cooked Key
        assertEquals(0, buff.getShort());//Time Held
        assertEquals(0, buff.get());//Single Click Only
        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));

        cmd.setKeyOverride(true);
        cmd.setIsSingleClick(true);
        cmd.setTimeHeld(123);
        cmd.setKeyCode(KeyCodes.START);
        buff = cmd.getCmdMsg();
        buff.position(0);

        assertEquals((byte)DeviceId.KEY_PRESS.getVal(), buff.get());//check the device id
        assertEquals(10,buff.get());// check the length of the message
        assertEquals(CommandId.SET_TESTING_KEY, CommandId.getCommandId((buff.get() & 0xFF)));
        assertEquals(1, buff.get());//Key enabled
        assertEquals(KeyCodes.START, (KeyCodes.getKeyCode(buff.getShort())));//Cooked Key
        assertEquals(123, buff.getShort());//Time Held
        assertEquals(1, buff.get());//Single Click Only

        //get the checkSum value
        checkSum = buff.get();
        assertEquals(checkSum, Command.getCheckSum(buff));
    }


}
