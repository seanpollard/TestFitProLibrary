package com.ifit.sfit.sparky;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;

import java.util.ArrayList;
import java.util.Collection;

/**************************************************************************************
 * Created by jc.almonte on 7/2/14.                                                    *
 * Test class for the BitFields                                                        *
 * Tests to perform:                                                                   *
 *      1. Test read/write operation on each bitfield - testBitfieldRdWr()              *
 *                                                                                     *
 *          - Send valid values to write to all the bitfields                          *
 *          - Read values written to verify them                                       *
 *          - If read only, verify writing operation to bitfield fails                 *
 *          - If bitfield is not supported, verify exception is thrown                 *
 *                                                                                     *
 *      2. Test valid input values for each bitfield - testBitfieldValuesValidation()  *
 *          - Send invalid values to each bitfield and verify exception is thrown      *
 *                                                                                     *
 *                                                                                     *
 **************************************************************************************/

public class TestBitfields {
    //Variables needed to initialize connection with Brainboard
    private FecpController mFecpController;
    private TestApp mAct;
    private HandleCmd hCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private SystemDevice MainDevice;

    public TestBitfields(FecpController fecpController, TestApp act, SFitSysCntrl ctrl) {
        //Get controller sent from the main activity (TestApp)
        try {
            this.mFecpController = fecpController;
            this.mAct = act;
            this.mSFitSysCntrl = ctrl;
            hCmd = new HandleCmd(this.mAct);// Init handlers
            //Get current system device
            MainDevice = this.mFecpController.getSysDev();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

     //---------------------------------------------------------------------//
     //    Test read/write operation on each bitfield- testBitfieldRdWr()   //
     //                                                                     //
     //         - Send valid values to write to all the supported bitfields //
     //         - Read values written to verify them                        //
     //         - If read only, verify writing operation to bitfield fails  //
     //         - If bitfield is not supported, verify exception is thrown  //
     //---------------------------------------------------------------------//

     public String testBitfieldRdWr() throws Exception {

        FecpCommand cmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA),hCmd,100,1000);

        String bitfieldRdWrResults = "\n\n----------------------------BITFIELDS TEST RESULTS----------------------------\n\n"; //to store results of test
        bitfieldRdWrResults+= "------Testing Unsupported Bitfields------\n\n"; //to store results of test

         ArrayList<BitFieldId> supportedBitFields = new ArrayList<BitFieldId>(MainDevice.getInfo().getSupportedBitfields());
        //ArrayList<BitFieldId> unsupportedBitFields = new ArrayList<BitFieldId>(); //To hold unsupported BitFields

        //loop through all bitfields, try to read unsupported ones, verify exception is thrown
        for (BitFieldId bf: BitFieldId.values())
        {
            if(!supportedBitFields.contains(bf))
            {
              //  unsupportedBitFields.add(bf);
              //Try to read a value from this bitfeld and verify that it throws exception
                try{
                    ((WriteReadDataCmd)cmd.getCommand()).addReadBitField(bf);
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    bitfieldRdWrResults+="* FAIL * NO Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "\n";

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    bitfieldRdWrResults+="* PASS * Exception thrown when trying to read unsupported bitfield:  "+bf.name()+ "\n";
                    bitfieldRdWrResults+="Details: " + ex.getMessage() +"\n\n";
                }

            }
        }

         bitfieldRdWrResults+= "------Testing Read/Write Access for Supported Bitfields------\n\n"; //to store results of test

            //Loop through all supported fields
        for(BitFieldId bf: supportedBitFields)
        {
            //if it's readonly, try to write to it and verify exception is thrown
            if(bf.getReadOnly())
            {
                try {
                    ((WriteReadDataCmd) cmd.getCommand()).addWriteData(bf, 10);
                    mSFitSysCntrl.getFitProCntrl().addCmd(cmd);
                    Thread.sleep(1000);
                    bitfieldRdWrResults += "* FAIL * NO Exception thrown when trying to write read-only bitfield:  " + bf.name() + "\n";
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    bitfieldRdWrResults+="* PASS * Exception thrown when trying to write read-only bitfield:  "+bf.name()+ "\n";
                    bitfieldRdWrResults+=" Details: " + ex.getMessage() +"\n\n";
                }
            }
        }

         return bitfieldRdWrResults;
    }

     //-------------------------------------------------------------------------------//
     //   Test valid input values for each bitfield - testBitfieldValuesValidation()  //
     //                                                                               //
     //      - Send invalid values to each bitfield and verify exception is thrown    //
     //-------------------------------------------------------------------------------//

     public String testBitfieldValuesValidation()
     {
         String bitfiedValuesVAlidatonResults = "";

         return bitfiedValuesVAlidatonResults;
     }

}
