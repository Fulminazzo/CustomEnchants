package it.fulminazzo.customenchants.utils;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class EventUtils {
    private static final String[] INVALID_EVENTS = new String[]{"EntityEvent", "VehicleEvent",
            "BlockShearEntityEvent", "BatToggleSleepEvent", "CreatureSpawnEvent", "CreeperPowerEvent", "EnderDragonChangePhaseEvent", "EntityBreakDoorEvent",
            "EntityBreedEvent", "EntityDropItemEvent", "EntityEnterBlockEvent", "EntityEnterLoveModeEvent", "EntityExplodeEvent", "EntitySpawnEvent",
            "EntitySpellCastEvent", "EntityTameEvent", "EntityTargetEvent", "EntityTargetLivingEntityEvent", "EntityTransformEvent", "EntityUnleashEvent",
            "ExplosionPrimeEvent", "HorseJumpEvent", "ItemDespawnEvent", "ItemMergeEvent", "ItemSpawnEvent", "LingeringPotionSplashEvent", "PigZapEvent",
            "PiglinBarterEvent", "ProjectileLaunchEvent", "SheepRegrowWoolEvent", "SlimeSplitEvent", "SpawnerSpawnEvent", "StriderTemperatureChangeEvent",
            "VillagerAcquireTradeEvent", "VillagerCareerChangeEvent", "VillagerReplenishTradeEvent", "HangingEvent", "RaidSpawnWaveEvent",
            "VehicleBlockCollisionEvent", "VehicleCollisionEvent", "VehicleCreateEvent", "VehicleMoveEvent", "VehicleUpdateEvent", "LootGenerateEvent"};

    public static boolean isClassEventPlayer(@Nullable Class<?> clazz) {
        if (clazz == null) return false;
        if (!Event.class.isAssignableFrom(clazz)) return false;
        if (Arrays.stream(INVALID_EVENTS).anyMatch(c -> c.equals(clazz.getSimpleName()))) return false;
        if (isClassEventPlayerForSure(clazz)) return true;
        return !ReflectionUtils.getFields(clazz, Entity.class).isEmpty();
    }

    public static boolean isClassEventPlayerForSure(@Nullable Class<?> clazz) {
        if (clazz == null) return false;
        if (!Event.class.isAssignableFrom(clazz)) return false;
        if (Arrays.stream(INVALID_EVENTS).anyMatch(c -> c.equals(clazz.getSimpleName()))) return false;
        if (InventoryEvent.class.isAssignableFrom(clazz)) return true;
        return PlayerEvent.class.isAssignableFrom(clazz);
    }
}
