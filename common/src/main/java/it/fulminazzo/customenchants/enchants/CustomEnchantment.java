package it.fulminazzo.customenchants.enchants;

import it.fulminazzo.customenchants.exceptions.EventHandlerAlreadyPresent;
import it.fulminazzo.customenchants.exceptions.NotValidEventClass;
import it.fulminazzo.customenchants.handlers.EventHandler;
import it.fulminazzo.customenchants.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public interface CustomEnchantment {
    int CRITICAL_VERSION = 13;
    double CRITICAL_VERSION2 = 20.3;
    List<CustomEnchantment> CUSTOM_ENCHANTMENTS = new ArrayList<>();

    default <T extends Event> EventHandler<T> getEventHandler(@NotNull String name) {
        return (EventHandler<T>) getEventHandlers().get(name.toLowerCase());
    }

    default void addEventHandler(String name, @NotNull EventHandler<?> eventHandler) {
        name = name.toLowerCase();
        if (getEventHandler(name) != null) throw new EventHandlerAlreadyPresent(name);
        if (!EventUtils.isClassEventPlayer(eventHandler.getEvent()))
            throw new NotValidEventClass(eventHandler.getEvent());
        getEventHandlers().put(name, eventHandler);
        registerEventHandlers();
    }

    default void removeEventHandler(@NotNull String name) {
        getEventHandlers().remove(name.toLowerCase());
    }

    default void removeEventHandler(EventHandler<?> eventHandler) {
        HashMap<String, EventHandler<?>> eventHandlers = getEventHandlers();
        eventHandlers.keySet().stream()
                .filter(k -> eventHandlers.get(k).equals(eventHandler))
                .forEach(k -> {
                    eventHandlers.get(k).unregister();
                    eventHandlers.remove(k);
                });
    }

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
            if (VersionUtils.is1_(CRITICAL_VERSION2)) {
                Map<?, Enchantment> cache = ReflectionUtils.getField(Registry.ENCHANTMENT.getClass(), "cache", Bukkit.getRegistry(Enchantment.class));
                cache.put(getKey(), (Enchantment) this);
                return;
            }
            Field field = ReflectionUtils.getField(Enchantment.class, "acceptingNew");
            field.set(Enchantment.class, true);
            Enchantment.registerEnchantment((Enchantment) this);
            field.set(Enchantment.class, false);
            field.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default void registerEventHandlers() {
        if (getPlugin() != null)
            getEventHandlers().values().stream()
                    .filter(e -> !e.isRegistered())
                    .forEach(e -> e.register(this, getPlugin()));
    }

    default void unregister() {
        if (!isRegistered()) return;
        if (VersionUtils.is1_(CRITICAL_VERSION2)) {
            Map<?, Enchantment> cache = ReflectionUtils.getField(Registry.ENCHANTMENT.getClass(), "cache", Registry.ENCHANTMENT);
            cache.remove(getKey());
            return;
        }
        String methodName = VersionUtils.is1_(CRITICAL_VERSION) ? "Key" : "Id";
        Map<?, Enchantment> byId = ReflectionUtils.getField(Enchantment.class, "by" + methodName, Enchantment.class);
        try {
            byId.remove(Enchantment.class.getMethod("get" + methodName).invoke(this));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Map<String, Enchantment> byName = ReflectionUtils.getField(Enchantment.class, "byName", Enchantment.class);
        byName.remove(getName());
    }

    default void unregisterEventHandlers() {
        getEventHandlers().values().forEach(EventHandler::unregister);
    }

    /**
     * Recommended way of applying a CustomEnchantment to an item.
     *
     * @param itemStack the item
     */
    default void apply(@Nullable ItemStack itemStack, int level) {
        if (itemStack == null) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        EnchantedItemUtils.removeEnchantment(itemStack, this);
        itemMeta.addEnchant((Enchantment) this, level, true);
        itemStack.setItemMeta(itemMeta);
        EnchantedItemUtils.editLore(itemStack, getLoreName("[MCDXLIV]+"), getLoreName(level));
    }

    default @NotNull String getLoreName(int level) {
        return getLoreName(EnchantedItemUtils.toRoman(level));
    }

    default @NotNull String getLoreName(String level) {
        return StringUtils.color(String.format("&7%s %s", StringUtils.capitalize(getName()), level));
    }

    String getName();

    <T> @Nullable T getKey();

    int getId();

    void setStartLevel(int startLevel);

    int getStartLevel();

    void setMaxLevel(int startLevel);

    int getMaxLevel();

    void setItemTarget(EnchantmentTarget itemTarget);

    EnchantmentTarget getItemTarget();

    void setTreasure(boolean treasure);

    boolean isTreasure();

    void setCursed(boolean treasure);

    boolean isCursed();

    List<Enchantment> getConflicts();

    HashMap<String, EventHandler<?>> getEventHandlers();

    JavaPlugin getPlugin();

    static void registerAll() {
        for (CustomEnchantment enchantment : CUSTOM_ENCHANTMENTS)
            if (!enchantment.isRegistered()) enchantment.register();
    }

    static void unregisterAll() {
        for (CustomEnchantment enchantment : CUSTOM_ENCHANTMENTS)
            if (enchantment.isRegistered()) enchantment.unregister();
    }
}
