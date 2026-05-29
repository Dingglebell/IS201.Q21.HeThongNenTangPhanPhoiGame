package com.gameplatform.model;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.Locale;

public final class VietnameseText {
    private static final Charset WINDOWS_1252 = Charset.forName("windows-1252");

    private VietnameseText() {
    }

    public static String repair(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (looksMojibake(trimmed)) {
            return new String(trimmed.getBytes(WINDOWS_1252), StandardCharsets.UTF_8).trim();
        }
        return trimmed;
    }

    public static boolean equalsDbText(String left, String right) {
        return canonical(left).equals(canonical(right));
    }

    public static String canonical(String value) {
        String repaired = repair(value);
        if (repaired == null) {
            return "";
        }
        String normalized = Normalizer.normalize(repaired, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('Đ', 'D')
                .replace('đ', 'd');
        return normalized.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private static boolean looksMojibake(String value) {
        return value.contains("Ã")
                || value.contains("Æ")
                || value.contains("Ä")
                || value.contains("Â")
                || value.contains("áº")
                || value.contains("á»");
    }
}


