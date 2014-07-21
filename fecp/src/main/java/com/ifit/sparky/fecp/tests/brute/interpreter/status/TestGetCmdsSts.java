/**
 * Tests the Status controller for the Get Supported Commands.
 * @author Levi.Balling
 * @date 1/16/14
 * @version 1
 * Tests the constructor and the values that come from the received buffer.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetCmdsSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.tests.brute.interpreter.command.TestCommandBuilder;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestGetCmdsSts extends TestCase {

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
    public void testGetCmdsSts_Constructor() throws Exception{

        GetCmdsSts sts;
        sts = new GetCmdsSts(DeviceId.TREADMILL);

        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, sts.getStsId());
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, sts.getCmdId());
        assertEquals(5, sts.getLength());//min length
    }

    /** Tests the interpretation of the buffer
     *
     * @throws Exception
     */
    public void testGetCmdsSts_handleStsMsg() throws Exception{

        GetCmdsSts sts;
        ByteBuffer buff;
        TestCommandBuilder builder = new TestCommandBuilder();

        sts = new GetCmdsSts(DeviceId.INCLINE_TRAINER);

        //initialize empty reply buffer command
        buff = builder.buildBuffer(sts.getDevId(), 5,sts.getCmdId(),StatusId.DONE);
        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        //test the default values
        // assert default values
        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(5, sts.getLength());
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, sts.getCmdId());
        assertEquals(StatusId.DONE, sts.getStsId());


        //test message with some added commands
        sts = new GetCmdsSts(DeviceId.TREADMILL);

        //initialize buffer with 2 commands in it
        buff = builder.buildBuffer(sts.getDevId(), 7,sts.getCmdId(),StatusId.DONE);
        buff.put((byte)CommandId.CONNECT.getVal());
        buff.put((byte)CommandId.DISCONNECT.getVal());
        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        //test the default values
        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(7, sts.getLength());
        assertEquals(CommandId.GET_SUPPORTED_COMMANDS, sts.getCmdId());
        assertEquals(StatusId.DONE, sts.getStsId());
        assertTrue(sts.getSupportedCommands().contains(CommandId.CONNECT));
        assertTrue(sts.getSupportedCommands().contains(CommandId.DISCONNECT));

    }
}
