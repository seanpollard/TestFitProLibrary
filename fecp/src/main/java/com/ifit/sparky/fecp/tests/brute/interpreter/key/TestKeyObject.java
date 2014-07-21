/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/13/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.key;

import com.ifit.sparky.fecp.interpreter.key.InvalidKeyCodeException;
import com.ifit.sparky.fecp.interpreter.key.KeyCodes;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;

import junit.framework.TestCase;

public class TestKeyObject extends TestCase {

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
     * @throws Exception if there is an failed test
     */
    public void testConstructor_KeyObject() throws Exception{

        KeyObject key;

        //test default constructor
        key = new KeyObject();

        assertEquals(KeyCodes.NO_KEY, key.getCookedKeyCode());
        assertEquals(0, key.getRawKeyCode());
        assertEquals(0, key.getTimePressed());
        assertEquals(0, key.getTimeHeld());

        //test Second constructor
        key = new KeyObject(KeyCodes.START, 0xFFFFFFFF, 123, 123);

        assertEquals(KeyCodes.START, key.getCookedKeyCode());
        assertEquals(0xFFFFFFFF, key.getRawKeyCode());
        assertEquals(123, key.getTimePressed());
        assertEquals(123, key.getTimeHeld());

        //test Third constructor
        key = new KeyObject(1, 0x0FFFFFFF, 123, 123);

        assertEquals(KeyCodes.STOP, key.getCookedKeyCode());
        assertEquals(0x0FFFFFFF, key.getRawKeyCode());
        assertEquals(123, key.getTimePressed());
        assertEquals(123, key.getTimeHeld());
    }

    /**
     * Tests the getters and setters for the KeyObject
     * @throws Exception if there is an error
     */
    public void testGetterSetters_KeyObject() throws Exception
    {
        KeyObject key;

        //test getters
        key = new KeyObject(KeyCodes.START, 0xCFFFFFFF, 0,1234);

        assertEquals(KeyCodes.START, key.getCookedKeyCode());
        assertEquals(0xCFFFFFFF, key.getRawKeyCode());
        assertEquals(0, key.getTimePressed());
        assertEquals(1234, key.getTimeHeld());

        //set and retest
        key.setCode(KeyCodes.SPEED_UP);
        key.setRawKeyCode(0xFFFFCFFF);
        key.setTimePressed(4321);
        key.setTimeHeld(0);

        assertEquals(KeyCodes.SPEED_UP, key.getCookedKeyCode());
        assertEquals(0xFFFFCFFF, key.getRawKeyCode());
        assertEquals(4321, key.getTimePressed());
        assertEquals(0, key.getTimeHeld());

        key.setCode(1);//stop key
        assertEquals(KeyCodes.STOP, key.getCookedKeyCode());
        assertNotNull(key.toString());
    }

    /**
     * Tests the getters and setters for the KeyObject
     * @throws Exception if there is an error
     */
    public void testExceptions_KeyObject() throws Exception
    {
        KeyObject key;

        //Test Exception throwing
        try
        {
            key = new KeyObject(9991, 0, 1,2);//throw exception
            fail();
        }
        catch (InvalidKeyCodeException ex)
        {
            assertTrue(true);
        }

        key = new KeyObject(KeyCodes.START, 0, 1,2);//throw exception
        //Test Exception throwing
        try
        {
            key.setCode(9991);
            fail();
        }
        catch (InvalidKeyCodeException ex)
        {
            assertTrue(true);
        }
    }

    public void testEnum_KeyCodes() throws Exception
    {
        KeyCodes code;
        code = KeyCodes.START;

        assertEquals(KeyCodes.START, code);
        assertEquals(2, code.getVal());
        assertEquals("Basic", code.getCategory());
    }
}
