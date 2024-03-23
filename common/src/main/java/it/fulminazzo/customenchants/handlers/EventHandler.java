package it.fulminazzo.customenchants.handlers;

import it.fulminazzo.customenchants.enchants.CustomEnchantment;
import it.fulminazzo.customenchants.enchants.EnchantListener;
import it.fulminazzo.customenchants.enums.SingleHandler;
import it.fulminazzo.customenchants.utils.EnchantedItemUtils;
import it.fulminazzo.customenchants.utils.ReflectionUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class EventHandler<T extends Event> {
    @Getter
    private final Class<T> event;
    private final Consumer<T> runner;
    private Listener listener;

    public EventHandler(Class<T> event, Consumer<T> runner) {
        this.event = event;
        this.runner = runner;
    }

    public void apply(T t) {
        runner.accept(t);
    }

    public void register(CustomEnchantment enchantment, @NotNull JavaPlugin plugin) {
        if (!isRegistered()) {
            listener = new EnchantListener();
            Bukkit.getPluginManager().registerEvent(event, listener, EventPriority.NORMAL, (l, e) -> {
                if (!event.equals(e.getClass())) return;
                if (ReflectionUtils.getFieldsAndMethodsResults(e.getClass(), e, LivingEntity.class, Item.class, ItemStack.class).stream().distinct()
                        .anyMatch(en -> {
                            List<ItemStack> itemStacks = new ArrayList<>();
                            if (en instanceof Item) itemStacks.add(((Item) en).getItemStack());
                            else if (en instanceof LivingEntity) SingleHandler.handleEntity((Entity) en, itemStacks);
                            else itemStacks.add((ItemStack) en);
                            SingleHandler.handleEvent(e, itemStacks);
                            return itemStacks.stream()
                                    .filter(Objects::nonNull)
                                    .anyMatch(i -> EnchantedItemUtils.hasCustomEnchant(i, enchantment));
                        })) apply((T) e);
            }, plugin);
        }
    }

    public void unregister() {
        if (isRegistered()) HandlerList.unregisterAll(listener);
    }

    public boolean isRegistered() {
        return listener != null;
    }

    public boolean equals(Class<?> clazz) {
        return Objects.equals(clazz, event);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Class<?>) return equals((Class<?>) obj);
        return super.equals(obj);
    }
}