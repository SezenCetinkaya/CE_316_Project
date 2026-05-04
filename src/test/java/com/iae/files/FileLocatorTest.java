package com.iae.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileLocatorTest {

    private FileLocator locator;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        locator = new FileLocator();
    }

    @Test
    void locateExactFilename_returnsCorrectFile() throws Exception {
        Files.writeString(tempDir.resolve("main.c"), "// code");

        File found = locator.locate(tempDir.toFile(), "main.c");

        assertEquals("main.c", found.getName());
        assertTrue(found.exists());
    }

    // T-06 bağlamı: main.c yerine Main.c gönderilse de bulunmalı
    @Test
    void locateCaseInsensitive_upperCaseFile_found() throws Exception {
        Files.writeString(tempDir.resolve("Main.c"), "// code");

        File found = locator.locate(tempDir.toFile(), "main.c");

        assertNotNull(found);
        assertTrue(found.getName().equalsIgnoreCase("main.c"));
    }

    // ZIP çıkarıldığında öğrenci ID klasörü içinde dosya bulunmalı
    @Test
    void locateInSubdirectory_returnsFile() throws Exception {
        Path subDir = tempDir.resolve("student123");
        Files.createDirectory(subDir);
        Files.writeString(subDir.resolve("main.c"), "// nested");

        File found = locator.locate(tempDir.toFile(), "main.c");

        assertNotNull(found);
        assertTrue(found.getAbsolutePath().contains("student123"));
    }

    // T-06: Kaynak dosya eksikse FileNotFoundException fırlatılmalı
    @Test
    void fileNotFound_throwsFileNotFoundException() {
        assertThrows(FileNotFoundException.class,
                () -> locator.locate(tempDir.toFile(), "missing.c"));
    }

    @Test
    void nonexistentWorkspace_throwsFileNotFoundException() {
        File ghost = new File("does_not_exist_dir");
        assertThrows(FileNotFoundException.class,
                () -> locator.locate(ghost, "main.c"));
    }

    @Test
    void deeplyNestedFile_isFound() throws Exception {
        Path level1 = tempDir.resolve("a");
        Path level2 = level1.resolve("b");
        Files.createDirectories(level2);
        Files.writeString(level2.resolve("Main.c"), "// deep");

        File found = locator.locate(tempDir.toFile(), "main.c");

        assertNotNull(found);
    }
}
