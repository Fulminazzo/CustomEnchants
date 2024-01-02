package it.fulminazzo.customenchants.enchants;

import it.fulminazzo.customenchants.utils.VersionUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface CustomEnchantment {
    int CRITICAL_VERSION = 13;
    List<CustomEnchantment> CUSTOM_ENCHANTMENTS = new ArrayList<>();

    default void addConflicts(Enchantment @Nullable ... enchantments) {
        if (enchantments != null)
            for (Enchantment e : enchantments)
                if (!getConflicts().contains(e)) getConflicts().add(e);
    }

    default boolean conflictsWith(@Nullable Enchantment other) {
        return other != null && getConflicts().contains(other);
    }

    default boolean canEnchantItem(@Nullable ItemStack item) {
        return item != null && getItemTarget().includes(item);
    }

    default boolean isRegistered() {
        return Enchantment.getByName(getName()) != null;
    }

    default void register() {
        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(Enchantment.class, true);
            Enchantment.registerEnchantment((Enchantment) this);
            field.set(Enchantment.class, false);
            field.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default void unregister() {
        try {
            if (!isRegistered()) return;
            String methodName = VersionUtils.is1_(CRITICAL_VERSION) ? "Key" : "Id";
            Field f = Enchantment.class.getDeclaredField("by" + methodName);
            f.setAccessible(true);
            Map<?, Enchantment> byId = (Map<?, Enchantment>) f.get(Enchantment.class);
            byId.remove(Enchantment.class.getMethod("get" + methodName).invoke(this));
            f = Enchantment.class.getDeclaredField("byName");
            f.setAccessible(true);
            Map<String, Enchantment> byName = (Map<String, Enchantment>) f.get(Enchantment.class);
            byName.remove(getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String getName();

    int getId();

    void setStartLevel(int startLevel);

    int getStartLevel();

    EnchantmentTarget getItemTarget();

    boolean isTreasure();

    boolean isCursed();

    List<Enchantment> getConflicts();

    static void registerAll() {
        for (CustomEnchantment enchantment : CUSTOM_ENCHANTMENTS)
            if (!enchantment.isRegistered()) enchantment.register();
    }

    static void unregisterAll() {
        for (CustomEnchantment enchantment : CUSTOM_ENCHANTMENTS)
            if (enchantment.isRegistered()) enchantment.unregister();
    }
}
