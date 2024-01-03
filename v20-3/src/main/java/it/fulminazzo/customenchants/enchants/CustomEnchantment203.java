package it.fulminazzo.customenchants.enchants;

import it.fulminazzo.customenchants.handlers.EventHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
class CustomEnchantment203 extends Enchantment implements CustomEnchantment {
    private final JavaPlugin plugin;
    private final @NotNull NamespacedKey key;
    private final @NotNull String name;
    private int startLevel = 1;
    private int maxLevel;
    private boolean treasure = false;
    private boolean cursed = false;
    private @NotNull EnchantmentTarget itemTarget;
    private final @NotNull List<Enchantment> conflicts;
    private final @NotNull HashMap<String, EventHandler<?>> eventHandlers;

    public CustomEnchantment203(JavaPlugin plugin, @NotNull String name, int maxLevel, @NotNull EnchantmentTarget itemTarget, Enchantment @Nullable ... conflicts) {
        super();
        this.key = NamespacedKey.minecraft(name);
        this.plugin = plugin;
        this.name = name.toUpperCase();
        this.maxLevel = maxLevel;
        this.itemTarget = itemTarget;
        this.conflicts = new ArrayList<>();
        this.eventHandlers = new HashMap<>();
        if (conflicts != null) addConflicts(conflicts);
        CUSTOM_ENCHANTMENTS.add(this);
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public boolean conflictsWith(@Nullable Enchantment enchantment) {
        return CustomEnchantment.super.conflictsWith(enchantment);
    }

    @Override
    public boolean canEnchantItem(@Nullable ItemStack itemStack) {
        return CustomEnchantment.super.canEnchantItem(itemStack);
    }
}
