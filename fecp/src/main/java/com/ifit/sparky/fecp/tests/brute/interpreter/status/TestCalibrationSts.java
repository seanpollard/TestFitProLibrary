/**
 * Validates the Calibration for the Status.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * This will validate the response of the Calibration reply.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.CalibrateSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.tests.brute.interpreter.command.TestCommandBuilder;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class TestCalibrationSts extends TestCase {

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testConstructor_calibrationStatus() throws Exception{

        CalibrateSts sts;


        sts = new CalibrateSts(DeviceId.TREADMILL);

        // assert default values
        assertEquals(DeviceId.TREADMILL, sts.getDevId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, sts.getStsId());
        assertEquals(CommandId.CALIBRATE, sts.getCmdId());
        assertEquals(6, sts.getLength());//min length
    }

    /** Tests the interpretation of the buffer
     *
     * @throws Exception
     */
    public void testBufferReading_calibrationStatus() throws Exception{

        CalibrateSts sts;
        ByteBuffer buff;
        TestCommandBuilder builder = new TestCommandBuilder();

        sts = new CalibrateSts(DeviceId.INCLINE_TRAINER);

        //initialize buffer command
        buff = builder.buildBuffer(sts.getDevId(), 6,sts.getCmdId(),StatusId.DONE);
        //Cal Status
        buff.put((byte)1);

        buff.put(Command.getCheckSum(buff));
        sts.handleStsMsg(buff);

        // assert default values
        assertEquals(DeviceId.INCLINE_TRAINER, sts.getDevId());
        assertEquals(StatusId.DONE, sts.getStsId());
        assertEquals(CommandId.CALIBRATE, sts.getCmdId());
        assertEquals(6, sts.getLength());
        assertEquals(1, sts.getCalResponseData());

    }
}
