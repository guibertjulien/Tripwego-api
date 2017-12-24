package com.tripwego.api.common;

import javax.annotation.Nullable;

/**
 * Created by JG on 25/12/17.
 */
public class Strings {
    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.length() == 0;
    }

    public static String nullToEmpty(@Nullable String string) {
        return string == null ? "" : string;
    }
}
