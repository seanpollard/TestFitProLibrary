/**
 * Tests the different command messages.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Handles the Get info command.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestCmdMsgs extends TestCase {

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
    public void testInfoCmdMsg_InfoCmd() throws Exception{

        //check all the different options for generating the buffer
        InfoCmd cmd;
        ByteBuffer buff;

        cmd = new InfoCmd();
        //check defaults
        assertEquals(4, cmd.getLength());
        assertEquals(CommandId.GET_INFO, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default

        //check that the buffer is what we would expect
        for(DeviceId id : DeviceId.values())
        {
            byte checkSum;
            cmd.setDevId(id);
            assertEquals(id, cmd.getDevId());//a little redundant
            buff = cmd.getCmdMsg();
            buff.position(0);
            assertEquals((byte)id.getVal(), buff.get());//check the device id
            assertEquals(4,buff.get());// check the length of the message
            assertEquals(CommandId.GET_INFO, CommandId.getCommandId((buff.get() & 0xFF)));

            //get the checkSum value
            checkSum = buff.get();
            assertEquals(checkSum, Command.getCheckSum(buff));
        }
    }

    /**
     * Tests the get command copy
     * @throws Exception in id issues
     */
    public void testInfoCmd_getCommandCopy() throws Exception{

        InfoCmd cmd;
        InfoCmd cmdCopy;
        cmd = new InfoCmd();

        assertEquals(DeviceId.NONE, cmd.getDevId());
        cmd.setDevId(DeviceId.INCLINE_TRAINER);
        cmdCopy = (InfoCmd)cmd.getCommandCopy();

        cmdCopy.setDevId(DeviceId.TREADMILL);

        assertEquals(DeviceId.INCLINE_TRAINER, cmd.getDevId());
        assertEquals(DeviceId.TREADMILL, cmdCopy.getDevId());//validate that they are different
    }


}
