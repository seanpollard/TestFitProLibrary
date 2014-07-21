/**
 * The system configuration for the communication mode.
 * @author Levi.Balling
 * @date 12/18/13
 * @version 1
 * This is to determine the mode and the type of communication that ifit can have with the device.
 */
package com.ifit.sparky.fecp.interpreter.device;

public enum SystemConfiguration {
    SLAVE("Ifit has complete control, if communication is lost system stops."),
    MASTER("Ifit only has access to getBitFieldData commands only, " +
            "if communication is lost system continues."),
    MULTI_MASTER("Both the systems need to play nice, if communication is lost, system continues"),
    SINGLE_MASTER("Only One Device will have Control of the Machine, All others are Listeners"),
    PORTAL_TO_SLAVE("this means the system is connected to a tablet, No Commands"),
    PORTAL_TO_MASTER("this means the system is connected to a tablet, And we can send commands");

    private String mDescription;//description about the mode

    /**
     * Constructor for the SystemConfiguration
     * @param description about the Configuration
     */
    SystemConfiguration(String description)
    {
        this.mDescription = description;
    }

    /**
     * Gets a description of the configuration
     * @return the description
     */
    public String getDescription()
    {
        return this.mDescription;
    }

    /**
     * Gets the unique id of the system configuration
     * @return the ordinal value
     */
    public int getVal()
    {
        return this.ordinal();
    }

    public static SystemConfiguration convert(byte val)
    {
        return SystemConfiguration.values()[val];
    }
}
