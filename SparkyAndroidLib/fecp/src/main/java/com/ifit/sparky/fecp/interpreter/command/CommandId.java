/**
 * Handles the Command Id enum.
 * @author Levi.Balling
 * @date 12/9/13
 * @version 1
 * Release Date
 * @date 12/10/13
 * Contains the id for the message, and the Description of the device.
 */
package com.ifit.sparky.fecp.interpreter.command;

import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.lang.reflect.Constructor;

public enum CommandId {
    NONE(0, null, "No Command"),
    RAW(0xFF, RawDataCmd.class, "This is a raw command ment to pass through"),
    PORTAL_DEV_LISTEN(0x01, PortalDeviceCmd.class, "Gets the System Device and all it's current data"),
    WRITE_READ_DATA(0x02, WriteReadDataCmd.class,"Writes data and reads data in single command."),
    TEST(0x03, null, "Test the device."),
    CONNECT(0x04, null, "Connects to the device."),
    DISCONNECT(0x05, null, "Disconnects from the device."),
    CALIBRATE(0x06, CalibrateCmd.class,"Calibrates the device."),
    UPDATE(0x09, UpdateCmd.class, "Updates the device."),
    WRITE_DATA(0x0B, null, "Write Data."),
    SET_TESTING_KEY(0x70, SetTestingKeyCmd.class, "Sets a key for testing."),
    SET_TESTING_TACH(0x71, SetTestingTachCmd.class, "Sets a tach for testing."),
    GET_SUPPORTED_DEVICES(0x80, GetSubDevicesCmd.class, "Get Supported Devices."),
    GET_INFO(0x81, InfoCmd.class, "Get Device Info."),
    GET_SYSTEM_INFO(0x82, GetSysInfoCmd.class, "Get System Info."),
    GET_TASK_INFO(0x83, GetTaskInfoCmd.class, "Get Task Info."),
    GET_SUPPORTED_COMMANDS(0x88, GetCmdsCmd.class, "Get Supported Commands."),
    READ_DATA(0x90, null, "Read Data.");

    private int mId;
    private String mDescription;
    private Class mCommandClass;

    /**
     * constructor for the CommandId enum.
     * @param id value of the Command
     * @param description of what the command is for
     */
    CommandId(int id, Class cmdClass, String description)
    {
        this.mId = id;
        this.mCommandClass = cmdClass;
        this.mDescription = description;
    }

    /**
     * gets the id value
     * @return gets the Command Id Value
     */
    public int getVal()
    {
        return this.mId;
    }

    /**
     * gets the description of the Command.
     * @return a description of the Command.
     */
    public String getDescription()
    {
        return  this.mDescription;
    }

    /**
     * Creates an instance of Command associated with the ID
     * @param devId The device id to initialize the command with.
     * @return Command associated with the CommandId
     * @throws Exception if the command isn't supported yet.
     */
    public Command getCommand(DeviceId devId) throws Exception
    {
        if(CommandId.NONE == this)
        {
            return null;
        }
        if(this.mCommandClass == null)
        {
            throw new InvalidCommandException("Command not supported yet");
        }
        //create an instance of the specific feature
        Class<?>  className = this.mCommandClass;
        Class<?>[] classTypes = new Class[] {DeviceId.class};

        Constructor<?> cons = className.getConstructor(classTypes);

        return (Command)cons.newInstance(devId);
    }

    /**
     * Gets the CommandId based on the idNumber.
     * @param id The Command id Value
     * @return the Command Id
     * @throws InvalidCommandException if it doesn't exist
     */
    public static CommandId getCommandId(int id) throws InvalidCommandException
    {
        //go through all command ids and if it equals then return it.
        for (CommandId cmdId : CommandId.values())
        {
            if((id & 0xFF) == cmdId.getVal())
            {
                return cmdId;
            }
        }

        //error throw exception
        throw new InvalidCommandException(id);
    }
}
