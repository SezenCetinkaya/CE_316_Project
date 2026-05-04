package com.iae.files;

import java.io.File;
import java.io.IOException;

public class WorkspaceManager {

    private static final String WORKSPACE_ROOT =
            System.getProperty("java.io.tmpdir") + File.separator + "iae_workspaces";

    /**
     * studentId için temiz bir geçici klasör oluşturur.
     * Zaten varsa önce siler, sonra yeniden oluşturur.
     */
    public File createWorkspace(String studentId) throws IOException {
        File workspace = new File(WORKSPACE_ROOT, studentId);

        if (workspace.exists()) {
            deleteRecursively(workspace);
        }

        if (!workspace.mkdirs()) {
            throw new IOException("Workspace oluşturulamadı: " + studentId);
        }

        return workspace;
    }

    /**
     * studentId'ye ait workspace klasörünü ve içeriğini tamamen siler.
     */
    public void cleanWorkspace(String studentId) throws IOException {
        File workspace = new File(WORKSPACE_ROOT, studentId);
        if (workspace.exists()) {
            deleteRecursively(workspace);
        }
    }

    private void deleteRecursively(File target) throws IOException {
        if (target.isDirectory()) {
            File[] children = target.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        if (!target.delete()) {
            throw new IOException("Dosya/klasör silinemedi: " + target.getAbsolutePath());
        }
    }
}
