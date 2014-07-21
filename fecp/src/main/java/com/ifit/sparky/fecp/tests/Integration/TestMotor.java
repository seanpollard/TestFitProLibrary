package com.ifit.sparky.fecp.tests.Integration;

import junit.framework.TestCase;
import com.ifit.sparky.fecp.tests.Integration.TestType;

/**
 * Created by eetestlab on 1/23/14.
 */
//class just for motor type tests
public class TestMotor extends TestIntegration{
    //The testStartSpeed is planned to automate #16 of the software
    //checklist to make sure that the machine starts at 1.0mph or 2.0kph
    public void testStartSpeed() throws Exception{
        //outline for code support #958 **first task to automate**
        //send basic start command to start motor at on position
        //request actual speed from device to make sure it is connected and moving
        //read speed received into this code which should be target speed
        //check against constant variable of 1.0 mph
        //make sure formatting is right for verification for english or metric units
    }
    //the testMaxSpeedTime is planned to automate #59 of the software
    //checklist to time the amount of time it takes to go from 0 to max speed
    public void testMaxSpeedTime() throws Exception{
        //outline for code
        //look up max speed for device
        //send basic start command to start motor at on position
        //start stopwatch timer
        //send command to change speed to max speed
        //read current speed until actual is the same as target
        //stop stopwatch and return/display/record the value of the stopwatch
    }
    public void testMotorPWM() throws Exception{
        //outline for code support #924 in redmine
        //send down speed command
        //wait a specified time
        //read actual distance
        //check against range for PWM standard
        //not sure if we have access to PWM values
    }
    public void testModeChange() throws Exception{
        //outline for code support #926 in redmine
        //change to mode 1
        //read mode status to verify mode 1
        //change to mode 2
        //read mode status to verify mode 2
        //go through all modes in above manner to validate mode changes have occurred
    }
    public void testSpeedController() throws Exception{
        //outline for code support #927 in redmine
        //do not have to check if treadmill with TestType class method
        //because this method will only be called for treadmills
        //run test for treadmill & incline trainers
        //send speed command
        //validate speed was sent
        //read speed
        //validate speed response is what you sent originally
        //go through entire speed range 1-15mph for example
    }
    public void testDistance() throws Exception{
        //outline for code support #929 in redmine
        //start timer stopwatch
        //send a speed of 10 mph for a 6 min/mile pace
        //wait 6 minutes
        //read distance value
        //verify distance is 1.0mph
    }
}
