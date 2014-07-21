/**
 * Tests the Get Task Info Status
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Tests the constructor and the handling of the response message.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.CpuTask;
import com.ifit.sparky.fecp.interpreter.status.GetTaskInfoSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.tests.brute.interpreter.command.TestCommandBuilder;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestGetTaskInfoSts extends TestCase {

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testGetTaskInfoSts_Constructor() throws Exception{

        GetTaskInfoSts sts;
        sts = new GetTaskInfoSts(DeviceId.TREADMILL);
        CpuTask task;

        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, sts.getStsId());
        assertEquals(CommandId.GET_TASK_INFO, sts.getCmdId());
        assertEquals(28, sts.getLength());//min length
        task = sts.getTask();
        assertEquals(0,task.getBestTime());//just to make sure it was actual created
    }

    /** Tests the interpretation of the buffer
     *
     * @throws Exception
     */
    public void testGetTaskInfoSts_BufferReading() throws Exception{

        GetTaskInfoSts sts;
        ByteBuffer buff;
        CpuTask task;
        TestCommandBuilder builder = new TestCommandBuilder();

        sts = new GetTaskInfoSts(DeviceId.INCLINE_TRAINER);

        //initialize buffer command
        buff = builder.buildBuffer(sts.getDevId(), 28,sts.getCmdId(),StatusId.DONE);
        //index
        buff.put((byte)3);
        //interval
        buff.putShort((short) 800);
        //execute Flag
        buff.put((byte)0);
        //worse time
        buff.putInt(987654);
        //best Time
        buff.putInt(3210);
        //recent time
        buff.putInt(43210);
        //number of Calls
        buff.putInt(500);
        //number of misses
        buff.putShort((short) 52);
        //length of name
        buff.put((byte)0);
        //name
        //no name

        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);
        task = sts.getTask();

        // assert default values
        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(StatusId.DONE, sts.getStsId());
        assertEquals(CommandId.GET_TASK_INFO, sts.getCmdId());
        assertEquals(28, sts.getLength());
        assertEquals(800, task.getInterval());
        assertEquals(false, task.getExecutFlag());
        assertEquals(43210, task.getRecentTime());
        assertEquals(987654, task.getWorseTime());
        assertEquals(3210, task.getBestTime());
        assertEquals(500, task.getNumberOfCalls());
        assertEquals(52, task.getNumberOfMisses());
        assertEquals("", task.getTaskName());
    }
}
