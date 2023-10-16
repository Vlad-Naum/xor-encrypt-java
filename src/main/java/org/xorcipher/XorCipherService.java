package org.xorcipher;

public class XorCipherService {

    public static final String PASSWORD = "password";

    public static String encrypt(String inputString) {
        StringBuilder outputString = new StringBuilder();
        for (int i = 0; i < inputString.length(); i++) {
            outputString.append((char) (inputString.charAt(i) ^ PASSWORD.charAt(i % (PASSWORD.length()))));
        }
        return outputString.toString();
    }
}
