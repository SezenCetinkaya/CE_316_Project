package com.iae.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ZipExtractorTest {

    private ZipExtractor extractor;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        extractor = new ZipExtractor();
    }

    // T-08 (birim kısmı): Geçerli ZIP başarıyla çıkarılmalı
    @Test
    void extractValidZip_returnsExtractedDirectory() throws Exception {
        File zip = buildZip("student123", "main.c", "int main(){ return 0; }");
        File out = tempDir.resolve("out").toFile();

        File result = extractor.extract(zip, out);

        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    void extractValidZip_sourceFileIsPresent() throws Exception {
        File zip = buildZip("student456", "main.c", "// code");
        File out = tempDir.resolve("out2").toFile();

        extractor.extract(zip, out);

        // main.c öğrenci klasörü içinde bulunmalı
        File expected = new File(out, "student456/main.c");
        assertTrue(expected.exists());
    }

    // T-07: Bozuk ZIP → InvalidZipException, uygulama çökmemeli
    @Test
    void extractCorruptZip_throwsInvalidZipException() {
        File corrupt = tempDir.resolve("corrupt.zip").toFile();
        try (FileOutputStream fos = new FileOutputStream(corrupt)) {
            fos.write(new byte[]{0x00, 0x01, 0x02}); // geçersiz zip verisi
        } catch (IOException ignored) {}

        assertThrows(InvalidZipException.class,
                () -> extractor.extract(corrupt, tempDir.toFile()));
    }

    @Test
    void extractEmptyZip_throwsInvalidZipException() throws Exception {
        File empty = tempDir.resolve("empty.zip").toFile();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(empty))) {
            // hiç entry ekleme
        }
        assertThrows(InvalidZipException.class,
                () -> extractor.extract(empty, tempDir.toFile()));
    }

    @Test
    void extractNonexistentFile_throwsInvalidZipException() {
        File ghost = new File("nonexistent_file.zip");
        assertThrows(InvalidZipException.class,
                () -> extractor.extract(ghost, tempDir.toFile()));
    }

    @Test
    void validateArchive_validZip_returnsTrue() throws Exception {
        File zip = buildZip("stu789", "main.c", "// ok");
        assertTrue(extractor.validateArchive(zip));
    }

    @Test
    void validateArchive_nonexistentFile_returnsFalse() {
        assertFalse(extractor.validateArchive(new File("does_not_exist.zip")));
    }

    @Test
    void validateArchive_corruptFile_returnsFalse() throws Exception {
        File corrupt = tempDir.resolve("bad.zip").toFile();
        try (FileOutputStream fos = new FileOutputStream(corrupt)) {
            fos.write("not a zip".getBytes());
        }
        assertFalse(extractor.validateArchive(corrupt));
    }

    // Yardımcı: studentId/filename içeriğiyle ZIP oluşturur
    private File buildZip(String studentId, String filename, String content) throws IOException {
        File zipFile = tempDir.resolve(studentId + ".zip").toFile();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zos.putNextEntry(new ZipEntry(studentId + "/"));
            zos.closeEntry();
            zos.putNextEntry(new ZipEntry(studentId + "/" + filename));
            zos.write(content.getBytes());
            zos.closeEntry();
        }
        return zipFile;
    }
}
