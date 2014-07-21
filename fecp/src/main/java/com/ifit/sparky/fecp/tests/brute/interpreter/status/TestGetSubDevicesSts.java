/**
 * Tests the Status handling of the Get Sub Devices.
 * @author Levi.Balling
 * @date 1/16/14
 * @version 1
 * Tests the constructor, and the Message Handling of the command.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetSubDevicesSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.tests.brute.interpreter.command.TestCommandBuilder;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestGetSubDevicesSts extends TestCase {

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

        GetSubDevicesSts sts;
        sts = new GetSubDevicesSts(DeviceId.TREADMILL);

        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, sts.getStsId());
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, sts.getCmdId());
        assertEquals(6, sts.getLength());//min length
    }

    /** Tests the interpretation of the buffer
     *
     * @throws Exception
     */
    public void testGetCmdsSts_handleStsMsg() throws Exception{

        GetSubDevicesSts sts;
        ByteBuffer buff;
        TestCommandBuilder builder = new TestCommandBuilder();

        sts = new GetSubDevicesSts(DeviceId.INCLINE_TRAINER);

        //initialize empty reply buffer command
        buff = builder.buildBuffer(sts.getDevId(), 6,sts.getCmdId(),StatusId.DONE);
        buff.put((byte)0);
        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        //test the default values
        // assert default values
        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(6, sts.getLength());
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, sts.getCmdId());
        assertEquals(StatusId.DONE, sts.getStsId());


        //test message with some added commands
        sts = new GetSubDevicesSts(DeviceId.TREADMILL);

        //initialize buffer with 2 commands in it
        buff = builder.buildBuffer(sts.getDevId(), 8,sts.getCmdId(),StatusId.DONE);
        buff.put((byte)2);
        buff.put((byte)DeviceId.INCLINE_TRAINER.getVal());
        buff.put((byte)DeviceId.AUDIO.getVal());
        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        //test the default values
        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(8, sts.getLength());
        assertEquals(CommandId.GET_SUPPORTED_DEVICES, sts.getCmdId());
        assertEquals(StatusId.DONE, sts.getStsId());
        assertTrue(sts.getSubDevices().contains(DeviceId.INCLINE_TRAINER));
        assertTrue(sts.getSubDevices().contains(DeviceId.AUDIO));
    }
}
