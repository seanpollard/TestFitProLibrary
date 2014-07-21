/**
 * Tests all the items in the Error Code enum
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This will valitade the the getting a value from the ErrorCode Static function
 */
package com.ifit.sparky.fecp.tests.brute.error;

import com.ifit.sparky.fecp.error.ErrorCode;

import junit.framework.TestCase;

public class TestErrorCode extends TestCase{

    /** Tests the Constructors, sets, and Gets
     *
     * @throws Exception
     */
    public void testErrorCode_Constructor() throws Exception{
        ErrorCode code = ErrorCode.NONE;

        assertEquals(code, ErrorCode.NONE);
        code = ErrorCode.getErrorCode((short)1);

        assertEquals(code, ErrorCode.INVALID_DEVICE_ERROR);

        //where the Error doesn't exist
        code = ErrorCode.getErrorCode((short)64000);
        assertEquals(code, ErrorCode.NONE);

    }

}
