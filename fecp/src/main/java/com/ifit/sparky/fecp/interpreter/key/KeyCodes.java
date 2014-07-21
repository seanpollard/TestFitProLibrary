/**
 * KeyCodes for what was pressed.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * keycode for what was pressed so every keycode can be handled.
 */
package com.ifit.sparky.fecp.interpreter.key;

public enum KeyCodes {
    NO_KEY(0, "Basic"),
    STOP(1, "Basic"),
    START(2, "Basic"),
    SPEED_UP(3, "Basic"),
    SPEED_DOWN(4, "Basic"),
    INCLINE_UP(5, "Basic"),
    INCLINE_DOWN(6, "Basic"),
    RESISTANCE_UP(7, "Basic"),
    RESISTANCE_DOWN(8, "Basic"),
    GEAR_UP(9, "Basic"),
    GEAR_DOWN(10, "Basic"),
    WEIGHT_UP(11, "Basic"),
    WEIGHT_DOWN(12, "Basic"),
    AGE_UP(13, "Basic"),
    AGE_DOWN(14, "Basic"),

    FAN_UP(50, "Fan"),
    FAN_DOWN(51, "Fan"),
    FAN_OFF(52, "Fan"),
    FAN_MANUAL(53, "Fan"),
    FAN_AUTO(54, "Fan"),
    FAN_1(55, "Fan"),
    FAN_2(56, "Fan"),
    FAN_3(57, "Fan"),
    FAN_4(58, "Fan"),
    FAN_5(59, "Fan"),

    PC_BACK(100, "Navigation"),
    PC_MENU(101, "Navigation"),
    PC_HOME(102, "Navigation"),
    KEYPAD(103, "Navigation"),
    DISPLAY(104, "Navigation"),
    ENTER(105, "Navigation"),
    UP(106, "Navigation"),
    DOWN(107, "Navigation"),
    LEFT(108, "Navigation"),
    RIGHT(109, "Navigation"),

    TV_POWER(120, "Tv"),
    TV_CHANNEL_UP(121, "Tv"),
    TV_CHANNEL_DOWN(122, "Tv"),
    TV_RECALL(123, "Tv"),
    TV_MENU(124, "Tv"),
    TV_SOURCE(125, "Tv"),
    TV_SEEK(126, "Tv"),
    TV_CLOSE_CAPTION(127, "Tv"),
    TV_VOLUME_UP(128, "Tv"),
    TV_VOLUME_DOWN(129, "Tv"),
    TV_MUTE(130, "Tv"),

    RIGHT_GEAR_UP(150, "Bike"),
    RIGHT_GEAR_DOWN(151, "Bike"),
    LEFT_GEAR_UP(152, "Bike"),
    LEFT_GEAR_DOWN(153, "Bike"),

    AUDIO_VOLUME_UP(200, "Audio"),
    AUDIO_VOLUME_DOWN(201, "Bike"),
    AUDIO_MUTE(202, "Bike"),
    AUDIO_EQUALIZER(203, "Bike"),
    AUDIO_SOURCE(204, "Bike"),

    NUMBER_PAD_0(300, "Number Pad"),
    NUMBER_PAD_1(301, "Number Pad"),
    NUMBER_PAD_2(302, "Number Pad"),
    NUMBER_PAD_3(303, "Number Pad"),
    NUMBER_PAD_4(304, "Number Pad"),
    NUMBER_PAD_5(305, "Number Pad"),
    NUMBER_PAD_6(306, "Number Pad"),
    NUMBER_PAD_7(307, "Number Pad"),
    NUMBER_PAD_8(308, "Number Pad"),
    NUMBER_PAD_9(309, "Number Pad"),
    NUMBER_PAD_STAR(310, "Number Pad"),
    NUMBER_PAD_DOT(311, "Number Pad"),
    NUMBER_PAD_HASH(312, "Number Pad"),
    NUMBER_PAD_OK(313, "Number Pad"),
    NUMBER_PAD_ENTER(314, "Number Pad"),


    ERGOFIT_TILT_FORWARD(400, "Ergo Fit Keys"),
    ERGOFIT_TILT_BACK(401, "Ergo Fit Keys"),
    ERGOFIT_UPRIGHT_UP(402, "Ergo Fit Keys"),
    ERGOFIT_UPRIGHT_DOWN(403, "Ergo Fit Keys"),
    ERGOFIT_MEMORY(404, "Ergo Fit Keys"),
    ERGOFIT_USER_1(405, "Ergo Fit Keys"),
    ERGOFIT_USER_2(406, "Ergo Fit Keys"),
    ERGOFIT_USER_3(407, "Ergo Fit Keys"),
    ERGOFIT_USER_4(408, "Ergo Fit Keys"),

