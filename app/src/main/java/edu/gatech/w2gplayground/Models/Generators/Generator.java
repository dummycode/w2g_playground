package edu.gatech.w2gplayground.Models.Generators;

import java.util.Random;

class Generator {

    private static Random random = new Random();

    /**
     * Returns a random integer in range [min, max)
     *
     * @param min minimum value
     * @param max maximum value
     *
     * @return random integer
     */
    static int randInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    /**
     * Generates a random alphanumeric string
     *
     * @param length length of the string
     *
     * @return random string
     */
    private static String randString(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    /**
     * Generates a random id of length 8
     *
     * @return random id
     */
    static String randId() {
        return randString(8);
    }

    /**
     * Generates a random upc string
     *
     * @return random upc string
     */
    static String randUpc() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 58; // numeral '9'

        // UPCs are 12 digits
        int targetStringLength = 12;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
