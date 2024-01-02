package it.fulminazzo.customenchants.utils;

import org.bukkit.Bukkit;

public class VersionUtils {

    public static boolean is1_(int version) {
        String serverVersion = Bukkit.getBukkitVersion().split("-")[0];
        serverVersion = serverVersion.substring(serverVersion.indexOf(".") + 1);
        serverVersion = serverVersion.substring(0, serverVersion.indexOf("."));
        return version <= Integer.parseInt(serverVersion);
    }
}
