package net.hydrogen2oxygen.utils;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;

public class StringUtil {

    public static boolean isPureAscii(String v) {
        return Charset.forName("UTF-8").newEncoder().canEncode(v);
        // or "ISO-8859-1" for ISO Latin 1
        // or StandardCharsets.US_ASCII with JDK1.7+
    }

    public static String cleanString(String text) {
        String cleanString = text;
        cleanString = StringUtils.stripAccents(cleanString);
        cleanString = toPlain(cleanString);


        cleanString = replacePart(cleanString, ": ");

        cleanString = replacePart(cleanString, ". ");

        return cleanString.trim();
    }

    private static String replacePart(String cleanString, String prefix) {
        if (cleanString.startsWith(prefix)) {
            cleanString = cleanString.replace(prefix,"");
        }
        return cleanString;
    }

    private static final String TAB_00C0 = "" +
            "AAAAAAACEEEEIIII" +
            "DNOOOOO×OUUUÜYTs" + // <-- note an accented letter you wanted and preserved multiplication sign
            "aaaaaaaceeeeiiii" +
            "dnooooo÷ouuuüyty" + // <-- note an accented letter and preserved division sign
            "AaAaAaCcCcCcCcDd" +
            "DdEeEeEeEeEeGgGg" +
            "GgGgHhHhIiIiIiIi" +
            "IiJjJjKkkLlLlLlL" +
            "lLlNnNnNnnNnOoOo" +
            "OoOoRrRrRrSsSsSs" +
            "SsTtTtTtUuUuUuUu" +
            "UuUuWwYyYZzZzZzs";

    private static String toPlain(String source) {
        StringBuilder sb = new StringBuilder(source.length());
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case 'Æ':
                    sb.append("AE");
                    break;
                case 'æ':
                    sb.append("ae");
                    break;
                case 'ß':
                    sb.append("ss");
                    break;
                case 'Œ':
                    sb.append("OE");
                    break;
                case 'œ':
                    sb.append("oe");
                    break;
                default:
                    if (c >= 0xc0 && c <= 0x17f) {
                        c = TAB_00C0.charAt(c - 0xc0);
                    }
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String removeNumbers(String category) {

        String parts [] = category.split(" ");
        StringBuilder str = new StringBuilder();

        for (String part : parts) {
            try {
                Integer.parseInt(part.replace(".","").trim());
            } catch (NumberFormatException e) {
                str.append(part + " ");
            }
        }

        return str.toString().trim();
    }
}
