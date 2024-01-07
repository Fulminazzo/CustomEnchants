package it.fulminazzo.customenchants.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String capitalize(@Nullable String string) {
        if (string == null) return null;
        string = string.toLowerCase();
        StringBuilder resultString = new StringBuilder();
        Matcher matcher = Pattern.compile("([^ \t\n\r_-]*)(.?)", Pattern.DOTALL).matcher(string);
        while (matcher.find()) {
            String s = matcher.group(1);
            if (!s.isEmpty()) resultString.append(s.substring(0, 1).toUpperCase()).append(s.length() > 1 ? s.substring(1) : "");
            if (matcher.end() != string.length()) resultString.append(" ");
        }
        return resultString.toString();
    }

    public static @NotNull String toSnakeCase(@NotNull String string) {
        StringBuilder result = new StringBuilder();
        for (String s : string.split("")) {
            if (s.matches("[A-Z \t\n\r_]") && (result.length() > 0) && !result.toString().endsWith("_")) {
                result.append("_");
                if (!s.matches("[A-Z]")) continue;
            } result.append(s.toLowerCase());
        }
        return result.toString();
    }

    public static String color(@Nullable String string) {
        return string == null ? null : ChatColor.translateAlternateColorCodes('&', string);
    }
}