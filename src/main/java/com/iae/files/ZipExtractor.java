package com.iae.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    /**
     * ZIP dosyasını targetDir altına çıkarır.
     * Başarılıysa çıkarılan kök klasörü döner.
     * Bozuk veya boş ZIP'te InvalidZipException fırlatır.
     */
    public File extract(File zipFile, File targetDir) throws InvalidZipException {
        if (!zipFile.exists() || !zipFile.isFile()) {
            throw new InvalidZipException("ZIP dosyası bulunamadı: " + zipFile.getAbsolutePath());
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File extractedRoot = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zis.getNextEntry();

            if (entry == null) {
                throw new InvalidZipException("ZIP boş veya bozuk: " + zipFile.getName());
            }

            while (entry != null) {
                File outFile = resolveEntry(targetDir, entry);

                if (extractedRoot == null) {
                    // İlk entry'nin kök klasörünü yakala (öğrenci ID klasörü)
                    extractedRoot = entry.isDirectory() ? outFile : outFile.getParentFile();
                }

                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    writeEntry(zis, outFile);
                }

                zis.closeEntry();
                entry = zis.getNextEntry();
            }

        } catch (IOException e) {
            throw new InvalidZipException("ZIP çıkarma hatası (" + zipFile.getName() + "): " + e.getMessage(), e);
        }

        if (extractedRoot == null) {
            throw new InvalidZipException("ZIP çıktı üretmedi: " + zipFile.getName());
        }

        return extractedRoot;
    }

    /**
     * ZIP dosyasının geçerli olup olmadığını doğrular (bozuk değilse true).
     */
    public boolean validateArchive(File zipFile) {
        if (!zipFile.exists() || !zipFile.isFile()) {
            return false;
        }
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            return zis.getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }

    private void writeEntry(ZipInputStream zis, File outFile) throws IOException {
        byte[] buffer = new byte[8192];
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }

    // Zip-slip saldırısını engeller: entry targetDir dışına çıkamaz
    private File resolveEntry(File targetDir, ZipEntry entry) throws InvalidZipException {
        try {
            File resolved = new File(targetDir, entry.getName());
            String canonicalTarget = targetDir.getCanonicalPath() + File.separator;
            String canonicalEntry  = resolved.getCanonicalPath();

            if (!canonicalEntry.startsWith(canonicalTarget)) {
                throw new InvalidZipException("Güvensiz ZIP entry (zip-slip): " + entry.getName());
            }
            return resolved;
        } catch (IOException e) {
            throw new InvalidZipException("Entry yolu çözümlenemedi: " + entry.getName(), e);
        }
    }
}
