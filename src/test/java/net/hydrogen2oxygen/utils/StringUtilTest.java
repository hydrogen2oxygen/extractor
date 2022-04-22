package net.hydrogen2oxygen.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilTest {

    @Test
    public void testCleanString() {
        assertEquals("Actaea", StringUtil.cleanString("Actæa"));
        assertEquals("OEnothera", StringUtil.cleanString("Œnothera"));
    }
}
