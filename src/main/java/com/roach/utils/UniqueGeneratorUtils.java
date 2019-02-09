package com.roach.utils;

import java.util.UUID;

public class UniqueGeneratorUtils {

    public static String nextUniqueID() {
        return UUID.randomUUID().toString();
    }

}
