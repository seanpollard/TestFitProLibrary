package com.ifit.sparky.fecp.tests.Integration;

/**
 * Created by eetestlab on 2/4/14.
 */
public class TestIncline extends TestIntegration {
    //class just for incline type tests
    public int testIncline() throws Exception{
        //outline for code support #928 in redmine
        //set an incline command to 5 %
        //validate response, was incline sent?
        //read current incline
        //check current incline against what was sent
        //run the above logic for the entire range of incline values -3 to 30% incline for example
        //start a timer
        //run the incline calibration
        //check the time of the calibration for the 1.5 sec change in direction
        //check bottom seek and return transmax from calibration command
        int transmax = 0;
        return transmax;
    }
}