    SET_TO_SHIP(500, "Maintenance"),
    DEBUG_MODE(501, "Maintenance"),
    LOG_MODE(502, "Maintenance"),

    MPH_1(1000, "Speed"),
    MPH_2(1001, "Speed"),
    MPH_3(1002, "Speed"),
    MPH_4(1003, "Speed"),
    MPH_5(1004, "Speed"),
    MPH_6(1005, "Speed"),
    MPH_7(1006, "Speed"),
    MPH_8(1007, "Speed"),
    MPH_9(1008, "Speed"),
    MPH_10(1009, "Speed"),
    MPH_11(1010, "Speed"),
    MPH_12(1011, "Speed"),
    MPH_13(1012, "Speed"),
    MPH_14(1013, "Speed"),
    MPH_15(1014, "Speed"),

    KPH_1(1100, "Speed"),
    KPH_2(1101, "Speed"),
    KPH_3(1102, "Speed"),
    KPH_4(1103, "Speed"),
    KPH_5(1104, "Speed"),
    KPH_6(1105, "Speed"),
    KPH_7(1106, "Speed"),
    KPH_8(1107, "Speed"),
    KPH_9(1108, "Speed"),
    KPH_10(1109, "Speed"),
    KPH_11(1110, "Speed"),
    KPH_12(1111, "Speed"),
    KPH_13(1112, "Speed"),
    KPH_14(1113, "Speed"),
    KPH_15(1114, "Speed"),
    KPH_16(1115, "Speed"),
    KPH_17(1116, "Speed"),
    KPH_18(1117, "Speed"),
    KPH_19(1118, "Speed"),
    KPH_20(1119, "Speed"),
    KPH_21(1120, "Speed"),
    KPH_22(1121, "Speed"),
    KPH_23(1122, "Speed"),
    KPH_24(1123, "Speed"),

    INCLINE_NEG_30(1200, "Incline"),
    INCLINE_NEG_29(1201, "Incline"),
    INCLINE_NEG_28(1202, "Incline"),
    INCLINE_NEG_27(1203, "Incline"),
    INCLINE_NEG_26(1204, "Incline"),
    INCLINE_NEG_25(1205, "Incline"),
    INCLINE_NEG_24(1206, "Incline"),
    INCLINE_NEG_23(1207, "Incline"),
    INCLINE_NEG_22(1208, "Incline"),
    INCLINE_NEG_21(1209, "Incline"),
    INCLINE_NEG_20(1210, "Incline"),
    INCLINE_NEG_19(1211, "Incline"),
    INCLINE_NEG_18(1212, "Incline"),
    INCLINE_NEG_17(1213, "Incline"),
    INCLINE_NEG_16(1214, "Incline"),
    INCLINE_NEG_15(1215, "Incline"),
    INCLINE_NEG_14(1216, "Incline"),
    INCLINE_NEG_13(1217, "Incline"),
    INCLINE_NEG_12(1218, "Incline"),
    INCLINE_NEG_11(1219, "Incline"),
    INCLINE_NEG_10(1220, "Incline"),
    INCLINE_NEG_9(1221, "Incline"),
    INCLINE_NEG_8(1222, "Incline"),
    INCLINE_NEG_7(1223, "Incline"),
    INCLINE_NEG_6(1224, "Incline"),
    INCLINE_NEG_5(1225, "Incline"),
    INCLINE_NEG_4(1226, "Incline"),
    INCLINE_NEG_3(1227, "Incline"),
    INCLINE_NEG_2(1228, "Incline"),
    INCLINE_NEG_1(1229, "Incline"),
    INCLINE_0(1230, "Incline"),
    INCLINE_1(1231, "Incline"),
    INCLINE_2(1232, "Incline"),
    INCLINE_3(1233, "Incline"),
    INCLINE_4(1234, "Incline"),
    INCLINE_5(1235, "Incline"),
    INCLINE_6(1236, "Incline"),
    INCLINE_7(1237, "Incline"),
    INCLINE_8(1238, "Incline"),
    INCLINE_9(1239, "Incline"),
    INCLINE_10(1240, "Incline"),
    INCLINE_11(1241, "Incline"),
    INCLINE_12(1242, "Incline"),
    INCLINE_13(1243, "Incline"),
    INCLINE_14(1244, "Incline"),
    INCLINE_15(1245, "Incline"),
    INCLINE_16(1246, "Incline"),
    INCLINE_17(1247, "Incline"),
    INCLINE_18(1248, "Incline"),
    INCLINE_19(1249, "Incline"),
    INCLINE_20(1250, "Incline"),
    INCLINE_21(1251, "Incline"),
    INCLINE_22(1252, "Incline"),
    INCLINE_23(1253, "Incline"),
    INCLINE_24(1254, "Incline"),
    INCLINE_25(1255, "Incline"),
    INCLINE_26(1256, "Incline"),
    INCLINE_27(1257, "Incline"),
    INCLINE_28(1258, "Incline"),
    INCLINE_29(1259, "Incline"),
    INCLINE_30(1260, "Incline"),
    INCLINE_31(1261, "Incline"),
    INCLINE_32(1262, "Incline"),
    INCLINE_33(1263, "Incline"),
    INCLINE_34(1264, "Incline"),
    INCLINE_35(1265, "Incline"),
    INCLINE_36(1266, "Incline"),
    INCLINE_37(1267, "Incline"),
    INCLINE_38(1268, "Incline"),
    INCLINE_39(1269, "Incline"),
    INCLINE_40(1270, "Incline"),
    INCLINE_41(1271, "Incline"),
    INCLINE_42(1272, "Incline"),
    INCLINE_43(1273, "Incline"),
    INCLINE_44(1274, "Incline"),
    INCLINE_45(1275, "Incline"),
    INCLINE_46(1276, "Incline"),
    INCLINE_47(1277, "Incline"),
    INCLINE_48(1278, "Incline"),
    INCLINE_49(1279, "Incline"),
    INCLINE_50(1280, "Incline"),

