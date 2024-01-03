package it.fulminazzo.customenchants.objects;

import it.fulminazzo.customenchants.enchants.CustomEnchantment;
import it.fulminazzo.customenchants.handlers.EventHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class MockCustomEnchantment implements CustomEnchantment {
    private int startLevel = 1;
    private int maxLevel = 5;
    private boolean treasure = false;
    private boolean cursed = false;
    private EnchantmentTarget itemTarget = EnchantmentTarget.ALL;
    private final HashMap<String, EventHandler<?>> eventHandlers = new HashMap<>();

    @Override
    public String getName() {
        return "mock_enchant";
    }

    @Override
    public <T> T getKey() {
        return null;
    }

    @Override
    public int getId() {
        return Integer.MAX_VALUE;
    }

    @Override
    public List<Enchantment> getConflicts() {
        return null;
    }

    @Override
    public JavaPlugin getPlugin() {
        return null;
    }
}
