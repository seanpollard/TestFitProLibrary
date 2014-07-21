/**
 * Tests all the items in the status SuperClass
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This class will test all the items of the status superclass. This includes the enums, invalid
 * inputs, and valid inputs
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.CpuTask;
import com.ifit.sparky.fecp.interpreter.status.Status;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import junit.framework.TestCase;

public class TestCpuTask extends TestCase{

    /** Tests the Constructors, sets, and Gets
     *
     * @throws Exception
     */
    public void testCpuTask_Constructor() throws Exception{

        CpuTask task = new CpuTask();

        // assert default values
        assertEquals(0, task.getTaskIndex());
        assertEquals(0, task.getInterval());
        assertEquals(false, task.getExecutFlag());
        assertEquals(0, task.getRecentTime());
        assertEquals(0, task.getWorseTime());
        assertEquals(0, task.getBestTime());
        assertEquals(0, task.getNumberOfCalls());
        assertEquals(0, task.getNumberOfMisses());
        assertEquals("", task.getTaskName());

        task.setTaskIndex(12);
        task.setInterval(125);
        task.setExecutFlag(true);
        task.setRecentTime(500);
        task.setWorseTime(800);
        task.setBestTime(100);
        task.setNumberOfCalls(10);
        task.setNumberOfMisses(1);
        task.setTaskName("HELLO");

        assertEquals(12, task.getTaskIndex());
        assertEquals(125, task.getInterval());
        assertEquals(true, task.getExecutFlag());
        assertEquals(500, task.getRecentTime());
        assertEquals(800, task.getWorseTime());
        assertEquals(100, task.getBestTime());
        assertEquals(10, task.getNumberOfCalls());
        assertEquals(1, task.getNumberOfMisses());
        assertEquals("HELLO", task.getTaskName());
    }

}
