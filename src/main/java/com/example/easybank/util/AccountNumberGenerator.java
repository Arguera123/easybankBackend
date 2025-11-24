package com.example.easybank.util;

import java.util.Random;

public class AccountNumberGenerator {

    private static final int ACCOUNT_NUMBER_LENGTH = 12;

    public static String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(new Random().nextInt(9) + 1);
        Random random = new Random();
        while (sb.length() < ACCOUNT_NUMBER_LENGTH) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
