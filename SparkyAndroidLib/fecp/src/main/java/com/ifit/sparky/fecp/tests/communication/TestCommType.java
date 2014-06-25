/**
 * Test the Communication type.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * Tests the Communication type.
 */
package com.ifit.sparky.fecp.tests.communication;

import com.ifit.sparky.fecp.communication.CommType;

import junit.framework.TestCase;

public class TestCommType extends TestCase {

    /**
     * Runs through the Tests for the Enum values
     * @throws Exception
     */
    public void testEnum() throws Exception{
        CommType type;

        type = CommType.USB;

        assertEquals(type, CommType.USB);//dumb test, but checks change
    }
}
