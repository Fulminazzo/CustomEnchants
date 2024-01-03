package it.fulminazzo.customenchants.exceptions;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class NotValidEventClass extends RuntimeException {

    public NotValidEventClass(@NotNull Class<?> clazz) {
        super(String.format("Class %s is not a valid %s class. Are you sure it contains an entity that is a player?",
                clazz.getCanonicalName(), Event.class.getCanonicalName()));
    }
}
