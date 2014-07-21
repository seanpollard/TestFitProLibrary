/**
 * Tests the CommandId enum object.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * This will handle all the different enums and validate that they are correct.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.InvalidCommandException;

import junit.framework.TestCase;

public class TestCommandId extends TestCase {

    /**
     * Setups the TestRunner for CommandId
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for CommandId
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Runs through the Tests for the Enum values
     * @throws Exception
     */
    public void testEnum() throws Exception{
        CommandId idOne = CommandId.NONE;
        CommandId idTwo = CommandId.CALIBRATE;

        assertEquals(CommandId.NONE, idOne);
        assertEquals(0x00, idOne.getVal());

        assertEquals(CommandId.CALIBRATE, idTwo);
        assertEquals(0x06, idTwo.getVal());
        assertNotNull(CommandId.CALIBRATE.getDescription());
    }

    /**
     * Runs through the Tests for the Enum values
     * @throws Exception
     */
    public void testGetStatic_CommandId() throws Exception{
        try
        {
            CommandId idOne = CommandId.getCommandId(0);
            assertEquals(CommandId.NONE, idOne);
        }
        catch (Exception ex)
        {
            fail();//shouldn't throw an exception
        }
        try
        {
            CommandId.getCommandId(253);
            fail();//should throw an exception before here
        }
        catch (InvalidCommandException ex)
        {
            assertTrue(true);//this should throw an exception
        }
    }

}
