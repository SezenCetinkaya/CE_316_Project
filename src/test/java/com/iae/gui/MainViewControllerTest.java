package com.iae.gui;

import com.iae.gui.support.FxTestHelper;
import com.iae.gui.support.JavaFxExtension;
import com.iae.gui.support.UiTestReflection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxExtension.class)
class MainViewControllerTest {

    @AfterEach
    void tearDown() throws Exception {
        FxTestHelper.closeAllStages();
    }

    @Test
    void fxml_loads_andWiresMainController() throws Exception {
        MainController controller = FxTestHelper.runOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/iae/gui/main-view.fxml"));
            Parent root = loader.load();
            new Scene(root, 800, 600);
            return loader.getController();
        });

        assertNotNull(controller);
    }

    @Test
    void initialState_runButtonDisabled_andStatusReady() throws Exception {
        FxTestHelper.runOnFxThread((java.util.concurrent.Callable<Void>) () -> {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/iae/gui/main-view.fxml"));
            loader.load();
            MainController controller = loader.getController();

            Button runButton = UiTestReflection.getField(controller, "runButton", Button.class);
            Label statusLabel = UiTestReflection.getField(controller, "statusLabel", Label.class);
            TextField projectNameField = UiTestReflection.getField(controller, "projectNameField", TextField.class);

            assertTrue(runButton.isDisabled());
            assertTrue(statusLabel.getText().contains("Ready"));
            assertEquals("", projectNameField.getText());
            return null;
        });
    }

    @Test
    void projectCard_andResultsCard_exist() throws Exception {
        FxTestHelper.runOnFxThread((java.util.concurrent.Callable<Void>) () -> {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/iae/gui/main-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            assertNotNull(scene.lookup("#projectCard"));
            assertNotNull(scene.lookup("#resultsCard"));
            assertNotNull(scene.lookup("#resultsTable"));
            return null;
        });
    }
}
