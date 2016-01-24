package com.mybus.model;

/**
 * Created by schanda on 01/14/16.
 */

public enum LayoutType {

    SLEEPER,
    AC_SEMI_SLEEPER,
    NON_AC_SEMI_SLEEPER;

    @Override
    public String toString() {
        return name();
    }
}
