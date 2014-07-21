/**
 * Event listener for When errors occur with the System.
 * @author Levi.Balling
 * @date 4/3/2014
 * @version 1
 * When you add these event listeners to the system it will call them when an error occurs.
 */
package com.ifit.sparky.fecp.error;

public interface ErrorEventListener {


    /**
     * This will notify anyone that an error has occurred with the system
     * @param error the error that occurred.
     */
    void onErrorEventListener(SystemError error);


}
