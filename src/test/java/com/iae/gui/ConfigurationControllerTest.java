package com.iae.gui;

import com.iae.db.ConfigurationDAO;
import com.iae.gui.support.FxTestHelper;
import com.iae.gui.support.JavaFxExtension;
import com.iae.gui.support.TestDatabase;
import com.iae.gui.support.UiTestReflection;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxExtension.class)
class ConfigurationControllerTest {

    private final TestDatabase testDatabase = new TestDatabase();

    @BeforeEach
    void setUp() throws Exception {
        testDatabase.setUp();
    }

    @AfterEach
    void tearDown() throws Exception {
        FxTestHelper.closeAllStages();
        testDatabase.tearDown();
    }

    @Test
    void fxml_loads_configurationEditor() throws Exception {
        ConfigurationController controller = FxTestHelper.loadController(
                "/com/iae/gui/configuration-view.fxml", ConfigurationController.class);
        assertNotNull(controller);
    }

    @Test
    void seedDefaults_populatesListView() throws Exception {
        new ConfigurationDAO().seedDefaultsIfEmpty();

        ConfigurationController controller = FxTestHelper.loadController(
                "/com/iae/gui/configuration-view.fxml", ConfigurationController.class);

        FxTestHelper.runOnFxThread(() -> {
            ListView<String> list = UiTestReflection.getField(controller, "configList", ListView.class);
            assertEquals(3, list.getItems().size());
            assertTrue(list.getItems().contains("C Programming"));
            assertTrue(list.getItems().contains("Java"));
            assertTrue(list.getItems().contains("Python"));
        });
    }

    @Test
    void onNew_clearsFormFields() throws Exception {
        ConfigurationController controller = FxTestHelper.loadController(
                "/com/iae/gui/configuration-view.fxml", ConfigurationController.class);

        FxTestHelper.runOnFxThread(() -> {
            TextField nameField = UiTestReflection.getField(controller, "nameField", TextField.class);
            TextField languageField = UiTestReflection.getField(controller, "languageField", TextField.class);

            nameField.setText("Test Config");
            languageField.setText("C");
            controller.onNew();

            assertEquals("", nameField.getText());
            assertEquals("", languageField.getText());
        });
    }

    @Test
    void selectingConfig_loadsFormFields() throws Exception {
        ConfigurationDAO dao = new ConfigurationDAO();
        dao.seedDefaultsIfEmpty();
        var javaConfig = dao.findByName("Java");
        assertNotNull(javaConfig);

        ConfigurationController controller = FxTestHelper.loadController(
                "/com/iae/gui/configuration-view.fxml", ConfigurationController.class);

        FxTestHelper.runOnFxThread(() -> {
            controller.loadConfiguration(javaConfig.getConfigId());
            TextField compilerPath = UiTestReflection.getField(controller, "compilerPathField", TextField.class);
            TextField sourceFile = UiTestReflection.getField(controller, "sourceFilenameField", TextField.class);

            assertEquals("javac", compilerPath.getText());
            assertEquals("Main.java", sourceFile.getText());
        });
    }

}
