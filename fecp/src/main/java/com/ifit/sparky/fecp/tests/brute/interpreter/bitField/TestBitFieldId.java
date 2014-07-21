/**
 * Tests the BitFieldId enum and its methods.
 * @author Levi.Balling
 * @date 12/11/13
 * @version 1
 * Tests the enum and all of the different values dealing with the DataConverters also.
 * This is a Brute Force test, and should only be ran over night and before release.
 */
package com.ifit.sparky.fecp.tests.brute.interpreter.bitField;

import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.InvalidBitFieldException;
import com.ifit.sparky.fecp.interpreter.bitField.converter.AudioSourceConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.AudioSourceId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.CaloriesConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.GradeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.KeyObjectConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.LongConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ResistanceConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ShortConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WeightConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WorkoutConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WorkoutId;
import com.ifit.sparky.fecp.interpreter.key.KeyCodes;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class TestBitFieldId extends TestCase {

    /**
     * Setups the TestRunner for CommandId
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Closes the TestRunner for CommandId
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Runs through the Tests for the Enum values
     * @throws Exception
     */
    public void testEnum() throws Exception{

        BitFieldId idOne;
        BitFieldId idTwo;
        BitfieldDataConverter converterOne;
        ByteBuffer rawData = ByteBuffer.allocate(2);
        rawData.order(ByteOrder.LITTLE_ENDIAN);
        Byte b1;
        Byte b2;

        idOne = BitFieldId.KPH;
        idTwo = BitFieldId.GRADE;
        b1 = 0x0B;
        b2 = 0x01;
        rawData.put(b1);
        rawData.put(b2);

        assertEquals(BitFieldId.KPH, idOne);
        assertEquals(0, idOne.getVal());
        assertEquals(0, idOne.getSection());
        assertEquals(0, idOne.getBit());
        assertEquals(2, idOne.getSize());
        assertEquals(false, idOne.getReadOnly());
        assertNotNull(idOne.getDescription());

        assertEquals(BitFieldId.GRADE, idTwo);
        assertEquals(1, idTwo.getVal());
        assertEquals(0, idTwo.getSection());
        assertEquals(1, idTwo.getBit());
        assertEquals(2, idTwo.getSize());
        assertEquals(false, idTwo.getReadOnly());
        assertNotNull(idTwo.getDescription());

        //test changes in the converter behind the seen to make sure it still matches
        converterOne =idOne.getData(rawData);

        assertEquals(2.67, ((SpeedConverter)converterOne).getSpeed());
        assertEquals(BitFieldId.KPH, idOne);

    }

    /**
     * Test the get data aspect of the Enum
     * @throws Exception
     */
    public void testConverterGetData_BitfieldId() throws Exception
    {

        BitFieldId idOne;
        BitfieldDataConverter converter;
        ByteBuffer buff;

        //test Byte Converter with int inputs
        idOne = BitFieldId.FAN_SPEED;
        buff = ByteBuffer.allocate(1);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        // Test all unsigned Byte values
        for(int i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++)
        {
            buff.clear();
            buff.put((byte) i);
            converter = idOne.getData(buff);
            assertEquals(ByteConverter.class, converter.getClass());//should be the same class
            assertEquals(((byte)i & 0xFF), ((ByteConverter)converter).getValue());
        }

        //test Incline Converter
        idOne = BitFieldId.GRADE;
        buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        // Test all unsigned short values
        for(int i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++)
        {
            double expectResult;
            buff.clear();
            buff.putShort((short) i);
            expectResult = (i + 0.0) / 100;
            converter = idOne.getData(buff);
            assertEquals(GradeConverter.class, converter.getClass());//should be the same class
            assertEquals(expectResult, ((GradeConverter)converter).getIncline());
        }

        //test Key object converter
        idOne = BitFieldId.KEY_OBJECT;
        buff = ByteBuffer.allocate(14);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        //Test all the keycodes
        for(KeyCodes code : KeyCodes.values())
        {
            buff.clear();
            buff.putShort((short) code.getVal());
            buff.putLong(0xFFFFFFFFFFFFFFFCL);
            buff.putShort((short) 1234);
            buff.putShort((short) 4321);
            converter = idOne.getData(buff);
            assertEquals(KeyObjectConverter.class, converter.getClass());//should be the same class
            assertEquals(code, ((KeyObjectConverter)converter).getKeyObject().getCookedKeyCode());
            assertEquals(0xFFFFFFFC, ((KeyObjectConverter)converter).getKeyObject().getRawKeyCode());
            assertEquals(1234, ((KeyObjectConverter)converter).getKeyObject().getTimePressed());
            assertEquals(4321, ((KeyObjectConverter)converter).getKeyObject().getTimeHeld());
        }
        //test Long Converter
        idOne = BitFieldId.DISTANCE;
        buff = ByteBuffer.allocate(4);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        //test limits only, anything above short is to big.
        //min
        buff.clear();
        buff.putInt(Integer.MIN_VALUE);
        converter = idOne.getData(buff);
        assertEquals(LongConverter.class, converter.getClass());//should be the same class
        assertEquals(Integer.MIN_VALUE , ((LongConverter)converter).getValue());

        //0
        buff.clear();
        buff.putInt(0);
        converter = idOne.getData(buff);
        assertEquals(LongConverter.class, converter.getClass());//should be the same class
        assertEquals(0, ((LongConverter)converter).getValue());

        //max
        buff.clear();
        buff.putInt(Integer.MAX_VALUE);
        converter = idOne.getData(buff);
        assertEquals(LongConverter.class, converter.getClass());//should be the same class
        assertEquals(Integer.MAX_VALUE, ((LongConverter)converter).getValue());

        //test Resistance Converter
        idOne = BitFieldId.RESISTANCE;
        buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        // Test all unsigned short values
        for(int i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++)
        {
            double expectResult;
            buff.clear();
            buff.putShort((short) i);
            expectResult = (((short)i & 0xFFFF) + 0.0) / 100;
            converter = idOne.getData(buff);
            assertEquals(ResistanceConverter.class, converter.getClass());//should be the same class
            assertEquals(expectResult, ((ResistanceConverter)converter).getResistance());
        }

        //test Short Converter with int inputs we want this to be unsigned
        idOne = BitFieldId.WATTS;
        buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        // Test all unsigned short values
        for(int i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++)
        {
            buff.clear();
            buff.putShort((short) i);
            converter = idOne.getData(buff);
            assertEquals(ShortConverter.class, converter.getClass());//should be the same class
            assertEquals(((short)i & 0xFFFF), ((ShortConverter)converter).getValue());
        }

        //test Speed Converter
        buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        idOne = BitFieldId.KPH;

        // Test all unsigned short values
        for(int i = 0; i < 65536; i++)
        {
            double expectResult;
            buff.clear();
            buff.putShort((short)i);
            expectResult = (i + 0.0) / 100;
            converter = idOne.getData(buff);
            assertEquals(SpeedConverter.class, converter.getClass());//should be the same class
            assertEquals(expectResult, ((SpeedConverter)converter).getSpeed());
        }

        //test Mode Converter
        buff = ByteBuffer.allocate(1);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        idOne = BitFieldId.WORKOUT_MODE;

        // Test all unsigned short values
        for (ModeId id : ModeId.values()) {

            ModeId expectResult;
            buff.clear();
            buff.put((byte) id.getValue());
            expectResult = id;
            converter = idOne.getData(buff);
            assertEquals(ModeConverter.class, converter.getClass());//should be the same class
            assertEquals(expectResult, ((ModeConverter)converter).getMode());
        }

        //test Workout Converter
        buff = ByteBuffer.allocate(1);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        idOne = BitFieldId.WORKOUT;

        // Test all unsigned short values
        for (WorkoutId id : WorkoutId.values()) {

            WorkoutId expectResult;
            buff.clear();
            buff.put((byte)id.getValue());
            expectResult = id;
            converter = idOne.getData(buff);
            assertEquals(WorkoutConverter.class, converter.getClass());//should be the same class
            assertEquals(expectResult, ((WorkoutConverter)converter).getWorkout());
        }

    }

    /**
     * Test all the different enums to make sure they work with there values
     * @throws Exception
     */
    public void testEachEnumValues_BitFieldId() throws Exception
    {
        BitFieldId bit;
        ByteBuffer buff1;
        ByteBuffer buff2;
        ByteBuffer buff4;
        ByteBuffer keyBuff;
        ByteBuffer resultBuff1;
        ByteBuffer resultBuff2;
        ByteBuffer resultBuff4;
        KeyObject key;
        int intValue = 5;
        buff1 = ByteBuffer.allocate(1);
        buff1.order(ByteOrder.LITTLE_ENDIAN);
        buff2 = ByteBuffer.allocate(2);
        buff2.order(ByteOrder.LITTLE_ENDIAN);
        buff4 = ByteBuffer.allocate(4);
        buff4.order(ByteOrder.LITTLE_ENDIAN);
        keyBuff = ByteBuffer.allocate(14);
        keyBuff.order(ByteOrder.LITTLE_ENDIAN);
        resultBuff1 = ByteBuffer.allocate(1);
        resultBuff1.order(ByteOrder.LITTLE_ENDIAN);
        resultBuff2 = ByteBuffer.allocate(2);
        resultBuff2.order(ByteOrder.LITTLE_ENDIAN);
        resultBuff4 = ByteBuffer.allocate(4);
        resultBuff4.order(ByteOrder.LITTLE_ENDIAN);
        buff1.put((byte)0x05);
        buff2.put((byte)0x05);
        buff4.put((byte)0x05);
        resultBuff1.put((byte)intValue);
        resultBuff2.putShort((short)intValue);
        resultBuff4.putInt(intValue);


        //test KPH
        bit = BitFieldId.KPH;
        assertEquals(BitFieldId.KPH, bit);
        assertEquals(0, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(0.05, ((SpeedConverter)bit.getData(buff2)).getSpeed());
        resultBuff2.clear();
        resultBuff2.putShort((short)50);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Incline
        bit = BitFieldId.GRADE;
        assertEquals(BitFieldId.GRADE, bit);
        assertEquals(1, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(0.05, ((GradeConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Resistance
        bit = BitFieldId.RESISTANCE;
        assertEquals(BitFieldId.RESISTANCE, bit);
        assertEquals(2, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(0.05, ((ResistanceConverter)bit.getData(buff2)).getResistance());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Watts
        bit = BitFieldId.WATTS;
        assertEquals(BitFieldId.WATTS, bit);
        assertEquals(3, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Torque
        bit = BitFieldId.TORQUE;
        assertEquals(BitFieldId.TORQUE, bit);
        assertEquals(4, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.123));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test


        //test RPM
        bit = BitFieldId.RPM;
        assertEquals(BitFieldId.RPM, bit);
        assertEquals(5, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Distance
        bit = BitFieldId.DISTANCE;
        assertEquals(BitFieldId.DISTANCE, bit);
        assertEquals(6, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(4, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((LongConverter)bit.getData(buff4)).getValue());
        resultBuff4.clear();
        resultBuff4.putInt(5);
        assertEquals(resultBuff4, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff4, bit.getRawFromData(5));//int test

        //test Current Keycode
        bit = BitFieldId.KEY_OBJECT;
        keyBuff.putShort((short)KeyCodes.SPEED_UP.getVal());//stopKey
        keyBuff.putLong(0xFFFFFFFFFFFFCFFFL);//long value
        keyBuff.putShort((short) 123);
        keyBuff.putShort((short)321);

        assertEquals(BitFieldId.KEY_OBJECT, bit);
        assertEquals(7, bit.getVal());
        assertEquals(0, bit.getSection());
        assertEquals(7, bit.getBit());
        assertEquals(14, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        key = ((KeyObjectConverter) bit.getData(keyBuff)).getKeyObject();
        assertEquals(KeyCodes.SPEED_UP, key.getCookedKeyCode());
        assertEquals(0xFFFFFFFFFFFFCFFFL, key.getRawKeyCode());
        assertEquals(123, key.getTimePressed());
        assertEquals(321, key.getTimeHeld());

        //test Fan Speed
        bit = BitFieldId.FAN_SPEED;
        assertEquals(BitFieldId.FAN_SPEED, bit);
        assertEquals(8, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test target Volume
        bit = BitFieldId.VOLUME;
        assertEquals(BitFieldId.VOLUME, bit);
        assertEquals(9, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Pulse
        bit = BitFieldId.PULSE;
        assertEquals(BitFieldId.PULSE, bit);
        assertEquals(10, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Running Time
        bit = BitFieldId.RUNNING_TIME;
        assertEquals(BitFieldId.RUNNING_TIME, bit);
        assertEquals(11, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(4, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((LongConverter)bit.getData(buff4)).getValue());
        resultBuff4.clear();
        resultBuff4.putInt(5);
        assertEquals(resultBuff4, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff4, bit.getRawFromData(5));//int test

        //test WorkoutState
        bit = BitFieldId.WORKOUT_MODE;
        assertEquals(BitFieldId.WORKOUT_MODE, bit);
        assertEquals(12, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(ModeId.DEBUG, ((ModeConverter)bit.getData(buff1)).getMode());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Calories
        bit = BitFieldId.CALORIES;
        assertEquals(BitFieldId.CALORIES, bit);
        assertEquals(13, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(4, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.0005, ((CaloriesConverter)bit.getData(buff4)).getCalories());
        resultBuff4.clear();
        resultBuff4.putInt(5);
        assertEquals(resultBuff4, bit.getRawFromData(0.0005));//double test
        //assertEquals(resultBuff4, bit.getRawFromData(5));//int test

        //test Audio Source
        bit = BitFieldId.AUDIO_SOURCE;
        ByteBuffer tempAudioBuff = ByteBuffer.allocate(bit.getSize());
        tempAudioBuff.order(ByteOrder.LITTLE_ENDIAN);
        tempAudioBuff.put((byte) 5);
        tempAudioBuff.putShort((short) 0x0027);
        assertEquals(BitFieldId.AUDIO_SOURCE, bit);
        assertEquals(14, bit.getVal());
        assertEquals(1, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(3, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(AudioSourceId.TV, ((AudioSourceConverter)bit.getData(tempAudioBuff)).getAudioSource());
        ArrayList<AudioSourceId> supportedAudioSrcs = ((AudioSourceConverter)bit.getData(tempAudioBuff)).getSupportedAudioSrcs();
        assertTrue(supportedAudioSrcs.contains(AudioSourceId.FM));
        assertTrue(supportedAudioSrcs.contains(AudioSourceId.MP3));
        assertTrue(supportedAudioSrcs.contains(AudioSourceId.BRAIN_BOARD));
        assertTrue(supportedAudioSrcs.contains(AudioSourceId.PC));
        tempAudioBuff.clear();
        tempAudioBuff.put((byte) 5);
        tempAudioBuff.putShort((short)0x0000);
        assertEquals(tempAudioBuff, bit.getRawFromData(AudioSourceId.TV));//int test

        //test Actual KPH
        bit = BitFieldId.ACTUAL_KPH;
        assertEquals(BitFieldId.ACTUAL_KPH, bit);
        assertEquals(16, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((SpeedConverter)bit.getData(buff2)).getSpeed());
        resultBuff2.clear();
        resultBuff2.putShort((short)50);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Actual Incline
        bit = BitFieldId.ACTUAL_INCLINE;
        assertEquals(BitFieldId.ACTUAL_INCLINE, bit);
        assertEquals(17, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((GradeConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Actual Resistance
        bit = BitFieldId.ACTUAL_RESISTANCE;
        assertEquals(BitFieldId.ACTUAL_RESISTANCE, bit);
        assertEquals(18, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((ResistanceConverter)bit.getData(buff2)).getResistance());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test


        //test Actual Distance
        bit = BitFieldId.ACTUAL_DISTANCE;
        assertEquals(BitFieldId.ACTUAL_DISTANCE, bit);
        assertEquals(19, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(4, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(5, ((LongConverter)bit.getData(buff4)).getValue());
        resultBuff4.clear();
        resultBuff4.putInt(5);
        assertEquals(resultBuff4, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff4, bit.getRawFromData(5));//int test

        //test Workout
        bit = BitFieldId.WORKOUT;
        assertEquals(BitFieldId.WORKOUT, bit);
        assertEquals(20, bit.getVal());
        assertEquals(2, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(WorkoutId.SET_A_GOAL, ((WorkoutConverter)bit.getData(buff1)).getWorkout());
        resultBuff1.clear();
        resultBuff1.put((byte)5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Age
        bit = BitFieldId.AGE;
        assertEquals(BitFieldId.AGE, bit);
        assertEquals(24, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Weight
        bit = BitFieldId.WEIGHT;
        assertEquals(BitFieldId.WEIGHT, bit);
        assertEquals(25, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(0.05, ((WeightConverter)bit.getData(buff2)).getWeight());
        resultBuff2.clear();
        resultBuff2.putShort((short) 5);
        assertEquals(resultBuff2, bit.getRawFromData(0.05));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Target Gears
        bit = BitFieldId.GEARS;
        assertEquals(BitFieldId.GEARS, bit);
        assertEquals(26, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test Max Incline
        bit = BitFieldId.MAX_GRADE;
        assertEquals(BitFieldId.MAX_GRADE, bit);
        assertEquals(27, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((GradeConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Min Incline
        bit = BitFieldId.MIN_GRADE;
        assertEquals(BitFieldId.MIN_GRADE, bit);
        assertEquals(28, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(4, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((GradeConverter)bit.getData(buff2)).getIncline());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Incline Transmax
        bit = BitFieldId.TRANS_MAX;
        assertEquals(BitFieldId.TRANS_MAX, bit);
        assertEquals(29, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(5, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test


        //test Max KPH
        bit = BitFieldId.MAX_KPH;
        assertEquals(BitFieldId.MAX_KPH, bit);
        assertEquals(30, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(6, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((SpeedConverter)bit.getData(buff2)).getSpeed());
        resultBuff2.clear();
        resultBuff2.putShort((short)50);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Min KPH
        bit = BitFieldId.MIN_KPH;
        assertEquals(BitFieldId.MIN_KPH, bit);
        assertEquals(31, bit.getVal());
        assertEquals(3, bit.getSection());
        assertEquals(7, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(true, bit.getReadOnly());
        assertEquals(0.05, ((SpeedConverter)bit.getData(buff2)).getSpeed());
        resultBuff2.clear();
        resultBuff2.putShort((short)50);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test


        //test Current Broadcast Volume
        bit = BitFieldId.BV_VOLUME;
        assertEquals(BitFieldId.BV_VOLUME, bit);
        assertEquals(32, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(0, bit.getBit());
        assertEquals(1, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ByteConverter)bit.getData(buff1)).getValue());
        resultBuff1.clear();
        resultBuff1.put((byte) 5);
        assertEquals(resultBuff1, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff1, bit.getRawFromData(5));//int test

        //test BroadCast frequency
        bit = BitFieldId.BV_FREQUENCY;
        assertEquals(BitFieldId.BV_FREQUENCY, bit);
        assertEquals(33, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(1, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 500);
        assertEquals(resultBuff2, bit.getRawFromData(500.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(500));//int test

        //test Idle Timeout
        bit = BitFieldId.IDLE_TIMEOUT;
        assertEquals(BitFieldId.IDLE_TIMEOUT, bit);
        assertEquals(34, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(2, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 5);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test

        //test Pause Timeout
        bit = BitFieldId.PAUSE_TIMEOUT;
        assertEquals(BitFieldId.PAUSE_TIMEOUT, bit);
        assertEquals(35, bit.getVal());
        assertEquals(4, bit.getSection());
        assertEquals(3, bit.getBit());
        assertEquals(2, bit.getSize());
        assertEquals(false, bit.getReadOnly());
        assertEquals(5, ((ShortConverter)bit.getData(buff2)).getValue());
        resultBuff2.clear();
        resultBuff2.putShort((short) 5);
        assertEquals(resultBuff2, bit.getRawFromData(5.0));//double test
        assertEquals(resultBuff2, bit.getRawFromData(5));//int test
    }

    /**
     * Runs through the Tests for the static Enum function calls
     * @throws Exception
     */
    public void testGetStatic_BitFieldId() throws Exception{
        //Test the static Get BitfieldId from ID
        try
        {
            BitFieldId idOne = BitFieldId.getBitFieldId(0);
            assertEquals(BitFieldId.KPH, idOne);
        }
        catch (Exception ex)
        {
            fail();//shouldn't throw an exception
        }

        try
        {
            BitFieldId.getBitFieldId(67000);
            fail();//should throw an exception before here
        }
        catch (InvalidBitFieldException ex)
        {
            assertTrue(true);//this should throw an exception
        }

        //Test the static Get BitfieldId from bit and section
        try
        {
            BitFieldId idOne = BitFieldId.getBitFieldId(0,1);
            assertEquals(BitFieldId.GRADE, idOne);
        }
        catch (Exception ex)
        {
            fail();//shouldn't throw an exception
        }

        try
        {
            BitFieldId.getBitFieldId(50, 3);
            fail();//should throw an exception before here
        }
        catch (InvalidBitFieldException ex)
        {
            assertTrue(true);//this should throw an exception
        }
    }

}