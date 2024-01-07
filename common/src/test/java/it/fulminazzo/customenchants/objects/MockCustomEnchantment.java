package it.fulminazzo.customenchants.objects;

import it.fulminazzo.customenchants.enchants.CustomEnchantment;
import it.fulminazzo.customenchants.handlers.EventHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class MockCustomEnchantment extends Enchantment implements CustomEnchantment {
    private int startLevel = 1;
    private int maxLevel = 5;
    private boolean treasure = false;
    private boolean cursed = false;
    private EnchantmentTarget itemTarget = EnchantmentTarget.ALL;
    private final HashMap<String, EventHandler<?>> eventHandlers = new HashMap<>();

    public MockCustomEnchantment() {
        super(NamespacedKey.minecraft("mock_enchant"));
    }

    @Override
    public NamespacedKey getKey() {
        return super.getKey();
    }

    @Override
    public String getName() {
        return "mock_enchant";
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return false;
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
