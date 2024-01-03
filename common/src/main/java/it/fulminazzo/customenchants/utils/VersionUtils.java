package it.fulminazzo.customenchants.utils;

import org.bukkit.Bukkit;

public class VersionUtils {

    public static boolean is1_(double version) {
        String serverVersion = Bukkit.getBukkitVersion().split("-")[0];
        serverVersion = serverVersion.substring(serverVersion.indexOf(".") + 1);
        return version <= Double.parseDouble(serverVersion);
    }
}
