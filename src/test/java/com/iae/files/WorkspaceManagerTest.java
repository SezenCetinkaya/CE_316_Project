package com.iae.files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceManagerTest {

    private static final String TEST_STUDENT = "test_student_ws_001";
    private WorkspaceManager manager;

    @BeforeEach
    void setUp() {
        manager = new WorkspaceManager();
    }

    @AfterEach
    void tearDown() throws Exception {
        manager.cleanWorkspace(TEST_STUDENT);
    }

    @Test
    void createWorkspace_createsDirectory() throws Exception {
        File ws = manager.createWorkspace(TEST_STUDENT);

        assertTrue(ws.exists());
        assertTrue(ws.isDirectory());
    }

    // Her öğrenci için izole, boş bir workspace sağlanmalı
    @Test
    void createWorkspace_existingWorkspace_isReplaced() throws Exception {
        File ws1 = manager.createWorkspace(TEST_STUDENT);
        new File(ws1, "leftover.txt").createNewFile();

        File ws2 = manager.createWorkspace(TEST_STUDENT);

        assertTrue(ws2.exists());
        File[] contents = ws2.listFiles();
        assertNotNull(contents);
        assertEquals(0, contents.length, "Yeni workspace boş olmalı");
    }

    @Test
    void cleanWorkspace_removesDirectory() throws Exception {
        File ws = manager.createWorkspace(TEST_STUDENT);
        assertTrue(ws.exists());

        manager.cleanWorkspace(TEST_STUDENT);

        assertFalse(ws.exists());
    }

    @Test
    void cleanWorkspace_nonexistentStudent_noException() {
        assertDoesNotThrow(() -> manager.cleanWorkspace("student_who_never_existed_xyz"));
    }

    @Test
    void createWorkspace_differentStudents_isolatedDirectories() throws Exception {
        String studentB = "test_student_ws_002";
        try {
            File wsA = manager.createWorkspace(TEST_STUDENT);
            File wsB = manager.createWorkspace(studentB);

            assertNotEquals(wsA.getAbsolutePath(), wsB.getAbsolutePath());
        } finally {
            manager.cleanWorkspace(studentB);
        }
    }
}
