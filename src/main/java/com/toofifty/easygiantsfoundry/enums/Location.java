package com.toofifty.easygiantsfoundry.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Location {
    GIANTS_FOUNDRY("Giants' Foundry"),
    EVERYWHERE("Everywhere"),
    NOWHERE("Nowhere");

    private final String type;

    @Override
    public String toString() {
        return type;
    }
}
