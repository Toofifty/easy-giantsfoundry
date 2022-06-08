package com.toofifty.easygiantsfoundry.enums;

public enum CommisionType {
    NONE,
    NARROW, // 1
    LIGHT, // 2
    FLAT, // 3
    BROAD, // 4
    HEAVY, // 5
    SPIKED, // 6
    ;

    public static final CommisionType[] values = CommisionType.values();

    public static CommisionType forVarbit(int varbitValue) {
        if (varbitValue < 0 || varbitValue >= values.length) {
            return NONE;
        }
        return CommisionType.values[varbitValue];
    }
}
