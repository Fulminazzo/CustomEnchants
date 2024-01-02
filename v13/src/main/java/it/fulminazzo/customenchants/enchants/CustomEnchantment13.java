package it.fulminazzo.customenchants.enchants;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
class CustomEnchantment13 extends Enchantment implements CustomEnchantment {
    private final @NotNull String name;
    private int startLevel = 1;
    private final int maxLevel;
    private final @NotNull EnchantmentTarget itemTarget;
    private final @NotNull List<Enchantment> conflicts;

    public CustomEnchantment13(@NotNull String name, int maxLevel, @NotNull EnchantmentTarget itemTarget, Enchantment @Nullable ... conflicts) {
        super(NamespacedKey.minecraft(name));
        this.name = name.toUpperCase();
        this.maxLevel = maxLevel;
        this.itemTarget = itemTarget;
        this.conflicts = new ArrayList<>();
        if (conflicts != null) addConflicts(conflicts);
        CUSTOM_ENCHANTMENTS.add(this);
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return CustomEnchantment.super.conflictsWith(enchantment);
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return CustomEnchantment.super.canEnchantItem(itemStack);
    }
}
