/**
 * This Class is to handle items specifically with the Task on the RTOS.
 * @author Levi.Balling
 * @date 2/11/14
 * @version 1
 * this will hold information about a specific task on the system.
 */
package com.ifit.sparky.fecp.interpreter.status;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class CpuTask implements Comparable<CpuTask>, Serializable {

    //items dealing with the CPU
    private int mTaskIndex;
    private int mInterval;
    private boolean mExecutFlag;
    private int mRecentTime;
    private int mWorseTime;
    private int mBestTime;
    private int mNumberOfCalls;
    private int mNumberOfMisses;
    private String mName;
    private double mMainClkFrequency;

    /**
     * default constructor
     */
    public CpuTask()
    {
        this.mTaskIndex = 0;
        this.mInterval = 0;
        this.mExecutFlag = false;
        this.mRecentTime = 0;
        this.mWorseTime = 0;
        this.mBestTime = 0;
        this.mNumberOfCalls = 0;
        this.mNumberOfMisses = 0;
        this.mNumberOfMisses = 0;
        this.mNumberOfMisses = 0;
        this.mName = "";
        this.mMainClkFrequency = 0;
    }

    /**
     * Copy Constructor
     * @param originalTask the task you wish to copy
     */
    public CpuTask(CpuTask originalTask)
    {
        this.mTaskIndex = originalTask.getTaskIndex();
        this.mInterval = originalTask.getInterval();
        this.mExecutFlag = originalTask.getExecutFlag();
        this.mRecentTime = originalTask.getRecentTime();
        this.mWorseTime = originalTask.getWorseTime();
        this.mBestTime = originalTask.getBestTime();
        this.mNumberOfCalls = originalTask.getNumberOfCalls();
        this.mNumberOfMisses = originalTask.getNumberOfMisses();
        this.mName = originalTask.getTaskName();
        this.mMainClkFrequency = originalTask.getMainClkFrequency();
    }

    /**
     * Gets the Task Index
     * @return the index of the task
     */
    public int getTaskIndex() {
        return mTaskIndex;
    }


    /**
     * Gets the interval of the Task
     * @return the interval in uSeconds
     */
    public int getInterval() {
        return mInterval;
    }

    /**
     * Gets whether the Task is scheduled to execute
     * @return whether it is set to execute
     */
    public boolean getExecutFlag() {
        return mExecutFlag;
    }

    /**
     * Gets the Most recent time of a task
     * @return the time in clock cycles
     */
    public int getRecentTime() {
        return mRecentTime;
    }

    /**
     * Gets the worse Time of the Task
     * @return value is the number of Clock cycles
     */
    public int getWorseTime() {
        return mWorseTime;
    }

    /**
     * Gets the Best Time of the Task
     * @return value is the number of Clock cycles
     */
    public int getBestTime() {
        return mBestTime;
    }

    /**
     * Gets the Number of function calls to the task, since startup
     * @return Number of function calls
     */
    public int getNumberOfCalls() {
        return mNumberOfCalls;
    }

    /**
     * Gets the Number of missed function calls to the task, since startup
     * @return Number of missed function calls
     */
    public int getNumberOfMisses() {
        return mNumberOfMisses;
    }

    /**
     * Gets the Name of the Task
     * @return the name
     */
    public String getTaskName() {
        return mName;
    }

    /**
     * Gets the Main frequency of the clock
     * @return the frequency in Hz
     */
    public double getMainClkFrequency() {
        return mMainClkFrequency;
    }

    /**
     * Sets the index of the task.
     * @param taskIndex the task index
     */
    public void setTaskIndex(int taskIndex) {
        this.mTaskIndex = taskIndex;
    }

    /**
     * Sets the time of the interval.
     * @param interval the time in uSeconds
     */
    public void setInterval(int interval) {
        this.mInterval = interval;
    }

    /**
     * Sets whether it is set to execute.
     * @param executFlag the execute flag
     */
    public void setExecutFlag(boolean executFlag) {
        this.mExecutFlag = executFlag;
    }

    /**
     * Sets the time of the Most recent task call.
     * @param recentTime the most recent task call
     */
    public void setRecentTime(int recentTime) {
        this.mRecentTime = recentTime;
    }

    /**
     * Sets the worse time of the Task call
     * @param worseTime the worse task call time
     */
    public void setWorseTime(int worseTime) {
        this.mWorseTime = worseTime;
    }

    /**
     * Sets the Best time of the Task Call
     * @param bestTime the best time of the task
     */
    public void setBestTime(int bestTime) {
        this.mBestTime = bestTime;
    }

    /**
     * Sets the number of times the task was called
     * @param numberOfCalls the number of times the task has executed
     */
    public void setNumberOfCalls(int numberOfCalls) {
        this.mNumberOfCalls = numberOfCalls;
    }

    /**
     * sets the number of misses
     * @param numberOfMisses the number of misses
     */
    public void setNumberOfMisses(int numberOfMisses) {
        this.mNumberOfMisses = numberOfMisses;
    }

    /**
     * Sets the name of the task, empty if not supported.
     * @param name name of the task
     */
    public void setTaskName(String name) {
        this.mName = name;
    }


    /**
     * Sets the Frequency of the main clock used for calculating the approximate time of each task.
     * @param mainClkFrequency the main clk speed for the Brain Board MCU in Hz
     */
    public void setMainClkFrequency(double mainClkFrequency) {
        this.mMainClkFrequency = mainClkFrequency;
    }

    /**
     * Handles the buffer from the system
     * @param buff buffer of formatted data position at the correct location.
     */
    public  void handleTaskBuff(ByteBuffer buff)
    {
        //buffer will start at the top of the data that pertains to the task
        int tempMsgLength;
        //index
        this.mTaskIndex = buff.get();
        //interval
        this.mInterval = buff.getShort();
        //executeFlag
        this.mExecutFlag = buff.get() == 1;
        //worst Time
        this.mWorseTime = buff.getInt();
        //Best Time
        this.mBestTime = buff.getInt();
        //last Time
        this.mRecentTime = buff.getInt();
        //Number of calls
        this.mNumberOfCalls = buff.getInt();
        //Number of misses
        this.mNumberOfMisses = buff.getShort();
        //length of name
        tempMsgLength = buff.get();
        //name
        for(int i = 0; i < tempMsgLength; i++)
        {
            this.mName += buff.get();
        }
    }

    @Override
    public String toString() {
        //time of one cycle
        double cycleTime = (1000/this.mMainClkFrequency);
        String tempStr = "Task=" + mTaskIndex +
                ", Interval=" + mInterval +
                ", Recent=" + String.format("%.3f",(mRecentTime * cycleTime)) + "mSec" +
                ", Worse=" + String.format("%.3f",(mWorseTime * cycleTime)) + "mSec" +
                ", mBestTime=" + String.format("%.3f",(mBestTime * cycleTime)) + "mSec" +
                ", mNumberOfCalls=" + mNumberOfCalls +
                ", mNumberOfMisses=" + mNumberOfMisses;
        if(this.mName.length() != 0)
        {
            tempStr += ", mName='" + mName;
        }
        if(this.mExecutFlag)
        {
            tempStr += ", EXE";
        }
        tempStr += '\n';
        return tempStr;
    }


    @Override
    public int compareTo(CpuTask cpuTask) {
        return Double.compare(this.getTaskIndex(), cpuTask.getTaskIndex());
    }
}
