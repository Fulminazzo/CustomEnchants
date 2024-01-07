package it.fulminazzo.customenchants.utils;

import it.fulminazzo.customenchants.enchants.CustomEnchantment;
import it.fulminazzo.customenchants.objects.MockCustomEnchantment;
import org.bukkit.entity.LightningStrike;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemUtilsTest {
    
    @ParameterizedTest
    @ValueSource(strings = {
            ";&7Mock Enchant II",
            "&7Mock Enchant I;&7Mock Enchant II",
            " ,&7Mock Enchant I; ,&7Mock Enchant II"
    })
    void testEditLore(String rawLore) {
        String[] tmp = rawLore.split(";");
        List<String> lore = tmp[0].equals("null") ? null : new ArrayList<>(Arrays.asList(tmp[0].split(",")))
                .stream().map(StringUtils::color).collect(Collectors.toList());
        List<String> expected = new ArrayList<>(Arrays.asList(tmp[1].split(",")))
                .stream().map(StringUtils::color).collect(Collectors.toList());
        ItemStack itemStack = mock(ItemStack.class);
        ItemMeta meta = mock(ItemMeta.class);
        when(itemStack.getItemMeta()).thenReturn(meta);
        when(meta.getLore()).thenReturn(lore);
        CustomEnchantment enchantment = new MockCustomEnchantment();
        enchantment.apply(itemStack, 2);
        assertEquals(expected, lore);
        assertIterableEquals(expected, lore);
    }
}