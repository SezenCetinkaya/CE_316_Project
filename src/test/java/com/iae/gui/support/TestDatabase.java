package com.iae.gui.support;

import com.iae.db.DatabaseHelper;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Points DAOs at a temporary SQLite database for isolated UI tests.
 */
public final class TestDatabase {

    private Path dbPath;

    public void setUp() throws Exception {
        dbPath = Files.createTempFile("iae-ui-test-", ".db");
        System.setProperty("iae.db.url", dbPath.toString());
        new DatabaseHelper().initialiseSchema();
    }

    public void tearDown() throws Exception {
        System.clearProperty("iae.db.url");
        if (dbPath != null) {
            Files.deleteIfExists(dbPath);
            dbPath = null;
        }
    }
}
