package it.fulminazzo.customenchants.enchants;

import it.fulminazzo.customenchants.exceptions.EventHandlerAlreadyPresent;
import it.fulminazzo.customenchants.exceptions.NotValidEventClass;
import it.fulminazzo.customenchants.handlers.EventHandler;
import it.fulminazzo.customenchants.objects.MockCustomEnchantment;
import it.fulminazzo.customenchants.utils.EventUtilsTest;
import org.bukkit.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class CustomEnchantmentTest {
    private MockCustomEnchantment mockCustomEnchantment;

    @BeforeEach
    public void setUp() {
        mockCustomEnchantment = new MockCustomEnchantment();
    }

    @ParameterizedTest
    @MethodSource("it.fulminazzo.customenchants.utils.EventUtilsTest#getEventClasses")
    public void testCreatingEventHandler(Class<? extends Event> eventClass) {
        String name = eventClass.getSimpleName();
        EventHandler<? extends Event> eventHandler = new EventHandler<>(eventClass, (e) -> {});
        Executable executable = () -> mockCustomEnchantment.addEventHandler(name, eventHandler);
        if (EventUtilsTest.getValidEventClasses().contains(eventClass)) {
            try {
                executable.execute();
                assertNotNull(mockCustomEnchantment.getEventHandler(name));
                assertThrowsExactly(EventHandlerAlreadyPresent.class, executable);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else assertThrowsExactly(NotValidEventClass.class, executable);
    }
}