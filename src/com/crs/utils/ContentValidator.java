package com.crs.utils;

import java.io.File;

public class ContentValidator {

    public static boolean isValid(String title, String subject, String filePath) {

        // Basic checks
        if (title == null || title.trim().length() < 3) return false;
        if (subject == null || subject.trim().isEmpty()) return false;

        if (filePath == null || filePath.isEmpty()) return false;

        String path = filePath.toLowerCase();

        // ✅ Allowed file types (NO .class)
        if (!(path.endsWith(".pdf") ||
              path.endsWith(".c") ||
              path.endsWith(".cpp") ||
              path.endsWith(".java") ||
              path.endsWith(".docx"))) {
            return false;
        }

        File file = new File(filePath);

        // File must exist
        if (!file.exists()) return false;

        // Size limit (5 MB)
        if (file.length() > 5 * 1024 * 1024) return false;

        return true;
    }
}