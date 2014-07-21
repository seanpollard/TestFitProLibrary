/**
 * Handles the function in the status class.
 * @author Levi.Balling
 * @date 1/14/14
 * @version 1
 * Tests the reply from the device or the buffer that would be received.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;
import com.ifit.sparky.fecp.tests.brute.interpreter.command.TestCommandBuilder;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.util.TreeMap;

public class TestWriteReadDataSts  extends TestCase {

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
    public void testWriteReadDataSts_constructor() throws Exception{

        WriteReadDataSts sts;

        sts = new WriteReadDataSts(DeviceId.TREADMILL);
        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, sts.getStsId());
        assertEquals(CommandId.WRITE_READ_DATA, sts.getCmdId());
        assertEquals(5, sts.getLength());//min length
    }

    /** Tests the interpretation of the buffer
     *
     * @throws Exception
     */
    public void testReadDataSts_MessageHandling() throws Exception{

        WriteReadDataSts sts;
        ByteBuffer buff;
        TestCommandBuilder builder = new TestCommandBuilder();
        TreeMap<BitFieldId, BitfieldDataConverter> map;

        sts = new WriteReadDataSts(DeviceId.INCLINE_TRAINER);

        //message with no values in it
        buff = builder.buildBuffer(sts.getDevId(), 5,sts.getCmdId(),StatusId.DONE);

        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        // assert default values
        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(StatusId.DONE, sts.getStsId());
        assertEquals(CommandId.WRITE_READ_DATA, sts.getCmdId());
        assertEquals(5, sts.getLength());
        map = sts.getResultData();
        assertEquals(0, map.size());

        //add bitfield to the status for expecting a value in the reply
        sts.getBitFieldReadData().addBitfieldData(BitFieldId.KPH, 0);
        buff = builder.buildBuffer(sts.getDevId(), 7,sts.getCmdId(),StatusId.DONE);

        buff.putShort((short) 105);//10.5
        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(StatusId.DONE, sts.getStsId());
        assertEquals(CommandId.WRITE_READ_DATA, sts.getCmdId());
        assertEquals(7, sts.getLength());
        map = sts.getResultData();
        assertTrue(map.containsKey(BitFieldId.KPH));
        assertEquals(1, map.size());
        assertEquals(1.05, ((SpeedConverter)map.get(BitFieldId.KPH)).getSpeed());
    }
}
