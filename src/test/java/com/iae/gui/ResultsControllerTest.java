package com.iae.gui;

import com.iae.core.StudentResult;
import com.iae.gui.support.FxTestHelper;
import com.iae.gui.support.JavaFxExtension;
import com.iae.gui.support.UiTestReflection;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxExtension.class)
class ResultsControllerTest {

    @AfterEach
    void tearDown() throws Exception {
        FxTestHelper.closeAllStages();
    }

    @Test
    void setResults_populatesTableAndStats() throws Exception {
        ResultsController controller = FxTestHelper.loadController(
                "/com/iae/gui/results-view.fxml", ResultsController.class);

        StudentResult pass = new StudentResult();
        pass.setStudentId("20260001");
        pass.setZipFilename("20260001.zip");
        pass.setCompileStatus("COMPILE_SUCCESS");
        pass.setRunStatus("PASS");
        pass.setExecutionTimeMs(120);

        StudentResult fail = new StudentResult();
        fail.setStudentId("20260002");
        fail.setZipFilename("20260002.zip");
        fail.setCompileStatus("COMPILE_SUCCESS");
        fail.setRunStatus("FAIL");

        FxTestHelper.runOnFxThread(() -> {
            controller.setResults(List.of(pass, fail));

            TableView<?> table = UiTestReflection.getField(controller, "resultsTable", TableView.class);
            Label statPass = UiTestReflection.getField(controller, "statPassLabel", Label.class);
            Label statFail = UiTestReflection.getField(controller, "statFailLabel", Label.class);
            Label statTotal = UiTestReflection.getField(controller, "statTotalLabel", Label.class);

            assertEquals(2, table.getItems().size());
            assertEquals("Total: 2", statTotal.getText());
            assertEquals("Pass: 1", statPass.getText());
            assertEquals("Fail: 1", statFail.getText());
        });
    }

    @Test
    void clear_emptiesTableAndStats() throws Exception {
        ResultsController controller = FxTestHelper.loadController(
                "/com/iae/gui/results-view.fxml", ResultsController.class);

        FxTestHelper.runOnFxThread(() -> {
            StudentResult pass = new StudentResult();
            pass.setRunStatus("PASS");
            controller.setResults(List.of(pass));
            controller.clear();

            TableView<?> table = UiTestReflection.getField(controller, "resultsTable", TableView.class);
            Label statTotal = UiTestReflection.getField(controller, "statTotalLabel", Label.class);
            TextArea log = UiTestReflection.getField(controller, "errorLogArea", TextArea.class);

            assertTrue(table.getItems().isEmpty());
            assertEquals("Total: 0", statTotal.getText());
            assertEquals("", log.getText());
        });
    }

    @Test
    void selectingRow_updatesErrorLog() throws Exception {
        ResultsController controller = FxTestHelper.loadController(
                "/com/iae/gui/results-view.fxml", ResultsController.class);

        FxTestHelper.runOnFxThread(() -> {
            StudentResult result = new StudentResult();
            result.setStudentId("20260003");
            result.setRunStatus("COMPILE_ERROR");
            result.setCompileErrorLog("error: expected ';' before '}'");
            controller.setResults(List.of(result));

            TableView<StudentResult> table =
                    UiTestReflection.getField(controller, "resultsTable", TableView.class);
            TextArea log = UiTestReflection.getField(controller, "errorLogArea", TextArea.class);

            table.getSelectionModel().select(0);
            assertTrue(log.getText().contains("expected ';'"));
        });
    }

}
