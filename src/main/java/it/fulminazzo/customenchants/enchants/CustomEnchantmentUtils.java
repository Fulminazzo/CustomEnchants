package it.fulminazzo.customenchants.enchants;

import it.fulminazzo.customenchants.utils.VersionUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CustomEnchantmentUtils {

    public static @NotNull CustomEnchantment newInstance(JavaPlugin plugin, @NotNull String name, int maxLevel, Enchantment... conflicts) {
        return newInstance(plugin, name, maxLevel, EnchantmentTarget.ALL, conflicts);
    }

    public static @NotNull CustomEnchantment newInstance(JavaPlugin plugin, @NotNull String name, int maxLevel, @NotNull EnchantmentTarget itemTarget, Enchantment... conflicts) {
        if (VersionUtils.is1_(CustomEnchantment.CRITICAL_VERSION2)) return new CustomEnchantment203(plugin, name, maxLevel, itemTarget, conflicts);
        if (VersionUtils.is1_(CustomEnchantment.CRITICAL_VERSION)) return new CustomEnchantment13(plugin, name, maxLevel, itemTarget, conflicts);
        else return new CustomEnchantment12(plugin, name, maxLevel, itemTarget, conflicts);
    }
}
