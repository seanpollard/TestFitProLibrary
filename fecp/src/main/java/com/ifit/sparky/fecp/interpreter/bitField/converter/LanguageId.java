/**
 * Language Id class handles all of the different Languages
 * @author Levi.Balling
 * @date 7/1/14
 * @version 1
 * Handles items dealing with the Language, and has a specific ID values
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

public enum LanguageId {
    NONE(0),
    GERMAN(1),
    ENGLISH(2),
    SPANISH(3),
    FRENCH(4),
    ITALIAN(5),
    DUTCH(6),
    RUSSIAN(7),
    PORTUGUESE(8),
    CHINESE(9),
    JAPANESE(10);

    private int mId;

    private LanguageId(int id)
    {
        this.mId = id;
    }

    public int getLanguageId() {
        return mId;
    }

    /**
     * Gets the Language enum from the id
     * @param id the raw value of the Id input
     * @return Language id of the int value.
     */
    static public LanguageId getLanguageFromId(int id)
    {
        for (LanguageId langId : LanguageId.values()) {
            if(langId.getLanguageId() == id)
            {
                return langId;
            }
        }
        return LanguageId.NONE;
    }
}
