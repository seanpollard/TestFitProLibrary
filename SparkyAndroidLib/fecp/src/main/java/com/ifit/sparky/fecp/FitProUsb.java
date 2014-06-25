/**
 * Creates a connection to the Fitpro system.
 * @author Levi.Balling
 * @date 5/16/2014
 * @version 1
 * sets up the communication to the FitPro system.
 */
package com.ifit.sparky.fecp;

import android.content.Context;
import android.content.Intent;

import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.UsbComm;

public class FitProUsb extends FecpController {

    /**
     * Sets up the controller, and all the facets dealing with the controller, specifically USB
     * @param context  the application context
     * @param intent Intent that is used to handle the communication
     * @throws Exception
     */
    public FitProUsb(Context context, Intent intent) throws Exception {
        super(CommType.USB);
        this.mCommController = new UsbComm(context, intent, 100);
    }
}
