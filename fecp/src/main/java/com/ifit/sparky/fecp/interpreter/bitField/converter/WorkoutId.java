/**
 * The mode of the system is what determines what is happening.
 * @author Levi.Balling
 * @date 2/19/14
 * @version 1
 * Each mode determines what is going on in every system.
 * You must set the mode in order to accomplish specific things.
 */
package com.ifit.sparky.fecp.interpreter.bitField.converter;

public enum WorkoutId {
    MANUAL(0,"None or Manual Workout"),
    MAP(1,"Map Workout"),
    TRAIN(2,"Train Workout"),
    COMPETE(3,"Compete Mode"),
    TRACK(4,"Track Workout"),
    SET_A_GOAL(5,"Set a Goal Workout"),
    VIDEO(6,"Video Workout"),
    LOSE_WEIGHT(7,"Lose Weight Workout"),
    CALORIES(8,"Calories Workout"),
    INTENSITY(9,"Intensity Workout"),
    INCLINE(10,"Incline Workout"),
    SPEED(11,"Speed Workout"),
    PULSE(12,"Pulse Workout");

    private String mDescription;
    private int mValue;

    /**
     * Initializes the type of mode
     * @param value the value of the mode
     * @param description the description of the mode
     */
    WorkoutId(int value, String description)
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


    public static WorkoutId getEnumFromId(int id)
    {
        for (WorkoutId enumId : WorkoutId.values()) {
            if(id == enumId.getValue())
            {
                return enumId;
            }
        }
        return WorkoutId.MANUAL;//default to none
    }
}
