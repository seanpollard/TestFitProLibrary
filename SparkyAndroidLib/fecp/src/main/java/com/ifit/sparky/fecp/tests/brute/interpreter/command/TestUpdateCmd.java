/**
 * Tests the Update Command
 * @author Levi.Balling
 * @date 4/7/14
 * @version 1
 * This will test the constructor and all the formatting of the message to send.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.UpdateCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

public class TestUpdateCmd extends TestCase {


    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testUpdateCmd_Constructor() throws Exception{

        //check all the different options for generating the buffer
        UpdateCmd cmd;

        cmd = new UpdateCmd();
        //check default constructor
        assertEquals(13, cmd.getLength());
        assertEquals(DeviceId.NONE, cmd.getDevId());//a little redundant
        assertEquals(CommandId.UPDATE, cmd.getCmdId());
        assertEquals(StatusId.DEV_NOT_SUPPORTED, cmd.getStatus().getStsId());//default
        assertEquals(0, cmd.getDataSize());
        assertEquals(0, cmd.getPacketNumber());
        assertEquals(0, cmd.getStartingAddress());
        assertEquals(50, cmd.getUpdateData().capacity());


    }

    /**
     * Test the Get command Message function.
     * @throws Exception
     */
    public void testUpdateCmd_getCmdMsg() throws Exception
    {

    }


}
