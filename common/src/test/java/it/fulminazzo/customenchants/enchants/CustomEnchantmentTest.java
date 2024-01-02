package it.fulminazzo.customenchants.enchants;

import it.fulminazzo.customenchants.exceptions.EventHandlerAlreadyPresent;
import it.fulminazzo.customenchants.exceptions.NotValidEventClass;
import it.fulminazzo.customenchants.handlers.EventHandler;
import it.fulminazzo.customenchants.objects.MockCustomEnchantment;
import it.fulminazzo.customenchants.utils.EventUtilsTest;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.MerchantRecipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.exceptions.base.MockitoException;

import javax.swing.plaf.basic.BasicTreeUI;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CustomEnchantmentTest {
    private MockCustomEnchantment mockCustomEnchantment;

    @BeforeEach
    public void setUp() {
        mockCustomEnchantment = new MockCustomEnchantment();
    }

    private static <T> T initialize(Class<T> clazz, Class<?> entityMock) {
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        Constructor<T> constructor = constructors[0];
        for (Constructor<T> c : constructors)
            if (Modifier.isPublic(c.getModifiers())) {
                constructor = c;
                break;
            }
        constructor.setAccessible(true);
        List<Object> parameters = new ArrayList<>();
        for (Class<?> param : constructor.getParameterTypes())
            try {
                parameters.add(getObjectFromClass(param, entityMock));
            } catch (MockitoException e) {
                return null;
            }
        try {
            return constructor.newInstance(parameters.toArray(new Object[0]));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static <T> Object getObjectFromClass(Class<T> clazz, Class<?> entityMock) {
        if (clazz.equals(int.class)) return 0;
        else if (clazz.equals(float.class)) return 0f;
        else if (clazz.equals(double.class)) return 0d;
        else if (clazz.equals(boolean.class)) return false;
        else if (clazz.equals(char.class)) return 'a';
        else if (clazz.equals(long.class)) return 0L;
        else if (clazz.equals(short.class)) return (short) 0;
        else if (clazz.equals(byte.class)) return (byte) 0;
        if (Entity.class.isAssignableFrom(clazz)) return mock(entityMock);
        return mock(clazz);
    }

    private static Object[][] getTestEvents() {
        Block block = mock(Block.class);
        Player player = mock(Player.class);
        LivingEntity livingEntity = mock(LivingEntity.class);
        Item item = mock(Item.class);
        Projectile projectile = mock(Projectile.class);
        return new Object[][]{
                new Object[]{new PlayerDeathEvent(player, new ArrayList<>(), 10, "RIP"), true},
                new Object[]{new EntityDeathEvent(livingEntity, new ArrayList<>(), 10), false},
                new Object[]{new BlockBreakEvent(block, player), true},
                new Object[]{new SignChangeEvent(block, player, new String[0], Side.BACK), true},
                new Object[]{new EntityPickupItemEvent(player, item, 1), true},
                new Object[]{new EntityPickupItemEvent(livingEntity, item, 1), false},
                new Object[]{new EntityDamageByEntityEvent(livingEntity, player, EntityDamageEvent.DamageCause.CUSTOM, 10), true},
                new Object[]{new EntityDamageByEntityEvent(livingEntity, livingEntity, EntityDamageEvent.DamageCause.CUSTOM, 10), false},
                new Object[]{new ProjectileHitEvent(projectile, player, null, null), true},
                new Object[]{new ProjectileHitEvent(projectile, livingEntity, null, null), false},
        };
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

    @ParameterizedTest
    @MethodSource("getTestEvents")
    public void testExecutingEvents(Event event, boolean expected) {
        AtomicBoolean check = new AtomicBoolean(false);
        mockCustomEnchantment.addEventHandler(event.getEventName(), new EventHandler<>(event.getClass(), e -> check.set(true)));
        mockCustomEnchantment.handleEvent(event);
        assertEquals(expected, check.get());
    }
}