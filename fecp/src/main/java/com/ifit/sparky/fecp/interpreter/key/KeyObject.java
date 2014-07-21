/**
 * This object handles all the items dealing with the key.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * Handle the Keycode, the RawKey data( the 0xFFFFFFFC ), the time the key was pressed,
 * and how long the key was press in milliseconds.
 */
package com.ifit.sparky.fecp.interpreter.key;

import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class KeyObject implements Serializable {

    private KeyCodes    mCode;
    private long        mRawKeyCode;//this is the 0xFFFFFFC thing
    private int         mTimePressed;
    private int         mTimeHeld;//how long it was held in milliseconds

    /**
     * default constructor for a keyObject
     */
    public KeyObject()
    {
        this.mCode = KeyCodes.NO_KEY;
        this.mRawKeyCode = 0;
        this.mTimePressed = 0;
        this.mTimeHeld = 0;
    }

    /**
     *  Constructor for all the values of the KeyObject.
     * @param code Cooked Keycode value
     * @param rawKey the status of all the key presses
     * @param timePressed the time the key was pressed from the start of the workout
     * @param timeHeld how long the key was held in milliseconds
     */
    public KeyObject(KeyCodes code, long rawKey, int timePressed, int timeHeld)
    {
        this.mCode = code;
        this.mRawKeyCode = rawKey;
        this.mTimePressed = timePressed;
        this.mTimeHeld = timeHeld;
    }

    public KeyObject(int rawCode, long rawKey, int timePressed, int timeHeld) throws InvalidKeyCodeException
    {
        this.mCode = KeyCodes.getKeyCode(rawCode);
        this.mRawKeyCode = rawKey;
        this.mTimePressed = timePressed;
        this.mTimeHeld = timeHeld;
    }

    /**
     * Gets the Cooked KeyCode
     * @return the cooked keycode
     */
    public KeyCodes getCookedKeyCode()
    {
        return this.mCode;
    }

    /**
     * Gets the raw value of all the button presses
     * @return the raw value of all the button presses
     */
    public long getRawKeyCode()
    {
        return this.mRawKeyCode;
    }

    /**
     * Gets the Time the button was pressed with respects to the start of the workout.
     * @return the time the button was pressed
     */
    public int getTimePressed()
    {
        return this.mTimePressed;
    }

    /**
     * gets how long the button was pressed in milliseconds
     * @return gets how long the button was pressed
     */
    public int getTimeHeld()
    {
        return this.mTimeHeld;
    }

    /**
     * Sets the Cooked Key Code.
     * @param code the cooked Key Code
     */
    public void setCode(KeyCodes code)
    {
        this.mCode = code;
    }

    /**
     * Sets the Cooked KeyCode with the keycode Value.
     * @param rawCode the keyCode value
     * @throws InvalidKeyCodeException if the keycode doesn't exist
     */
    public void setCode(int rawCode) throws InvalidKeyCodeException
    {
        this.mCode = KeyCodes.getKeyCode(rawCode);
    }
    /**
     * Sets the Cooked KeyCode with the keycode Value.
     * @param rawCode the keyCode value
     * @throws InvalidKeyCodeException if the keycode doesn't exist
     */
    public void setCode(long rawCode) throws InvalidKeyCodeException
    {
        this.mCode = KeyCodes.getKeyCode((int)rawCode);
    }

    /**
     * Sets the rawKey Values
     * @param rawKeyValues the Raw Key values
     */
    public void setRawKeyCode(long rawKeyValues)
    {
        this.mRawKeyCode = rawKeyValues;
    }

    /**
     * Gets the Time the key was pressed in relation to the start of the workout in seconds
     * @param time in seconds
     */
    public void setTimePressed(int time)
    {
        this.mTimePressed = time;
    }

    /**
     * Gets how long the button was held for in milliseconds.
     * @param time in milliseconds
     */
    public void setTimeHeld(int time)
    {
        this.mTimeHeld = time;
    }

    public void writeObject(ByteBuffer stream) throws IOException {

        stream.putInt(this.mCode.getVal());
        stream.putLong(this.mRawKeyCode);
        stream.putInt(this.mTimePressed);
        stream.putInt(this.mTimeHeld);
    }

    public void readObject(ByteBuffer stream) throws IOException, ClassNotFoundException {
        int keyVal = stream.getInt();
        try {
            this.mCode = KeyCodes.getKeyCode(keyVal);

        } catch (InvalidKeyCodeException e) {
            Log.d("InvalidKeyCode", "Invalid key actualVal["+ keyVal +"]. " + e.getMessage());
            this.mCode = KeyCodes.NO_KEY;//default
        }

        this.mRawKeyCode = stream.getLong();
        this.mTimePressed = stream.getInt();
        this.mTimeHeld = stream.getInt();
    }

    public String toString()
    {
        //Format the data about the keypress
        String result = "Key ";
        result += "cooked " + this.mCode.name();
        result += ", category " + this.mCode.getCategory();
        result += ", value " + this.mCode.getVal() + "\n";
        result += "rawCode " + String.format("%08X",this.mRawKeyCode) + "\n";
        result += "timeHeld " + this.mTimeHeld +"\n";
        result += "timePressed " + this.mTimePressed;

        return result;
    }

}
