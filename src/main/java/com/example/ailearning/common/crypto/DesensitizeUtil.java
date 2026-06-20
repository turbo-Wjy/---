package com.example.ailearning.common.crypto;

public final class DesensitizeUtil {
    private DesensitizeUtil() {
    }

    public static String phone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    public static String email(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        String prefix = email.substring(0, atIndex);
        String maskedPrefix = prefix.length() <= 2 ? prefix.charAt(0) + "***" : prefix.substring(0, 2) + "***";
        return maskedPrefix + email.substring(atIndex);
    }
}
