package com.iae.gui.support;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Starts the JavaFX toolkit once for all UI tests in the JVM.
 */
public class JavaFxExtension implements BeforeAllCallback {

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (STARTED.compareAndSet(false, true)) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
            } catch (IllegalStateException alreadyRunning) {
                latch.countDown();
            }
            if (!latch.await(30, TimeUnit.SECONDS)) {
                throw new IllegalStateException("JavaFX toolkit did not start in time");
            }
        }
    }
}
