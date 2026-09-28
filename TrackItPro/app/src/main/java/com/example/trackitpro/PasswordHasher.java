package com.example.trackitpro;

import java.security.SecureRandom;
import android.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;

    //create salt for new password
    public static String createSalt()
    {
        byte[] salt = new byte[SALT_LENGTH];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    //hash password using salt
    public static String hashPassword(String password, String salt)
    {
        try
        {
            byte[] saltBytes = Base64.decode(salt, Base64.NO_WRAP);

            PBEKeySpec keySpec = new PBEKeySpec(
                    password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hashedPassword = keyFactory.generateSecret(keySpec).getEncoded();

            keySpec.clearPassword();

            return Base64.encodeToString(hashedPassword, Base64.NO_WRAP);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    //check entered password against saved hash
    public static boolean verifyPassword(String password, String storedHash, String salt)
    {
        String enteredHash = hashPassword(password, salt);

        return slowEquals(storedHash, enteredHash);
    }

    //compare hashes without stopping at first difference
    private static boolean slowEquals(String firstHash, String secondHash)
    {
        byte[] firstBytes = Base64.decode(firstHash, Base64.NO_WRAP);
        byte[] secondBytes = Base64.decode(secondHash, Base64.NO_WRAP);

        if (firstBytes.length != secondBytes.length)
            return false;

        int differences = 0;

        for (int i = 0; i < firstBytes.length; i++)
        {
            differences |= firstBytes[i] ^ secondBytes[i];
        }

        return differences == 0;
    }
}
