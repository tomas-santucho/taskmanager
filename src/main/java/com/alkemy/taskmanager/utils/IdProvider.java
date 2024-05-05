package com.alkemy.taskmanager.utils;

import java.security.SecureRandom;

public class IdProvider {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static long generateRandomId() {
        return Math.abs(RANDOM.nextLong());
    }
}
