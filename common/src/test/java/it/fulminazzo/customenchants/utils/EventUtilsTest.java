package it.fulminazzo.customenchants.utils;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventUtilsTest {

    @SuppressWarnings("unchecked")
    public static List<Class<? extends Event>> getValidEventClasses() {
        try {
            String[] invalidEvents = ReflectionUtils.getField(EventUtils.class, "INVALID_EVENTS", EventUtils.class);
            Set<Class<?>> classes = ReflectionUtils.findClassesInPackage("org.bukkit.event", org.bukkit.event.Event.class);
            return classes.stream()
                    .filter(Event.class::isAssignableFrom)
                    .filter(e -> !EntityEvent.class.equals(e))
                    .filter(e -> !VehicleEvent.class.equals(e))
                    .filter(e -> Arrays.stream(invalidEvents).noneMatch(c -> c.equals(e.getSimpleName())))
                    .filter(c -> {
                        if (InventoryEvent.class.isAssignableFrom(c)) return true;
                        try {
                            c.getMethod("getPlayer");
                            return true;
                        } catch (NoSuchMethodException e) {
                            return !ReflectionUtils.getFields(c, Entity.class).isEmpty();
                        }
                    })
                    .map(c -> (Class<? extends Event>) c)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<?>> getEventClasses() {
        return ReflectionUtils.findClassesInPackage("org.bukkit.event", org.bukkit.event.Event.class).stream()
                .filter(Event.class::isAssignableFrom)
                .collect(Collectors.toSet());
    }

    @ParameterizedTest
    @MethodSource("getEventClasses")
    public void testEventClass(Class<?> eventClass) {
        boolean contains = getValidEventClasses().contains(eventClass);
        String message = String.format(contains ? "%s was not detected as valid class, but should have been" : "%s is not a valid class!",
                eventClass.getCanonicalName());
        assertEquals(contains, EventUtils.isClassEventPlayer(eventClass), message);
    }
}