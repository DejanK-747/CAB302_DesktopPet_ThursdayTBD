package com.cab302thursdaytbd.Service;

import java.security.MessageDigest;

public class PasswordService {
    // Converts a plain text password into a secure SHA-256 hash
    public static String hashPassword(String password) {
        try {
            // Creates SHA-256 hashing object
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Converts the password into bytes and hash it
            byte[] hashBytes = md.digest(password.getBytes());

            // Builds the final hexadecimal hash string
            StringBuilder hexString = new StringBuilder();
            // Convert each hashed byte into a 2-digit hexadecimal value
            for (byte b : hashBytes) {
                // %02x formats the byte as hexadecimal
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        // If hashing fails, stop the program and display the error
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}