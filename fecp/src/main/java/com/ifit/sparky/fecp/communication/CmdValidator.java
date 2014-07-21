/**
 * This class will handle whether the command is valid or not.
 * @author Levi.Balling
 * @date 6/11/2014
 * @version 1
 * This checks whether a command is valid, device exists, and whether the bitfield id is supported.
 * If any of these fail it will reject the command with a message.
 */
package com.ifit.sparky.fecp.communication;

import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CmdValidator {

    /**
     * Validates the Device, whether it is supported or not
     * @param dev the main system device
     * @param id the command's device id
     * @return true if valid, false if invalid
     */
    public static boolean ValidateDevice(SystemDevice dev, DeviceId id)
    {
        //recursively check all of the devices in the system device. and check if there are any sub devices
        List<DeviceId> allDevIds = getAllSubDeviceIds(dev);
        return allDevIds.contains(id);
    }

    /**
     * helper function to get all of the device ids of a system recursively
     * @param dev the Device to get all the sub devices
     * @return list of device ids
     */
    private static List<DeviceId> getAllSubDeviceIds(Device dev)
    {
        ArrayList<DeviceId> idList = new ArrayList<DeviceId>();

        for (Device device : dev.getSubDeviceList()) {
            idList.addAll(getAllSubDeviceIds(device));
        }

        idList.add(dev.getInfo().getDevId());
        return idList;
    }

    /**
     * Validates whether the command is valid
     * @param dev the System device
     * @param devId the Device Id
     * @param cmdId the Command Id
     * @return True if valid false if invalid
     */
    public static boolean ValidateCommand(SystemDevice dev, DeviceId devId, CommandId cmdId)
    {
        //get all the sub devices
        List<Device> allDevices = getAllSubDevices(dev);

        for (Device device : allDevices) {
            //check if any of the devices have a command
            if(device.getInfo().getDevId() == devId && device.getCommandSet().containsKey(cmdId))
            {
                return true;
            }
        }

        return false;


    }


    /**
     * Recursive helper function to simplify validation
     * @param dev the device
     * @return list of subdevices
     */
    private static List<Device> getAllSubDevices(Device dev)
    {
        ArrayList<Device> subDevList = new ArrayList<Device>();

        for (Device device : dev.getSubDeviceList()) {
            subDevList.addAll(getAllSubDevices(device));
        }
        subDevList.add(dev);

        return subDevList;
    }

    /**
     * Validates whether it is a valid bitfield or invalid bitfield
     * @param dev System Device
     * @param cmd Command with the Bitfields
     * @throws InvalidBitFieldException
     */
    public static void ValidateBitfieldCmd(SystemDevice dev, WriteReadDataCmd cmd) throws InvalidBitFieldException
    {
        //scan through all the supported bitfields
        Set<BitFieldId> supportedBitIds = dev.getInfo().getSupportedBitfields();

        //todo report list of array
        for (BitFieldId fieldId : cmd.getWriteBitData().getMsgData().keySet()) {
            if(!supportedBitIds.contains(fieldId)) {
                throw new InvalidBitFieldException("Invalid Write Bitfield("+fieldId.name()+":"+ fieldId.getVal()+") System Device Doesn't support it.");
            }
        }

        for (BitFieldId fieldId : ((WriteReadDataSts) cmd.getStatus()).getBitFieldReadData().getMsgData().keySet()) {
            if(!supportedBitIds.contains(fieldId)) {
                throw new InvalidBitFieldException("Invalid Read Bitfield("+fieldId.name()+":"+ fieldId.getVal()+") System Device Doesn't support it.");
            }
        }


    }


}
