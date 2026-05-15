package com.iae.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StudentResultTest {

    @Test
    void appendErrorShouldStoreMessage() {
        StudentResult result = new StudentResult();

        result.appendError("Compile failed");

        assertEquals("Compile failed", result.getCompileErrorLog());
    }

    @Test
    void markCompiledShouldUpdateStatus() {
        StudentResult result = new StudentResult();

        result.markCompiled("COMPILE_SUCCESS");

        assertEquals("COMPILE_SUCCESS", result.getCompileStatus());
    }
}