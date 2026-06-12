package com.cab302thursdaytbd.Service;

import java.security.MessageDigest;

/**
 * Provides password hashing functionality for user authentication.
 * Passwords are converted into SHA-256 hashes before being stored
 * or compared, ensuring that plain-text passwords are not saved
 * in the database.
 */
public class PasswordService {
    /**
     * Hashes a password using the SHA-256 cryptographic hashing algorithm.
     * The resulting hash is returned as a hexadecimal string that can
     * be safely stored in the database and used for authentication.
     * @param password The plain-text password to hash.
     * @return The SHA-256 hash represented as a hexadecimal string.
     * @throws RuntimeException If the hashing algorithm is unavailable.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}