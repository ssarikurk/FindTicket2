package org.cucumber.utilities;


import java.util.Random;

public class RandomUtils {

    public static String generateRandomAlphaNumeric(int randomLength) {
//        int randomLength = 3;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        StringBuilder sb = new StringBuilder(randomLength);

        for (int j = 0; j < randomLength; j++) {
            int index = random.nextInt(characters.length()); // Pick a random index
            sb.append(characters.charAt(index)); // Add the character at that index
        }
        return sb.toString();
    }





}





