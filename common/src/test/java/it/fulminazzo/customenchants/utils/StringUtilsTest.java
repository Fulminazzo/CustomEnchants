package it.fulminazzo.customenchants.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testCapitalize() {
        assertEquals("Mock Enchant", StringUtils.capitalize("mock_enchant"));
    }
}