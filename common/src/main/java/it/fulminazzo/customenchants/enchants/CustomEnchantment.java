package it.fulminazzo.customenchants.enchants;

import it.fulminazzo.customenchants.exceptions.EventHandlerAlreadyPresent;
import it.fulminazzo.customenchants.exceptions.NotValidEventClass;
import it.fulminazzo.customenchants.handlers.EventHandler;
import it.fulminazzo.customenchants.utils.EventUtils;
import it.fulminazzo.customenchants.utils.ReflectionUtils;
import it.fulminazzo.customenchants.utils.VersionUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public interface CustomEnchantment {
    int CRITICAL_VERSION = 13;
    List<CustomEnchantment> CUSTOM_ENCHANTMENTS = new ArrayList<>();

    default <T extends Event> void handleEvent(T event) {
        if (!EventUtils.isClassEventPlayerForSure(event.getClass())) {
            if (ReflectionUtils.getFields(event.getClass(), Entity.class, event).stream().noneMatch(e -> e instanceof Player))
                return;
        }
        getEventHandlers(event.getClass())
                .stream().map(e -> (EventHandler<T>) e)
                .forEach(e -> e.apply(event));
    }

    default <T extends Event> EventHandler<T> getEventHandler(String name) {
        return (EventHandler<T>) getEventHandlers().get(name.toLowerCase());
    }

    default List<EventHandler<?>> getEventHandlers(Class<?> clazz) {
        return getEventHandlers().values().stream()
                .filter(c -> c.equals(clazz))
                .collect(Collectors.toList());
    }

    default void addEventHandler(String name, EventHandler<?> eventHandler) {
        name = name.toLowerCase();
        if (getEventHandler(name) != null) throw new EventHandlerAlreadyPresent(name);
        if (!EventUtils.isClassEventPlayer(eventHandler.getEvent()))
            throw new NotValidEventClass(eventHandler.getEvent());
        getEventHandlers().put(name, eventHandler);
    }

    default void removeEventHandler(String name) {
        getEventHandlers().remove(name.toLowerCase());
    }

    default void removeEventHandler(EventHandler<?> eventHandler) {
        HashMap<String, EventHandler<?>> eventHandlers = getEventHandlers();
        eventHandlers.keySet().stream()
                .filter(k -> eventHandlers.get(k).equals(eventHandler))
                .forEach(eventHandlers::remove);
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
            Field field = ReflectionUtils.getField(Enchantment.class, "acceptingNew");
            field.set(Enchantment.class, true);
            Enchantment.registerEnchantment((Enchantment) this);
            field.set(Enchantment.class, false);
            field.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default void unregister() {
        if (!isRegistered()) return;
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

    String getName();

    int getId();

    void setStartLevel(int startLevel);

    int getStartLevel();

    EnchantmentTarget getItemTarget();

    boolean isTreasure();

    boolean isCursed();

    List<Enchantment> getConflicts();

    HashMap<String, EventHandler<?>> getEventHandlers();

    static void registerAll() {
        for (CustomEnchantment enchantment : CUSTOM_ENCHANTMENTS)
            if (!enchantment.isRegistered()) enchantment.register();
    }

    static void unregisterAll() {
        for (CustomEnchantment enchantment : CUSTOM_ENCHANTMENTS)
            if (enchantment.isRegistered()) enchantment.unregister();
    }
}
