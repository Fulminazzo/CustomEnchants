package it.fulminazzo.customenchants.enchants;

import it.fulminazzo.customenchants.utils.VersionUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.jetbrains.annotations.NotNull;

public class CustomEnchantmentUtils {

    public static @NotNull CustomEnchantment newInstance(@NotNull String name, int maxLevel, Enchantment... conflicts) {
        return newInstance(name, maxLevel, EnchantmentTarget.ALL, conflicts);
    }

    public static @NotNull CustomEnchantment newInstance(@NotNull String name, int maxLevel, @NotNull EnchantmentTarget itemTarget, Enchantment... conflicts) {
        if (VersionUtils.is1_(CustomEnchantment.CRITICAL_VERSION)) return new CustomEnchantment13(name, maxLevel, itemTarget, conflicts);
        else return new CustomEnchantment12(name, maxLevel, itemTarget, conflicts);
    }
}
