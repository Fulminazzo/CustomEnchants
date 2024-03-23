package it.fulminazzo.customenchants.utils;

import it.fulminazzo.customenchants.enchants.CustomEnchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Enchanted item utils.
 */
public class EnchantedItemUtils {

    /**
     * Compare two items by removing their custom enchantments and calling {@link ItemStack#isSimilar(ItemStack)}.
     *
     * @param item1 the first item
     * @param item2 the second item
     * @return the boolean
     */
    public static boolean isSimilar(@Nullable ItemStack item1, @Nullable ItemStack item2) {
        if (item1 == null && item2 == null) return true;
        if (item1 == null || item2 == null) return false;
        for (CustomEnchantment enchantment : CustomEnchantment.CUSTOM_ENCHANTMENTS) {
            removeEnchantment(item1, enchantment);
            removeEnchantment(item2, enchantment);
        }
        return item1.isSimilar(item2);
    }

    /**
     * Check if the item has the specified custom enchantment.
     *
     * @param itemStack   the item stack
     * @param enchantment the enchantment
     * @return true if a level is found
     */
    public static boolean hasCustomEnchant(@NotNull ItemStack itemStack, @NotNull CustomEnchantment enchantment) {
        return getCustomEnchantLevel(itemStack, enchantment) != 0;
    }

    /**
     * Gets the current level of the specified custom enchantment.
     * If none is found, returns 0.
     *
     * @param itemStack   the item stack
     * @param enchantment the enchantment
     * @return the custom enchantment level
     */
    public static int getCustomEnchantLevel(@NotNull ItemStack itemStack, @NotNull CustomEnchantment enchantment) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return 0;
        return meta.getEnchants().keySet().stream()
                .filter(e -> e.getName().equals(enchantment.getName()))
                .map(meta::getEnchantLevel)
                .findFirst().orElse(0);
    }

    /**
     * Remove a custom enchantment from the specified item stack.
     *
     * @param itemStack   the item stack
     * @param enchantment the enchantment
     */
    public static void removeEnchantment(@Nullable ItemStack itemStack, @NotNull CustomEnchantment enchantment) {
        if (itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        new ArrayList<>(meta.getEnchants().keySet()).stream()
                .filter(e -> e.getName().equals(enchantment.getName()))
                .forEach(e -> {
                    editLore(meta, enchantment.getLoreName(meta.getEnchantLevel(e)), "");
                    meta.removeEnchant(e);
                });
        itemStack.setItemMeta(meta);
    }

    /**
     * Edit the lore of the specified item.
     * Check if any of the lore entries match with the lore regex.
     * If one matches, replace it with the new lore.
     *
     * @param itemStack the item stack
     * @param loreRegex the lore regex
     * @param newLore   the new lore
     */
    public static void editLore(@NotNull ItemStack itemStack, @NotNull String loreRegex, @NotNull String newLore) {
        ItemMeta meta = itemStack.getItemMeta();
        editLore(meta, loreRegex, newLore);
        itemStack.setItemMeta(meta);
    }

    /**
     * Edit the lore of the specified item.
     * Check if any of the lore entries match with the lore regex.
     * If one matches, replace it with the new lore.
     *
     * @param meta      the meta
     * @param loreRegex the lore regex
     * @param newLore   the new lore
     */
    public static void editLore(@Nullable ItemMeta meta, @NotNull String loreRegex, @NotNull String newLore) {
        if (meta == null) return;
        loreRegex = StringUtils.color(loreRegex);
        newLore = StringUtils.color(newLore);
        List<String> lore = meta.getLore();
        boolean changed = false;
        if (lore != null)
            for (int i = 0; i < lore.size(); i++)
                if (lore.get(i).matches(loreRegex)) {
                    if (newLore.isEmpty()) {
                        i = Math.max(0, i - 1);
                        lore.remove(i);
                    } else lore.set(i, newLore);
                    changed = true;
                }
        if (!changed && !newLore.trim().isEmpty()) {
            if (lore == null) lore = Collections.singletonList(newLore);
            else {
                if (!lore.get(lore.size() - 1).trim().isEmpty()) lore.add(" ");
                if (lore.stream().allMatch(String::isEmpty)) lore.clear();
                lore.add(newLore);
            }
        }
        meta.setLore(lore);
    }

    /**
     * Convert a number to roman format.
     *
     * @param number the number
     * @return the string
     */
    public static @NotNull String toRoman(int number) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanLetters = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder roman = new StringBuilder();
        for (int i = 0; i < values.length; i++)
            while(number >= values[i]) {
                number = number - values[i];
                roman.append(romanLetters[i]);
            }
        return roman.toString();
    }
}
