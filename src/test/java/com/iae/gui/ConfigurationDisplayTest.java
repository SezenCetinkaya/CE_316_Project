package com.iae.gui;

import com.iae.core.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationDisplayTest {

    @Test
    void toString_includesNameAndLanguage() {
        Configuration config = new Configuration();
        config.setName("C Programming");
        config.setLanguage("C");

        assertEquals("C Programming (C)", config.toString());
    }

    @Test
    void toString_withoutLanguage_returnsNameOnly() {
        Configuration config = new Configuration();
        config.setName("Custom");

        assertEquals("Custom", config.toString());
    }

    @Test
    void toString_blankName_returnsPlaceholder() {
        Configuration config = new Configuration();
        assertEquals("Unnamed configuration", config.toString());
    }
}
