import it.fulminazzo.customenchants.enchants.CustomEnchantment;
import it.fulminazzo.customenchants.enchants.CustomEnchantmentUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Creating new CustomEnchantment telepathy.
        CustomEnchantmentUtils.newInstance("telepathy", 2, EnchantmentTarget.TOOL, Enchantment.getByName("silk_touch"));
        // Creating new CustomEnchantment lightning.
        CustomEnchantmentUtils.newInstance("lightning", 3, EnchantmentTarget.WEAPON);
        // Registering all custom enchantments.
        CustomEnchantment.registerAll();
    }

    @Override
    public void onDisable() {
        // Unregistering all custom enchantments.
        CustomEnchantment.unregisterAll();
    }
}