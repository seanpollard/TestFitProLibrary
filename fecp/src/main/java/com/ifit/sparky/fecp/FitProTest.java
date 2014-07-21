package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.TestComm;

public class FitProTest extends FecpController {

    /**
     * This is for Mock/Test Fecp Connections
     *
     * @throws Exception
     */
    public FitProTest() throws Exception {
        super(CommType.TESTING_COMM);
        this.mCommController = new TestComm();
    }
}
