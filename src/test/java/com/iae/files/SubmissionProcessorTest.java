package com.iae.files;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class SubmissionProcessorTest {

    @Test
    void extractStudentIdShouldRemoveZipExtension() {
        SubmissionProcessor processor = new SubmissionProcessor();

        String result = processor.processSingle(
                new File("20220602021.zip"),
                null,
                "",
                false
        ).getStudentId();

        assertNotNull(result);
    }
}
