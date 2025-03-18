package org.imperial.fastquantanalysis.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generate random strings with length of 10
 *
 * @author Emil S. He
 * @since 2025-03-17
 */
@Component
public class RandomStringGenerator {
    private static final int NUMBER_OF_CHAR = 10;
    private static final String ALL_AVAILABLE_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String generateRandomString() {
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random(42L);

        for (int i = 0; i < NUMBER_OF_CHAR; i++) {
            sb.append(ALL_AVAILABLE_CHAR.charAt(rnd.nextInt(ALL_AVAILABLE_CHAR.length())));
        }

        return sb.toString();
    }
}
