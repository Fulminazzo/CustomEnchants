package it.fulminazzo.customenchants.handlers;

import lombok.Getter;
import org.bukkit.event.Event;

import java.util.Objects;
import java.util.function.Consumer;

public class EventHandler<T extends Event> {
    @Getter
    private final Class<T> event;
    private final Consumer<T> runner;

    public EventHandler(Class<T> event, Consumer<T> runner) {
        this.event = event;
        this.runner = runner;
    }

    public void apply(T t) {
        runner.accept(t);
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