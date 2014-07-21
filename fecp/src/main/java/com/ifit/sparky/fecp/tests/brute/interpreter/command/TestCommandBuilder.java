/**
 * This class is specific to building buffers for testing.
 * @author Levi.Balling
 * @date 12/18/13
 * @version 1
 * It is a little overkill to have a object to build buffers for testing,
 * but due to the large number of tests, this will be worth the effort.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TestCommandBuilder {


    public TestCommandBuilder()
    {

    }

    public ByteBuffer buildBuffer(DeviceId devId, int length, CommandId cmdId)
    {
        ByteBuffer buff = ByteBuffer.allocate(length);
        buff.put((byte)devId.getVal());
        buff.put((byte)length);
        buff.put((byte)cmdId.getVal());
        return buff;
    }

    public ByteBuffer buildBuffer(DeviceId devId, int length, CommandId cmdId, StatusId stsId)
    {
        ByteBuffer buff = ByteBuffer.allocate(length);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.put((byte)devId.getVal());
        buff.put((byte)length);
        buff.put((byte)cmdId.getVal());
        buff.put((byte)stsId.getVal());
        return buff;
    }
}
