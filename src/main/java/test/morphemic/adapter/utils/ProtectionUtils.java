package test.morphemic.adapter.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class ProtectionUtils {

    public static final StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();

    public static final String PASSWORD = "PhemicMor20.";

    private static boolean isSet = false;

    private ProtectionUtils() {
    }

    public static String decrypt(String encryptedText) {
        if (!isSet) {
            textEncryptor.setPassword(PASSWORD);
            isSet = true;
        }
        return textEncryptor.decrypt(encryptedText);
    }

    public static String encrypt(String plainText) {
        if (!isSet) {
            textEncryptor.setPassword(PASSWORD);
            isSet = true;
        }
        return textEncryptor.encrypt(plainText);
    }

}
