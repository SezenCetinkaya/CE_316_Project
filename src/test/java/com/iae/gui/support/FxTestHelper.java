package com.iae.gui.support;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class FxTestHelper {

    private FxTestHelper() {}

    public static void runOnFxThread(Runnable action) throws Exception {
        runOnFxThread(() -> {
            action.run();
            return null;
        });
    }

    public static <T> T runOnFxThread(Callable<T> action) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return action.call();
        }
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Exception> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                result.set(action.call());
            } catch (Exception ex) {
                error.set(ex);
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(30, TimeUnit.SECONDS)) {
            throw new IllegalStateException("FX action timed out");
        }
        if (error.get() != null) {
            throw error.get();
        }
        return result.get();
    }

    public static <T> T loadController(String fxmlResource, Class<T> controllerType) throws Exception {
        return runOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(FxTestHelper.class.getResource(fxmlResource));
            Parent root = loader.load();
            Scene scene = new Scene(root, 640, 480);
            Stage stage = new Stage();
            stage.setScene(scene);
            // Keep off-screen: avoids focus/modality issues; layout still works
            stage.setOpacity(0);
            return controllerType.cast(loader.getController());
        });
    }

    public static void closeAllStages() throws Exception {
        runOnFxThread(() -> {
            for (Window window : Window.getWindows()) {
                window.hide();
            }
        });
    }
}
