package main;

import java.io.File;
import java.nio.file.Files;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static main.TrayWidget.*;

public class AES {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SALT = "THIS IS SOME SALT";

    /**
     * encrypt data and save it to a file, the iv file should already exist and
     * contain a random iv.
     * 
     * @param filename file to write the data to (also shares name with the iv file)
     * @param data     data to encrypt
     * @param key      key to encrypt with
     */
    public static void encrypt(String filename, String data, String keyString) {
        try {
            SecretKey key = getKeyFromPassword(keyString);

            IvParameterSpec iv = new IvParameterSpec(
                    Files.readAllBytes(new File(IV_FOLDER + "/" + filename + IV_FILE_TYPE).toPath()));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            Files.write(new File(PASSWORDS_FOLDER + "/" + filename + PASSWORD_FILE_TYPE).toPath(),
                    cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
        }
    }

    /**
     * decrypts data from a file and returns it, the iv file should already exist
     * @param filename file to decrypt
     * @param keyString key to decrypt with
     * @return decrypted data
     */
    public static String decrypt(String filename, String keyString) {
        try {
            SecretKey key = getKeyFromPassword(keyString);

            IvParameterSpec iv = new IvParameterSpec(
                    Files.readAllBytes(new File(IV_FOLDER + "/" + filename + IV_FILE_TYPE).toPath()));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] plainText = cipher.doFinal(
                    Files.readAllBytes(new File(PASSWORDS_FOLDER + "/" + filename + PASSWORD_FILE_TYPE).toPath()));

            return new String(plainText);
        } catch (Exception e) {
        }
        return null;
    }

    private static SecretKey getKeyFromPassword(String password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return secret;
        } catch (Exception e) {
        }
        return null;
    }
}
