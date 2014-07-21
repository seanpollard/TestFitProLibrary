/**
 * This will be the control interface for the testing the Ifit side of the commands.
 * @author Levi.Balling
 * @date 4/16/2014
 * @version 1
 * This will be used to intercept the commands that are apart of the system.
 */
package com.ifit.sparky.fecp.testingUtil;

import com.ifit.sparky.fecp.FecpCommand;

public class CmdInterceptor {

    private boolean mInterceptorEnabled;
    private InterceptCallback mInterceptCallback;

    /**
     * Creates an interceptor for the Fecp Controller.
     * @param cmdListener the callback for the fecpCommands to handle
     */
    public CmdInterceptor(InterceptCallback cmdListener)
    {
        this.mInterceptorEnabled = false;
        this.mInterceptCallback = cmdListener;
    }

    /**
     * Enables the intercepting of commands to the system.
     * (CAUTION) Disables communication to the device, implements own results.
     * @param enabled if true enables interceptor, if false runs as normal.
     */
    public void enableInterceptor(boolean enabled)
    {
        // not going in intercept without any callback implemented.
        if(this.mInterceptCallback == null)
        {
            return;
        }
        this.mInterceptorEnabled = enabled;
    }

    /**
     * Gets the status of whether the system is enabled or not.
     * @return true if enabled(intercepting commands), false if regular operation.
     */
    public boolean isInterceptorEnabled()
    {
        return this.mInterceptorEnabled;
    }

    /**
     * Intercepts the command from the communication to the brain board.
     * This is not for testing (used for FecpController).
     * @param fCmd the command being intercepted
     */
    public void interceptFecpCommand(FecpCommand fCmd)
    {
        this.mInterceptCallback.fecpCmdListener(fCmd);
    }
}
