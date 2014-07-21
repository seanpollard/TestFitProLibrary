package com.ifit.sparky.fecp.tests.Integration;

import junit.framework.TestCase;

/**
 * Created by eetestlab on 1/21/14.
 * This is Sean's first attempt to try and set up and verify that the android tablet clock
 * is the same as the computer clock to verify the stopwatch portion of the software checklist
 */
public class TestIntegration extends TestCase{
    public void testTimeFiveMin() throws Exception
    {
        //start stopwatch
        //get java time
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long androidTime = 0;
        boolean testPassed = false;
        //loop for 5 minute time test
        while (elapsedTime < 5*60*1000){
            //get time from android and compare to java time
            //Not sure how to do this
            //the test took 300 seconds which is 5 minutes so it looks like it works
            //androidTime = getDeviceTime();
            elapsedTime = (new java.util.Date()).getTime() - startTime;
        }
        if(androidTime == elapsedTime)
            testPassed = true;
        else
            testPassed = false;
    }
    public void testTimeTenMin() throws Exception
    {
        //start stopwatch
        //get java time
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long androidTime = 0;
        boolean testPassed = false;
        //loop for 10 minute time test
        while (elapsedTime < 10*60*1000){
            //get time from android and compare to java time
            //Not sure how to do this
            //the test should take 600 seconds which is 10 minutes
            //androidTime = getDeviceTime();
            elapsedTime = (new java.util.Date()).getTime() - startTime;
        }
        if(androidTime == elapsedTime)
            testPassed = true;
        else
            testPassed = false;
    }
    public void testTimeFifteenMin() throws Exception
    {
        //start stopwatch
        //get java time
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long androidTime = 0;
        boolean testPassed = false;
        //loop for 15 minute time test
        while (elapsedTime < 15*60*1000){
            //get time from android and compare to java time
            //Not sure how to do this
            //the test should take 900 seconds which is 15 minutes
            //androidTime = getDeviceTime();
            elapsedTime = (new java.util.Date()).getTime() - startTime;
        }
        if(androidTime == elapsedTime)
            testPassed = true;
        else
            testPassed = false;
    }
    public void testTimeModes() throws Exception{
        //outline for code support #930 in redmine
        //start timer stopwatch
        //change mode to running
        //wait 5 min
        //read current displayed time and store value
        //wait 10 min
        //read current displayed time and store value
        //wait 15 min
        //read current displayed time and store values
        //stop, reset, and start stopwatch
        //change mode to pause
        //keep reading mode status until mode changes to idle for pause timeout
        //stop timer and record pause timeout
        //stop, reset, and start stopwatch
        //keep reading mode status until mode changes from idle timeout
        //record time taken going out of idle mode
        //verify 5, 10, 15 min values, pause and idle timeout values
        //against hardcoded constant values to make sure the timeout works
    }
    public void testSystemConfiguration() throws Exception{
        //outline for code support #951
        //read System Config data from Brainboard or config file
        //verify the data against PDM data
    }

    //test for checking max values hardcoded
    public boolean maxCheckTest(int max){
        //simple method for checking max against standard incline trainer 30% max incline
        int standardMax = 30;
        if(max == standardMax)
            return true;
        else return false;
    }
    //test for checking min values hardcoded
    public boolean minCheckTest(int min){
        //simple method for checking min against standard incline trainer -6 min incline
        int standardMin = 6;
        if(min == standardMin)
            return true;
        else return false;
    }

    //super test to run through all the tests; not sure if necessary
    public void superTest() throws Exception{

        //testConstructor_fecpCommand();
        //testSetters_fecpCommand();
        //testCallback_fecpCommand();
        //testGetterSetter_systemDevice();
        //testConstructor_systemConfiguration();
        //testCallback_SystemCallback();
        //testConstructor_fecpController();
        //testInitializeConnection_FecpController();
        //testConstructor_systemDevice();
        //testGetterSetter_systemDevice();
        //getSysDev();
        //getIsConnected();
        //testInitializeConnection_FecpController();

    }
}
