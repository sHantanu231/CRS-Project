package com.crs.utils;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class PlagiarismChecker {

    public static String getFileHash(String filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            FileInputStream fis = new FileInputStream(filePath);
            byte[] dataBytes = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, bytesRead);
            }

            fis.close();

            byte[] hashBytes = md.digest();

            // convert to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}