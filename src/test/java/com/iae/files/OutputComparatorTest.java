package com.iae.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OutputComparatorTest {

    private OutputComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new OutputComparator();
    }

    @Test
    void identicalStrings_returnsTrue() {
        assertTrue(comparator.compare("hello\nworld", "hello\nworld"));
    }

    @Test
    void differentContent_returnsFalse() {
        assertFalse(comparator.compare("apple\nbanana", "apple\ncherry"));
    }

    // T-05: \r\n (Windows) ile \n (Unix) aynı sayılmalı
    @Test
    void windowsLineEndings_treatedSameAsUnix() {
        assertTrue(comparator.compare("line1\r\nline2\r\n", "line1\nline2"));
    }

    // Tasarım raporu §9.1: trailing whitespace normalize edilmeli
    @Test
    void trailingWhitespace_treatedAsEqual() {
        assertTrue(comparator.compare("hello   \n", "hello"));
    }

    @Test
    void differentLineCounts_returnsFalse() {
        assertFalse(comparator.compare("a\nb\nc", "a\nb"));
    }

    @Test
    void emptyVsEmpty_returnsTrue() {
        assertTrue(comparator.compare("", ""));
    }

    @Test
    void nullActual_returnsFalse() {
        assertFalse(comparator.compare(null, "expected"));
    }

    // getCombinedOutput() fark olan satır numarasını içermeli
    @Test
    void onLineDiff_diffMessageContainsLineNumber() {
        comparator.compare("line1\nWRONG\nline3", "line1\nCORRECT\nline3");
        String msg = comparator.getCombinedOutput();
        assertTrue(msg.contains("2"), "Fark mesajı satır 2'yi belirtmeli: " + msg);
    }

    @Test
    void onMatch_diffMessageIsEmpty() {
        comparator.compare("same", "same");
        assertEquals("", comparator.getCombinedOutput());
    }

    @Test
    void compareAgainstExpected_delegatesToCompare() {
        assertTrue(comparator.compareAgainstExpected("abc", "abc"));
        assertFalse(comparator.compareAgainstExpected("abc", "xyz"));
    }
}
