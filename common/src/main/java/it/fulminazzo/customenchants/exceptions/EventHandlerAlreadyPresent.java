package it.fulminazzo.customenchants.exceptions;

public class EventHandlerAlreadyPresent extends RuntimeException {

    public EventHandlerAlreadyPresent(String name) {
        super(name);
    }
}
