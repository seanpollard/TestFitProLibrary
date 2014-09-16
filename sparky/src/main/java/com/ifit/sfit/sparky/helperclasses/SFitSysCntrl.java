/**
 * This will handle the interface to all the different systems.
 * @author Levi.Balling
 * @date 5/22/2014
 * @version 1
 * This will be a public source of information for all the different systems, and there unique information.
 */
package com.ifit.sfit.sparky.helperclasses;


import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.interpreter.device.SystemConfiguration;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.PortalDeviceCmd;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.ArrayList;
import java.util.Set;

public class SFitSysCntrl {

    private FecpController mFitProCntrl;
    protected ArrayList<FecpCommand> mFitProPeriodicCommands;// Periodic Commands specific to the Fragment

    //things that all fragments need
    private boolean mIsMetric;

    //items of interest
    // System Units  1
    // Workout Mode  1
    // Age if not master, if master send age down 1
    // Weight same as age 2
    //Max and Min Incline  4
    //Max KPH if treadmill 2
    //Idle Timeout  2
    //Pause Timeout  2

    /**
     * This should be created after a connection has been made
     * @param fitProCntrl the FitPro Communication Controller
     */
    public SFitSysCntrl(FecpController fitProCntrl)
    {
        this.mIsMetric = true;//default
        this.mFitProCntrl = fitProCntrl;
        this.mFitProPeriodicCommands = new ArrayList<FecpCommand>();
    }

    /**
     * Gets the FitProController
     * @return FitProController
     */
    public FecpController getFitProCntrl() {
        return mFitProCntrl;
    }

    /**
     * Gets a list of Periodic commands
     * @return list of commands
     */
    public ArrayList<FecpCommand> getFitProPeriodicCommands() {
        return mFitProPeriodicCommands;
    }

    /**
     * Gets the first command that has a ReadBitfieldId that matches,
     * Should only send Single commands for writes.
     * @param bitId Id that you are checking
     * @return FecpCommand if it matches, null if doesn't exist
     */
    public FecpCommand getReadCommand(BitFieldId bitId)
    {
        for (FecpCommand fitCmd : this.getFitProPeriodicCommands()) {
            Command cmd = fitCmd.getCommand();

            if(cmd.getCmdId() == CommandId.WRITE_READ_DATA)
            {
                WriteReadDataSts sts = (WriteReadDataSts)((WriteReadDataCmd)cmd).getStatus();

                if(sts.getBitFieldReadData().cmdContainsBitfield(bitId))
                {
                    return fitCmd;
                }
            }
            else if(cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)//only one command
            {
                return fitCmd;
            }
        }
        return null;
    }


    public boolean isMetric() {
        return mIsMetric;
    }

    public void setMetric(boolean isMetric) {
        this.mIsMetric = isMetric;
    }