    RESISTANCE_0(1300, "Resistance"),
    RESISTANCE_1(1301, "Resistance"),
    RESISTANCE_2(1302, "Resistance"),
    RESISTANCE_3(1303, "Resistance"),
    RESISTANCE_4(1304, "Resistance"),
    RESISTANCE_5(1305, "Resistance"),
    RESISTANCE_6(1306, "Resistance"),
    RESISTANCE_7(1307, "Resistance"),
    RESISTANCE_8(1308, "Resistance"),
    RESISTANCE_9(1309, "Resistance"),
    RESISTANCE_10(1310, "Resistance"),
    RESISTANCE_11(1311, "Resistance"),
    RESISTANCE_12(1312, "Resistance"),
    RESISTANCE_13(1313, "Resistance"),
    RESISTANCE_14(1314, "Resistance"),
    RESISTANCE_15(1315, "Resistance"),
    RESISTANCE_16(1316, "Resistance"),
    RESISTANCE_17(1317, "Resistance"),
    RESISTANCE_18(1318, "Resistance"),
    RESISTANCE_19(1319, "Resistance"),
    RESISTANCE_20(1320, "Resistance"),
    RESISTANCE_21(1321, "Resistance"),
    RESISTANCE_22(1322, "Resistance"),
    RESISTANCE_23(1323, "Resistance"),
    RESISTANCE_24(1324, "Resistance"),
    RESISTANCE_25(1325, "Resistance"),
    RESISTANCE_26(1326, "Resistance"),
    RESISTANCE_27(1327, "Resistance"),
    RESISTANCE_28(1328, "Resistance"),
    RESISTANCE_29(1329, "Resistance"),
    RESISTANCE_30(1330, "Resistance"),

    //todo  need to continue to add more keycodes stopped for sanity sake

    DUMMY(9999, "Maintenance")
    ;


    private int mKeyValue;
    private String mCategory;

    /**
     * Constructor for all keycodes
     * @param keyValue the key value
     * @param category what category it is a part of
     */
    KeyCodes(int keyValue, String category)
    {
        this.mKeyValue = keyValue;
        this.mCategory = category;
    }

    /**
     * Gets the keycode value.
     * @return the keyCode value
     */
    public int getVal()
    {
        return this.mKeyValue;
    }

    /**
     * gets the Category the Keycode is in.
     * @return the category
     */
    public String getCategory()
    {
        return this.mCategory;
    }

    /**
     * Gets the Keycode based on the value
     * @param value the KeyCode value
     * @return the Keycode
     * @throws InvalidKeyCodeException if Keycode doesn't exist throw
     */
    public static KeyCodes getKeyCode(int value) throws InvalidKeyCodeException
    {
        //go through all Keycodes and if it equals then return it.

        for (KeyCodes devId : KeyCodes.values())
        {
            if(value == devId.getVal())
            {
                return devId; // the Keycode
            }
        }

        //error throw exception
        throw new InvalidKeyCodeException(value);
    }

}
