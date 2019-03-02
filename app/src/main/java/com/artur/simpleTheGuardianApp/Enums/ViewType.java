package com.artur.simpleTheGuardianApp.Enums;

/**
 * Created by artur on 02-Mar-19.
 */

public enum ViewType {

    LIST(1),
    PINTEREST(2);

    private final int type;

    ViewType(int type) {
        this.type = type;
    }

    public int type() {
        return type;
    }

}