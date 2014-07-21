/**
 * Tests the Fecp Command Handler.
 * @author Levi.Balling
 * @date 2/5/14
 * @version 1
 * Tests the constructor, and when you add a command, and remove a command.
 */
package com.ifit.sparky.fecp.tests.brute;

import android.test.ActivityTestCase;

public class TestFecpCmdHandler extends ActivityTestCase {

    /**
     * Tests the Constructor.
     * @throws Exception
     */
    public void testFecpCmdHandler_Constructor() throws Exception{
//        TestToolDummyCom comTool = new TestToolDummyCom();
//
//        FecpCmdHandler cmdHandler  = new FecpCmdHandler(comTool, null);
//
//        assertEquals(cmdHandler.getCommController(), comTool);
    }

    /**
     * Tests the Adding a Fecp Command to the Fecp Command Handler.
     * @throws Exception
     */
    public void testFecpCmdHandler_AddFecpCmd() throws Exception{
//        TestToolDummyCom comTool = new TestToolDummyCom();
//        FecpCommand cmd;//command to validate sent
//
//        FecpCmdHandler cmdHandler  = new FecpCmdHandler(comTool, null);
//
//        cmd = new FecpCommand(new InfoCmd(DeviceId.TREADMILL));
//
//        assertEquals(0, cmd.getCmdSentCounter());
//        assertEquals(0, cmd.getCmdReceivedCounter());
//        cmdHandler.addFecpCommand(cmd);
//        assertEquals(0, cmd.getCmdSentCounter());
//        assertEquals(0, cmd.getCmdReceivedCounter());
//        Thread.sleep(100);//need to wait for the command to finish
//        assertEquals(1, cmd.getCmdSentCounter());
//
//        //test single execution of command
//        cmdHandler.addFecpCommand(cmd);
//        assertEquals(1, cmd.getCmdSentCounter());
//        Thread.sleep(100);//need to wait for the command to finish
//        assertEquals(2, cmd.getCmdSentCounter());
    }

    /**
     * Tests the Adding a Fecp Command to the Fecp Command Handler.
     * @throws Exception
     */
    public void testFecpCmdHandler_RemoveFecpCmd() throws Exception{
//        TestToolDummyCom comTool = new TestToolDummyCom();
//        FecpCommand cmd;//command to validate sent
//
//        FecpCmdHandler cmdHandler  = new FecpCmdHandler(comTool, null);
//
//        cmd = new FecpCommand(new InfoCmd(DeviceId.TREADMILL), null, 0, 100);
//        cmdHandler.addFecpCommand(cmd);
//        assertTrue(cmdHandler.removeFecpCommand(cmd));
//        cmdHandler.addFecpCommand(cmd);
//        assertTrue(cmdHandler.removeFecpCommand(DeviceId.TREADMILL, CommandId.GET_INFO));
    }

}
