package com.example.enterpriseapi.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class PropertyEncryptionUtil {
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: PropertyEncryptionUtil <encryption-password> <value-to-encrypt>");
            System.exit(1);
        }

        String password = args[0];
        String valueToEncrypt = args[1];

        String encryptedValue = encrypt(password, valueToEncrypt);
        System.out.println("\nEncrypted value: ENC(" + encryptedValue + ")");
        
        // Verify decryption
        String decryptedValue = decrypt(password, encryptedValue);
        System.out.println("Decrypted value (verification): " + decryptedValue + "\n");
    }

    public static String encrypt(String password, String value) {
        PooledPBEStringEncryptor encryptor = createEncryptor(password);
        return encryptor.encrypt(value);
    }

    public static String decrypt(String password, String encryptedValue) {
        PooledPBEStringEncryptor encryptor = createEncryptor(password);
        return encryptor.decrypt(encryptedValue);
    }

    private static PooledPBEStringEncryptor createEncryptor(String password) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        
        config.setPassword(password);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        
        encryptor.setConfig(config);
        return encryptor;
    }
} 