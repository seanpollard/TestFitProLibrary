/**
 * This is the interface for the Fecp Interceptor.
 * @author Levi.Balling
 * @date 4/16/2014
 * @version 1
 * This Intercept callback will have to handle all commands going to the Device,
 * and fill the data with the expected response, or unexpected response.
 */
package com.ifit.sparky.fecp.testingUtil;

import com.ifit.sparky.fecp.FecpCommand;

public interface InterceptCallback {

    /**
     * This will be called every time a command is sent.
     * @param fCmd the Fecp Command to the System, that was intercepted.
     */
    void fecpCmdListener(FecpCommand fCmd);
}
