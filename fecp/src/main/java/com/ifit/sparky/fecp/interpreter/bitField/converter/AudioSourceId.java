/**
 * The mode of the system is what determines what is happening.
 * @author Levi.Balling
 * @date 2/19/14
 * @version 1
 * Each mode determines what is going on in every system.
 * You must set the mode in order to accomplish specific things.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

public enum AudioSourceId {
    NONE(0,"No Source"),
    PC(1,"PC or Tablet"),
    BRAIN_BOARD(2,"Brain Board"),
    MP3(3,"MP3 device"),
    IPOD(4,"IPOD"),
    TV(5,"TV"),
    FM(6,"FM Radio");

    private String mDescription;
    private int mValue;

    /**
     * Initializes the type of mode
     * @param value the value of the mode
     * @param description the description of the mode
     */
    AudioSourceId(int value, String description)
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

    public static AudioSourceId getEnumFromId(int id)
    {
        for (AudioSourceId audioSourceId : AudioSourceId.values()) {
            if(id == audioSourceId.getValue())
            {
                return audioSourceId;
            }
        }
        return AudioSourceId.NONE;//default to none
    }
}
