package it.fulminazzo.customenchants.enums;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public enum SingleHandler {
    PLAYER_ITEM_HELD_EVENT(PlayerItemHeldEvent.class, (event, itemStacks) -> {
        Player player = event.getPlayer();
        itemStacks.add(player.getInventory().getItem(event.getPreviousSlot()));
        itemStacks.add(player.getInventory().getItem(event.getNewSlot()));
    }),
    PLAYER_DROP_ITEM_EVENT(PlayerDropItemEvent.class, (event, itemStacks) -> itemStacks.add(event.getItemDrop().getItemStack())),
    ENTITY_PICKUP_ITEM_EVENT(EntityPickupItemEvent.class, (event, itemStacks) -> itemStacks.add(event.getItem().getItemStack())),
    INVENTORY_CLICK_EVENT(InventoryClickEvent.class, (event, itemStacks) -> {
        itemStacks.add(event.getCurrentItem());
        itemStacks.add(event.getCursor());
    }),
    INVENTORY_CREATIVE_EVENT(InventoryCreativeEvent.class, (event, itemStacks) -> {
        itemStacks.add(event.getCurrentItem());
        itemStacks.add(event.getCursor());
    })
    ;

    private final Class<? extends Event> eventClass;
    private final BiConsumer<Event, List<ItemStack>> handler;

    <T extends Event> SingleHandler(Class<T> eventClass, BiConsumer<T, List<ItemStack>> handler) {
        this.eventClass = eventClass;
        this.handler = (BiConsumer<Event, List<ItemStack>>) handler;
    }

    public static void handleEvent(@NotNull Event event, List<ItemStack> items) {
        for (SingleHandler singleHandler : values())
            if (event.getClass().equals(singleHandler.eventClass)) {
                singleHandler.handler.accept(event, items);
                break;
            }
    }
}