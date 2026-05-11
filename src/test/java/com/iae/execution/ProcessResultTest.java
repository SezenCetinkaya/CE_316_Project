package com.iae.execution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessResultTest {

    @Test
    void testSuccessfulProcess() {
        ProcessResult result = new ProcessResult(0, "output", "", false);
        assertTrue(result.isSuccessful());
        assertFalse(result.hasFailed());
    }

    @Test
    void testFailedProcessWithExitCode() {
        ProcessResult result = new ProcessResult(1, "", "error", false);
        assertFalse(result.isSuccessful());
        assertTrue(result.hasFailed());
    }

    @Test
    void testFailedProcessWithTimeout() {
        ProcessResult result = new ProcessResult(0, "", "", true);
        assertFalse(result.isSuccessful());
        assertTrue(result.hasFailed());
    }

    @Test
    void testCombinedOutputBothFilled() {
        ProcessResult result = new ProcessResult(0, "line1", "line2", false);
        String combined = result.getCombinedOutput();
        assertTrue(combined.contains("line1"));
        assertTrue(combined.contains("line2"));
        assertEquals("line1\nline2", combined);
    }

    @Test
    void testCombinedOutputOnlyStderr() {
        ProcessResult result = new ProcessResult(0, "", "error-only", false);
        assertEquals("error-only", result.getCombinedOutput());
    }

    @Test
    void testNullOutputs() {
        ProcessResult result = new ProcessResult(0, null, null, false);
        assertDoesNotThrow(result::getCombinedOutput);
        assertEquals("", result.getStdout());
        assertEquals("", result.getStderr());
    }
}