    public void getInitialSysItems(OnCommandReceivedListener listener, int age, int weight)
    {
        //single command to get the initial items like max,min incline speed, etc..

        //items of interest
        // System Units  1
        // Workout Mode  1
        // Age if not master, if master send age down 1
        // Weight same as age 2
        //Max and Min Incline  4
        //Max KPH if treadmill 2
        //Idle Timeout  2
        //Pause Timeout  2
        //total 15 Bytes total response is ok to request them all

        SystemDevice dev = this.mFitProCntrl.getSysDev();
        if(dev.getSysDevInfo().getConfig() == SystemConfiguration.MASTER || dev.getSysDevInfo().getConfig() == SystemConfiguration.SLAVE) {
            //Load generic Fields if supported
            try {

                Set<BitFieldId> supportedData = dev.getInfo().getSupportedBitfields();
                FecpCommand fitProCmd;
                if (dev.getCommandSet().containsKey(CommandId.WRITE_READ_DATA)) {
                    WriteReadDataCmd readDataCmd;

                    fitProCmd = new FecpCommand(dev.getCommand(CommandId.WRITE_READ_DATA), listener);//check every half second
                    readDataCmd = (WriteReadDataCmd) fitProCmd.getCommand();

                    //check if we are the master, if so Write the Age if there is a user attached to the tablet
                    //this will automatically convert the calories response to be correct
                    //Master means it can Write data
                    if (this.mFitProCntrl.getSysDev().getSysDevInfo().getConfig() == SystemConfiguration.MASTER) {
                        if (age != 0) {
                            readDataCmd.addWriteData(BitFieldId.AGE, age);
                        }

                        if (weight != 0) {
                            readDataCmd.addWriteData(BitFieldId.WEIGHT, weight);
                        }
                    }

                    //Currently No support for System Units will add shortly by Aaron
//                if (supportedData.contains(BitFieldId.SYSTEM_UNITS)) {
//                    readDataCmd.addReadBitField(BitFieldId.SYSTEM_UNITS);
//                }


                    if (supportedData.contains(BitFieldId.WORKOUT_MODE)) {
                        readDataCmd.addReadBitField(BitFieldId.WORKOUT_MODE);
                    }


                    if (supportedData.contains(BitFieldId.AGE)) {
                        readDataCmd.addReadBitField(BitFieldId.AGE);
                    }

                    if (supportedData.contains(BitFieldId.WEIGHT)) {
                        readDataCmd.addReadBitField(BitFieldId.WEIGHT);
                    }

                    if (supportedData.contains(BitFieldId.MAX_GRADE)) {
                        readDataCmd.addReadBitField(BitFieldId.MAX_GRADE);
                    }

                    if (supportedData.contains(BitFieldId.MIN_GRADE)) {
                        readDataCmd.addReadBitField(BitFieldId.MIN_GRADE);
                    }

                    if (supportedData.contains(BitFieldId.MAX_KPH)) {
                        readDataCmd.addReadBitField(BitFieldId.MAX_KPH);
                    }

                    if (supportedData.contains(BitFieldId.MIN_KPH)) {
                        readDataCmd.addReadBitField(BitFieldId.MIN_KPH);
                    }

                    if (supportedData.contains(BitFieldId.IDLE_TIMEOUT)) {
                        readDataCmd.addReadBitField(BitFieldId.IDLE_TIMEOUT);
                    }

                    if (supportedData.contains(BitFieldId.PAUSE_TIMEOUT)) {
                        readDataCmd.addReadBitField(BitFieldId.PAUSE_TIMEOUT);
                    }

                    if (supportedData.contains(BitFieldId.VOLUME)) {
                        readDataCmd.addReadBitField(BitFieldId.VOLUME);
                    }

                    if (supportedData.contains(BitFieldId.BV_VOLUME)) {
                        readDataCmd.addReadBitField(BitFieldId.BV_VOLUME);
                    }

                    //add the command to FitPro
                    this.mFitProCntrl.addCmd(fitProCmd);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(dev.getSysDevInfo().getConfig() == SystemConfiguration.PORTAL_TO_SLAVE || dev.getSysDevInfo().getConfig() == SystemConfiguration.PORTAL_TO_MASTER)
        {
            //load single command to get the Device
            FecpCommand fitProCmd;
            try {
                fitProCmd = new FecpCommand(new PortalDeviceCmd(DeviceId.PORTAL),listener);//update every 0.5 seconds with all of the data

                //add the command to FitPro
                this.mFitProCntrl.addCmd(fitProCmd);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public void populatePeriodicSysItems(OnCommandReceivedListener listener)
    {
        //Setup the Gui to respond to the type of device that it is.
        SystemDevice dev = this.mFitProCntrl.getSysDev();
        DeviceId devId = dev.getInfo().getDevId();

        //Careful on your selection of interval, the User experience will be DIRECTLY affected by over sending commands

        if(dev.getSysDevInfo().getConfig() == SystemConfiguration.PORTAL_TO_MASTER || dev.getSysDevInfo().getConfig() == SystemConfiguration.PORTAL_TO_SLAVE) {
            //create a periodic command that will query for the system device, so we can update everything off of it.
            FecpCommand fitProCmd;
            try {
                fitProCmd = new FecpCommand(new PortalDeviceCmd(DeviceId.PORTAL),listener, 150, 175);//update every 0.5 seconds with all of the data

                //add the command to FitPro
                this.mFitProCntrl.addCmd(fitProCmd);
                this.mFitProPeriodicCommands.add(fitProCmd);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(dev.getSysDevInfo().getConfig() == SystemConfiguration.MASTER || dev.getSysDevInfo().getConfig() == SystemConfiguration.SLAVE) {
            //Load generic Fields if supported
            try {

                Set<BitFieldId> supportedData = dev.getInfo().getSupportedBitfields();
                FecpCommand fitProCmd;
                if (dev.getCommandSet().containsKey(CommandId.WRITE_READ_DATA)) {
                    WriteReadDataCmd readDataCmd;

                    fitProCmd = new FecpCommand(dev.getCommand(CommandId.WRITE_READ_DATA), listener, 50, 300);//every Second Commands
                    readDataCmd = (WriteReadDataCmd) fitProCmd.getCommand();

                    if (supportedData.contains(BitFieldId.DISTANCE)) {
                        readDataCmd.addReadBitField(BitFieldId.DISTANCE);
                    }

                    if (supportedData.contains(BitFieldId.PULSE)) {
                        readDataCmd.addReadBitField(BitFieldId.PULSE);
                    }

                    if (supportedData.contains(BitFieldId.RUNNING_TIME)) {
                        readDataCmd.addReadBitField(BitFieldId.RUNNING_TIME);
                    }

                    if (supportedData.contains(BitFieldId.CALORIES)) {
                        readDataCmd.addReadBitField(BitFieldId.CALORIES);
                    }

                    if (supportedData.contains(BitFieldId.AUDIO_SOURCE)) {
                        readDataCmd.addReadBitField(BitFieldId.AUDIO_SOURCE);
                    }

                    //add the command to FitPro
                    this.mFitProCntrl.addCmd(fitProCmd);
                    this.mFitProPeriodicCommands.add(fitProCmd);

                    fitProCmd = new FecpCommand(dev.getCommand(CommandId.WRITE_READ_DATA), listener, 0, 100);//every 1/4 Second Commands
                    readDataCmd = (WriteReadDataCmd) fitProCmd.getCommand();
                    // 0.5 Second Commands these are Push Result No Hold incrementing with these
                    //Fan Speed
                    //Volume
                    if (supportedData.contains(BitFieldId.FAN_SPEED)) {
                        readDataCmd.addReadBitField(BitFieldId.FAN_SPEED);
                    }

                    if (supportedData.contains(BitFieldId.VOLUME)) {
                        readDataCmd.addReadBitField(BitFieldId.VOLUME);
                    }
                    if (supportedData.contains(BitFieldId.GEARS)) {
                        readDataCmd.addReadBitField(BitFieldId.GEARS);
                    }

                    // 0.25 Second Commands Fast enough to keep up with Incline and Speed Button Hold.
                    // KPH          2
                    // Incline      2
                    // Resistance   2
                    // watts        2
                    // Torque       2
                    // RPM          1
                    // Mode         1
                    // Workout Controller 1
                    //Since We aren't querying to much with this we will combine the 0.5 and the 0.25
                    if (supportedData.contains(BitFieldId.KPH)) {
                        readDataCmd.addReadBitField(BitFieldId.KPH);
                    }

                    if (supportedData.contains(BitFieldId.GRADE)) {
                        readDataCmd.addReadBitField(BitFieldId.GRADE);
                    }
                    if (supportedData.contains(BitFieldId.RESISTANCE)) {
                        readDataCmd.addReadBitField(BitFieldId.RESISTANCE);
                    }

                    if (supportedData.contains(BitFieldId.WATTS)) {
                        readDataCmd.addReadBitField(BitFieldId.WATTS);
                    }

                    if (supportedData.contains(BitFieldId.TORQUE)) {
                        readDataCmd.addReadBitField(BitFieldId.TORQUE);
                    }

                    if (supportedData.contains(BitFieldId.RPM)) {
                        readDataCmd.addReadBitField(BitFieldId.RPM);
                    }

                    if (supportedData.contains(BitFieldId.WORKOUT_MODE)) {
                        readDataCmd.addReadBitField(BitFieldId.WORKOUT_MODE);
                    }

                    if (supportedData.contains(BitFieldId.WORKOUT)) {
                        readDataCmd.addReadBitField(BitFieldId.WORKOUT);
                    }

                    //add the command to FitPro
                    this.mFitProCntrl.addCmd(fitProCmd);
                    this.mFitProPeriodicCommands.add(fitProCmd);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
