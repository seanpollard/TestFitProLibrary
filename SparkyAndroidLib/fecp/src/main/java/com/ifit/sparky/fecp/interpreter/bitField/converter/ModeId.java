/**
 * The mode of the system is what determines what is happening.
 * @author Levi.Balling
 * @date 2/19/14
 * @version 1
 * Each mode determines what is going on in every system.
 * You must set the mode in order to accomplish specific things.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

public enum ModeId {
    UNKNOWN(0,"Unknown Mode"),
    IDLE(1,"Idle Mode"),
    RUNNING(2,"Running Mode"),
    PAUSE(3,"Pause Mode"),
    RESULTS(4,"ResultsMode"),
    DEBUG(5,"Debug Mode"),
    LOG(6,"Log Mode"),
    MAINTENANCE(7,"Maintenance Mode"),
    DMK(8,"Safety key out Mode");

    private String mDescription;
    private int mValue;

    /**
     * Initializes the type of mode
     * @param value the value of the mode
     * @param description the description of the mode
     */
    ModeId(int value, String description)
    {
        this.mValue = value;
        this.mDescription = description;

    }

    /**
     * Gets the description of the mode
     * @return the description
     */
    public String getDescription()
    {
        return this.mDescription;
    }

    /**
     * gets the raw value of the mode. should match the ordinal.
     * @return the raw value of the mode
     */
    public int getValue()
    {
        return this.mValue;
    }

    public static ModeId getEnumFromId(int id)
    {
        for (ModeId enumId : ModeId.values()) {
            if(id == enumId.getValue())
            {
                return enumId;
            }
        }
        return ModeId.UNKNOWN;//default to none
    }
}
