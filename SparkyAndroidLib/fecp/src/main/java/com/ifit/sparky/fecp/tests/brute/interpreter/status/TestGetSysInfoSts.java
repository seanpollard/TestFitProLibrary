/**
 * Tests the Get System Info Status .
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Tests the constructor and the handling of the response message.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.tests.brute.interpreter.command.TestCommandBuilder;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestGetSysInfoSts extends TestCase {

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testGetSysInfoSts_Constructor() throws Exception{

        GetSysInfoSts sts;
        sts = new GetSysInfoSts(DeviceId.TREADMILL);

        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, sts.getStsId());
        assertEquals(CommandId.GET_SYSTEM_INFO, sts.getCmdId());
//        assertEquals(SystemConfiguration.SLAVE, sts.getConfig());
//        assertEquals(0, sts.getModel());
//        assertEquals(0, sts.getPartNumber());
//        assertEquals(0.0, sts.getCpuUse());
//        assertEquals(0, sts.getNumberOfTasks());
//        assertEquals(0, sts.getIntervalTime());
//        assertEquals(0, sts.getCpuFrequency());
//        assertEquals("", sts.getMcuName());
//        assertEquals("", sts.getConsoleName());
        assertEquals(24, sts.getLength());//min length
    }

    /** Tests the interpretation of the buffer
     *
     * @throws Exception
     */
    public void testGetSysInfoSts_BufferReading() throws Exception{

        GetSysInfoSts sts;
        ByteBuffer buff;
        TestCommandBuilder builder = new TestCommandBuilder();

        sts = new GetSysInfoSts(DeviceId.INCLINE_TRAINER);

        //initialize buffer command
        buff = builder.buildBuffer(sts.getDevId(), 24,sts.getCmdId(),StatusId.DONE);
        //SystemConfig
        buff.put((byte)1);//master
        //Model
        buff.putInt(54321);
        //partNum
        buff.putInt(12345);
        //Cpu use
        buff.putShort((short) 240);
        //num of Task
        buff.put((byte)10);
        //cpu min interval
        buff.putShort((short) 125);
        //cpu Freq
        buff.putInt(48000000);
        //mcu length
        buff.put((byte)0);
        //mcu name
        //no name
        //console length
        buff.put((byte)0);
        //console name
        //no name

        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        // assert default values
        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(StatusId.DONE, sts.getStsId());
        assertEquals(CommandId.GET_SYSTEM_INFO, sts.getCmdId());
        assertEquals(24, sts.getLength());

//        assertEquals(SystemConfiguration.MASTER, sts.getConfig());
//        assertEquals(54321, sts.getModel());
//        assertEquals(12345, sts.getPartNumber());
//        assertEquals(0.24, sts.getCpuUse());
//        assertEquals(10, sts.getNumberOfTasks());
//        assertEquals(125, sts.getIntervalTime());
//        assertEquals(48000000, sts.getCpuFrequency());
//        assertEquals("", sts.getMcuName());
//        assertEquals("", sts.getConsoleName());
    }
}
