/**
 * Handles all the device SuperClass items.
 * @author Levi.Balling
 * @date 12/9/2013
 * @version 1
 * Release Date
 * @date 12/10/13
 * This class will handle the device items, all the available commands and status connected
 * to the commands.
 */
package com.ifit.sparky.fecp.interpreter.device;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.*;
import com.ifit.sparky.fecp.interpreter.status.InvalidStatusException;

import java.io.Serializable;
import java.util.*;

public class Device implements Serializable {

    private Map<CommandId, Command> mCommandMap;//list of all the available commands.
    private ArrayList<Device> mSubDevArrayList;//list of all the subDevices.
    private DeviceInfo mInfo;// all the major information about a system.

    /**
     * Default constructor for devices.
     */
    public Device() throws InvalidStatusException, InvalidCommandException
    {
        this.mCommandMap = new LinkedHashMap<CommandId, Command>();
        this.mSubDevArrayList = new ArrayList<Device>();
        this.mInfo = new DeviceInfo();
        populateDefaultCommands();
    }

    /**
     * constructor for single device.
     * @param id the deviceId
     */
    public Device(DeviceId id) throws InvalidStatusException, InvalidCommandException
    {
        this.mCommandMap = new LinkedHashMap<CommandId, Command>();
        this.mSubDevArrayList = new ArrayList<Device>();
        this.mInfo = new DeviceInfo();
        this.mInfo.setDevId(id);
        populateDefaultCommands();
    }

    /**
     * Constructor for adding lists of commands and sub-devices.
     * @param commands List of commands to add
     * @param devices the List of Sub devices
     * @param info the info about the device.
     */
    public Device(
            Collection<Command> commands,
            Collection<Device> devices,
            DeviceInfo info) throws InvalidStatusException, InvalidCommandException
    {
        this.mCommandMap = new LinkedHashMap<CommandId, Command>();
        this.mSubDevArrayList = new ArrayList<Device>();

        this.mSubDevArrayList.addAll(devices);
        this.mInfo = info;
        populateDefaultCommands();
        this.addCommands(commands);
    }

    /*******************************
     * GETTERS
     ******************************/

    /**
     * gets the list of subdevices
     * @return list of subdevices
     */
    public ArrayList<Device> getSubDeviceList()
    {
        return this.mSubDevArrayList; /* list of subdevices */
    }

    /**
     * gets the device from the list of subdevices, based on the id value.
     * @return returns the Device that matches
     */
    public Device getSubDevice(int idVal)
    {
        for(Device dev : this.mSubDevArrayList)
        {
            if(dev.mInfo.getDevId().getVal() == idVal)
            {
                return dev;/* returns the Device that matches */
            }
        }

        return null; /* no device exists with that id value */
    }

    /**
     * gets the device from the list of subdevices, based on the id.
     * @return returns the Device that matches
     */
    public Device getSubDevice(DeviceId id)
    {
        for(Device dev : this.mSubDevArrayList)
        {
            if(dev.mInfo.getDevId() == id)
            {
                return dev;/* returns the Device that matches */
            }
        }

        return null; /* no device exists with that id value */
    }

    /**
     * gets the list of supported Commands
     * @return list of commands
     */
    public Map<CommandId, Command> getCommandSet()
    {
        return this.mCommandMap; /* list of commands */
    }

    /**
     * gets the Command from the list of commands, based on the id value.
     * @return the Command that matches
     */
    public Command getCommand(int idVal) throws InvalidCommandException
    {
        if(this.mCommandMap.containsKey(CommandId.getCommandId(idVal)))
        {
            return this.mCommandMap.get(CommandId.getCommandId(idVal));//returns the Command
            // that matches
        }

        return null; /* no device exists with that id value */
    }

    /**
     * gets the Command from the list of Commands, based on the id.
     * @return the Command that matches
     */
    public Command getCommand(CommandId id)
    {
        if(this.mCommandMap.containsKey(id))
        {
            return this.mCommandMap.get(id);//returns the Command
            // that matches
        }
        return null; /* no device exists with that id */
    }

    /**
     * Gets the info about the device
     * @return the device info
     */
    public DeviceInfo getInfo()
    {
        return this.mInfo;
    }

    /**
     * Added a command to the list of commands
     * @param cmd command to add to the devices available commands
     * @throws InvalidCommandException if the command is already in the list you can't add it again.
     */
    public void addCommand(Command cmd) throws InvalidCommandException
    {
        if(cmd == null)
        {

           throw new InvalidCommandException(0);//null
        }
        if(this.mCommandMap.containsKey(cmd.getCmdId()))
        {
            throw new InvalidCommandException(cmd);
        }

        this.mCommandMap.put(cmd.getCmdId(), cmd);
    }

    /**
     * Adds the list of commands to the device.
     * @param cmds set of commands to add to the device.
     * @throws Exception if there already is a command Error.
     */
    public void addCommands (Collection<Command> cmds) throws InvalidCommandException
    {
        for(Command tempCmd : cmds)
        {
            if(!this.getCommandSet().containsKey(tempCmd.getCmdId()))//if it doesn't contain a default command add it.
            {
                this.addCommand(tempCmd);
            }
        }
    }

    /**
     * Added a device to the list of subdevices
     * @param dev the sub device
     */
    public void addSubDevice(Device dev)
    {
        this.mSubDevArrayList.add(dev);
    }

    /**
     * Adds a collection of Sub devices to the device.
     * @param devices to add as subdevices.
     */
    public void addAllSubDevice(Collection<Device> devices)
    {
        for(Device dev : devices)
        {
            this.addSubDevice(dev);
        }
    }

    /**
     * Sets the device's info.
     * @param info the Device Info
     */
   public void setDeviceInfo(DeviceInfo info)
   {
       this.mInfo = info;
       //update all the commands to also have the correct device id
       for (Map.Entry<CommandId, Command> commandEntry : this.mCommandMap.entrySet()) {
           commandEntry.getValue().setDevId(info.getDevId());
       }
   }


    /**
     * This populates the command map with all the default commands that all devices will support.
     */
    private void populateDefaultCommands() throws InvalidStatusException, InvalidCommandException
    {
        //default commands
        this.addCommand(new InfoCmd(this.mInfo.getDevId()));
        this.addCommand(new GetCmdsCmd(this.mInfo.getDevId()));
        this.addCommand(new GetSubDevicesCmd(this.mInfo.getDevId()));
        this.addCommand(new WriteReadDataCmd(this.mInfo.getDevId()));
    }

    /**
     * determines whether the device is supported with this device
     * @param id the DeviceId
     * @return true if in sub device list, false if not
     */
    public boolean containsDevice(DeviceId id)
    {
        for (Device device : this.getSubDeviceList()) {
            if(device.getInfo().getDevId() == id)
            {
                return true;
            }
        }
        return  false;
    }

    @Override
    public String toString() {
        //get a list of
        String deviceStr;
        deviceStr = this.mInfo.getDevId().getDescription();
        deviceStr += " swV=" + this.getInfo().getSWVersion();
        deviceStr += " hwV=" + this.getInfo().getHWVersion();
        deviceStr += " bitfields=";
        for(BitFieldId bitId : this.getInfo().getSupportedBitfields())
        {
            deviceStr += bitId.getDescription() + ", ";
        }

        return deviceStr;
    }


}
