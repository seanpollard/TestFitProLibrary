/**
 * Tests the Data Command Status Base.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * This will make sure all of the methods in the Data Cmd Sts base are valid.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.command;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.GradeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.DataBaseCmd;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

public class TestDataCmdStsBase extends TestCase {

    /**
     * Setups the TestRunner for Status.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    /**
     * Closes the TestRunner for Status.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /** Tests the Constructors.
     *
     * @throws Exception
     */
    public void testDataCmdSts_Constructor() throws Exception{

        DataBaseCmd dataBase;


        dataBase = new DataBaseCmd();
        //default constructor
        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

    }
    /** Tests the Copy Constructors.
     *
     * @throws Exception
     */
    public void testDataCmdSts_CopyConstructor() throws Exception{

        DataBaseCmd dataBase;
        DataBaseCmd copyDataBase;

        dataBase = new DataBaseCmd();
        //default constructor
        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());
        dataBase.addBitfieldData(BitFieldId.KPH, 2.5);
        dataBase.addBitfieldData(BitFieldId.GRADE, 10.5);
        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(4, dataBase.getMsgDataBytesCount());
        assertEquals(2.5, dataBase.getMsgData().get(BitFieldId.KPH));
        assertEquals(10.5, dataBase.getMsgData().get(BitFieldId.GRADE));

        //create copy, and make sure it is the same
        copyDataBase = new DataBaseCmd(dataBase);
        assertEquals(1, copyDataBase.getNumOfDataBytes());
        assertEquals(4, copyDataBase.getMsgDataBytesCount());
        assertEquals(2.5, copyDataBase.getMsgData().get(BitFieldId.KPH));
        assertEquals(10.5, copyDataBase.getMsgData().get(BitFieldId.GRADE));

        //change original and make sure that it doesn't change both of them.
        dataBase.addBitfieldData(BitFieldId.KPH, 32.1);
        dataBase.addBitfieldData(BitFieldId.GRADE, 1.2);
        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(4, dataBase.getMsgDataBytesCount());
        assertEquals(32.1, dataBase.getMsgData().get(BitFieldId.KPH));
        assertEquals(1.2, dataBase.getMsgData().get(BitFieldId.GRADE));

        //check copy and make sure it is the same.
        assertEquals(1, copyDataBase.getNumOfDataBytes());
        assertEquals(4, copyDataBase.getMsgDataBytesCount());
        assertEquals(2.5, copyDataBase.getMsgData().get(BitFieldId.KPH));
        assertEquals(10.5, copyDataBase.getMsgData().get(BitFieldId.GRADE));
    }

    /** Tests the add bitfield and data.
     *
     * @throws Exception
     */
    public void testDataCmdSts_addBitField() throws Exception{

        DataBaseCmd dataBase;

        dataBase = new DataBaseCmd();
        //default constructor
        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

        dataBase.addBitfieldData(BitFieldId.KPH, 10.5);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(2, dataBase.getMsgDataBytesCount());

        //add another byte to the first section
        dataBase.addBitfieldData(BitFieldId.GRADE, 10.5);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(4, dataBase.getMsgDataBytesCount());

        //add another byte to the 2nd section.
        dataBase.addBitfieldData(BitFieldId.RESISTANCE, 50.00);//%50.00

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(6, dataBase.getMsgDataBytesCount());
    }

    /** Tests the removing a bitfield and data.
     *
     * @throws Exception
     */
    public void testDataCmdSts_removeBitField() throws Exception{

        DataBaseCmd dataBase;

        dataBase = new DataBaseCmd();
        //default constructor
        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

        //try removing an item when there isn't one there.
        //should do nothing
        dataBase.removeBitfieldData(BitFieldId.KPH);

        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

        //add one and remove it.
        dataBase.addBitfieldData(BitFieldId.KPH, 10.5);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(2, dataBase.getMsgDataBytesCount());

        //try removing an item when there is one there.
        dataBase.removeBitfieldData(BitFieldId.KPH);

        assertEquals(0, dataBase.getNumOfDataBytes());
        assertEquals(0, dataBase.getMsgDataBytesCount());

        //add 2
        dataBase.addBitfieldData(BitFieldId.KPH, 10.5);
        dataBase.addBitfieldData(BitFieldId.GRADE, 10.5);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(4, dataBase.getMsgDataBytesCount());

        //remove 1 and make sure it is still there
        dataBase.removeBitfieldData(BitFieldId.GRADE);

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(2, dataBase.getMsgDataBytesCount());

        //add 1 in a different section
        dataBase.addBitfieldData(BitFieldId.RESISTANCE, 50.00);//%50.00

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(4, dataBase.getMsgDataBytesCount());

        //remove the 1st section item
        dataBase.removeBitfieldData(BitFieldId.KPH);

        assertEquals(1, dataBase.getNumOfDataBytes());// Caught Bug in code
        assertEquals(2, dataBase.getMsgDataBytesCount());

        //try adding one that is already there
        dataBase.addBitfieldData(BitFieldId.RESISTANCE, 50.00);//%50.00

        assertEquals(1, dataBase.getNumOfDataBytes());
        assertEquals(2, dataBase.getMsgDataBytesCount());

    }

    /** Tests getting the message data formatting.
     *
     * @throws Exception
     */
    public void testDataCmdSts_getMsgDataHeader() throws Exception{

        DataBaseCmd dataBase;
        ByteBuffer buffer;

        dataBase = new DataBaseCmd();

        //get the message header for an empty list
        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(1, buffer.capacity());
        assertEquals(0, buffer.get());

        dataBase.addBitfieldData(BitFieldId.KPH, 10.5);

        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(2, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(1, buffer.get());//targetMPH bit Caught Bug in Code

        //add another in same section
        dataBase.addBitfieldData(BitFieldId.GRADE, 10.5);

        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(2, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(3, buffer.get());

        //add another in different section
        dataBase.addBitfieldData(BitFieldId.FAN_SPEED, 50);

        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(3, buffer.capacity());
        assertEquals(2, buffer.get());//number of sections
        assertEquals(3, buffer.get());
        assertEquals(1, buffer.get());

        //skip a section and add it
        //add another in different section
        dataBase.addBitfieldData(BitFieldId.PULSE, 10);

        buffer = dataBase.getMsgDataHeader();
        buffer.position(0);
        assertEquals(3, buffer.capacity());
        assertEquals(2, buffer.get());//number of sections
        assertEquals(3, buffer.get());
        assertEquals(5, buffer.get());
    }

    /** Tests getting the all the data, section bytes to the end of the last data object.
     *
     * @throws Exception
     */
    public void testDataCmdSts_getWriteDataMsg() throws Exception{

        DataBaseCmd dataBase;
        ByteBuffer buffer;

        dataBase = new DataBaseCmd();

        //get the message header for an empty list
        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(1, buffer.capacity());
        assertEquals(0, buffer.get());

        dataBase.addBitfieldData(BitFieldId.KPH, 10.5);

        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(4, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(1, buffer.get());
        assertEquals(1050, buffer.getShort());//Caught bug

        //add another in same section
        dataBase.addBitfieldData(BitFieldId.GRADE, 10.50);//%10.50 percent incline

        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(6, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(3, buffer.get());
        assertEquals(1050, buffer.getShort());//target speed
        assertEquals(1050, buffer.getShort());//target incline Caught Bug

        //add another in different section
        dataBase.addBitfieldData(BitFieldId.RESISTANCE, 50.00);

        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(8, buffer.capacity());
        assertEquals(1, buffer.get());//number of sections
        assertEquals(7, buffer.get());//section 0
        assertEquals(1050, buffer.getShort());//speed
        assertEquals(1050, buffer.getShort());//incline
        assertEquals(5000, buffer.getShort());//Resistance

        //skip a section and add it
        //add another in different section
        dataBase.addBitfieldData(BitFieldId.PULSE, 123);

        buffer = dataBase.getWriteMsgData();
        buffer.position(0);
        assertEquals(10, buffer.capacity());
        assertEquals(2, buffer.get());//number of sections
        assertEquals(7, buffer.get());//section 0
        assertEquals(4, buffer.get());//section 1
        assertEquals(1050, buffer.getShort());//target speed
        assertEquals(1050, buffer.getShort());//target incline
        assertEquals(5000, buffer.getShort());//target Resistance
        assertEquals(123, buffer.get());//Current Pulse
    }

    /** Tests handling the data in the buffer.
     * buffer has to be in the correct position.
     *
     * @throws Exception
     */
    public void testDataCmdSts_handleReadData() throws Exception{
        DataBaseCmd dataBase;
        ByteBuffer buffer;
        Map<BitFieldId, BitfieldDataConverter> map;

        dataBase = new DataBaseCmd();

        //test empty byte buffer
        buffer = ByteBuffer.allocate(1);//tes
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0);
        buffer.position(0);
        dataBase.handleReadData(buffer);
        assertEquals(0, buffer.position());//position shouldn't change

        //Test Speed value
        buffer = ByteBuffer.allocate(2);//tes
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) 100);//ten mph
        buffer.position(0);

        dataBase.addBitfieldData(BitFieldId.KPH, 0);
        map = dataBase.handleReadData(buffer);

        assertEquals(1.0, ((SpeedConverter)map.get(BitFieldId.KPH)).getSpeed());

        // Test the Speed and the Incline
        buffer = ByteBuffer.allocate(4);
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) 100);//ten mph
        buffer.putShort((short) 1234);//incline
        buffer.position(0);

        dataBase.addBitfieldData(BitFieldId.GRADE, 0);
        map = dataBase.handleReadData(buffer);

        assertEquals(1.0, ((SpeedConverter)map.get(BitFieldId.KPH)).getSpeed());
        assertEquals(12.34, ((GradeConverter)map.get(BitFieldId.GRADE)).getIncline());

        // Test skipping a section, and the order of the items
        buffer = ByteBuffer.allocate(3);
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) 1234);//incline
        buffer.put((byte) 231);//Pulse
        buffer.position(0);

        dataBase.removeBitfieldData(BitFieldId.KPH);
        dataBase.addBitfieldData(BitFieldId.PULSE, 0);
        map = dataBase.handleReadData(buffer);

        assertEquals(12.34, ((GradeConverter)map.get(BitFieldId.GRADE)).getIncline());
        assertEquals(231, ((ByteConverter)map.get(BitFieldId.PULSE)).getValue());

        //re add the speed, and check order
        // Test skipping a section, and the order of the items
        buffer = ByteBuffer.allocate(5);
        buffer.position(0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) 100);//ten mph
        buffer.putShort((short) 1234);//incline
        buffer.put((byte) 231);//Pulse
        buffer.position(0);

        dataBase.addBitfieldData(BitFieldId.KPH, 0);
        map = dataBase.handleReadData(buffer);

        assertEquals(1.0, ((SpeedConverter)map.get(BitFieldId.KPH)).getSpeed());
        assertEquals(12.34, ((GradeConverter)map.get(BitFieldId.GRADE)).getIncline());
        assertEquals(231, ((ByteConverter)map.get(BitFieldId.PULSE)).getValue());

    }

    /** Tests if the command or status has the Specific bitfield
     *
     * @throws Exception
     */
    public void testDataCmdSts_containsBitfield() throws Exception{
        DataBaseCmd dataBase;
        dataBase = new DataBaseCmd();

        assertFalse(dataBase.cmdContainsBitfield(BitFieldId.KPH));

        dataBase.addBitfieldData(BitFieldId.KPH, 0);
        assertTrue(dataBase.cmdContainsBitfield(BitFieldId.KPH));

        //remove and test that it is gone
        dataBase.removeBitfieldData(BitFieldId.KPH);

        assertFalse(dataBase.cmdContainsBitfield(BitFieldId.KPH));


    }
}
