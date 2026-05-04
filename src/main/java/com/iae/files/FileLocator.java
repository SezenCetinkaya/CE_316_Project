package com.iae.files;

import java.io.File;
import java.io.FileNotFoundException;

public class FileLocator {

    /**
     * workspaceDir içinde filename'i büyük/küçük harf farkı gözetmeden arar.
     * Bulamazsa FileNotFoundException fırlatır.
     * Alt klasörleri de tarar (öğrenci ZIP'i iç içe klasör içerebilir).
     */
    public File locate(File workspaceDir, String filename) throws FileNotFoundException {
        if (!workspaceDir.exists() || !workspaceDir.isDirectory()) {
            throw new FileNotFoundException(
                    "Workspace klasörü bulunamadı: " + workspaceDir.getAbsolutePath());
        }

        File found = searchRecursively(workspaceDir, filename);

        if (found == null) {
            throw new FileNotFoundException(
                    "'" + filename + "' dosyası workspace içinde bulunamadı: "
                    + workspaceDir.getAbsolutePath());
        }

        return found;
    }

    // Önce doğrudan çocukları tara, sonra alt klasörlere in (BFS benzeri öncelik)
    private File searchRecursively(File dir, String filename) {
        File[] children = dir.listFiles();
        if (children == null) {
            return null;
        }

        // Önce dosyaları kontrol et
        for (File child : children) {
            if (child.isFile() && child.getName().equalsIgnoreCase(filename)) {
                return child;
            }
        }

        // Sonra alt klasörleri tara
        for (File child : children) {
            if (child.isDirectory()) {
                File found = searchRecursively(child, filename);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}
