package com.iae.gui;

import com.iae.core.Configuration;
import com.iae.db.ConfigurationDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class ConfigurationController {

    @FXML private ListView<String> configList;
    @FXML private TextField nameField;
    @FXML private TextField languageField;
    @FXML private TextField compilerPathField;
    @FXML private TextField compileArgsField;
    @FXML private TextField sourceFilenameField;
    @FXML private TextField runCommandField;
    @FXML private Spinner<Integer> timeoutSpinner;
    @FXML private CheckBox interpretedCheck;
    @FXML private BorderPane configRoot;
    @FXML private VBox configFormCard;

    private final ConfigurationDAO configDAO = new ConfigurationDAO();
    private Configuration editingConfig;
    private Stage stage;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 600, 30, 5);
        timeoutSpinner.setValueFactory(factory);

        configList.getSelectionModel().selectedItemProperty().addListener((obs, old, name) -> {
            if (name != null) {
                Configuration found = configDAO.findByName(name);
                if (found != null) {
                    loadIntoForm(found);
                    UiAnimations.fadeInUp(configFormCard, Duration.ZERO);
                }
            }
        });

        UiAnimations.applyCardShadow(configFormCard);
        UiAnimations.fadeInUp(configList, Duration.millis(80));
        UiAnimations.fadeInUp(configFormCard, Duration.millis(160));

        refreshList();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onNew() {
        editingConfig = new Configuration();
        editingConfig.setConfigId(0);
        editingConfig.setTimeoutSeconds(30);
        clearForm();
        configList.getSelectionModel().clearSelection();
        nameField.requestFocus();
    }

    @FXML
    public void onSave() {
        if (!validateForm()) {
            return;
        }

        Configuration config = editingConfig != null ? editingConfig : new Configuration();
        applyFormTo(config);

        if (config.getConfigId() > 0) {
            configDAO.update(config);
        } else {
            int id = configDAO.insert(config);
            if (id > 0) {
                config.setConfigId(id);
            }
        }

        editingConfig = config;
        refreshList();
        selectByName(config.getName());
        UiAnimations.flashSuccess(configFormCard);
        showInfo("Saved", "Configuration \"" + config.getName() + "\" saved.");
    }

    @FXML
    public void onDelete() {
        String selected = configList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Nothing selected", "Select a configuration to delete.");
            return;
        }

        Configuration found = configDAO.findByName(selected);
        if (found == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete configuration");
        confirm.setHeaderText("Delete \"" + selected + "\"?");
        confirm.setContentText("This cannot be undone.");
        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.isEmpty() || answer.get() != ButtonType.OK) {
            return;
        }

        configDAO.delete(found.getConfigId());
        editingConfig = null;
        clearForm();
        refreshList();
    }

    @FXML
    public void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import configuration");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Configuration files", "*.properties", "*.cfg"));
        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            Configuration imported = Configuration.importFromFile(file.getAbsolutePath());
            imported.setConfigId(0);
            int id = configDAO.insert(imported);
            imported.setConfigId(id);
            refreshList();
            loadIntoForm(imported);
            selectByName(imported.getName());
            showInfo("Imported", "Configuration imported from " + file.getName());
        } catch (RuntimeException ex) {
            showError("Import failed", ex.getMessage());
        }
    }

    @FXML
    public void onExport() {
        if (editingConfig == null || editingConfig.getName() == null || editingConfig.getName().isBlank()) {
            showWarning("Nothing to export", "Save or select a configuration first.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export configuration");
        chooser.setInitialFileName(editingConfig.getName() + ".properties");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Properties", "*.properties"));
        File file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        String path = file.getAbsolutePath();
        if (!path.endsWith(".properties")) {
            path += ".properties";
        }
        try {
            editingConfig.exportToFile(path);
            showInfo("Exported", "Configuration saved to " + new File(path).getName());
        } catch (RuntimeException ex) {
            showError("Export failed", ex.getMessage());
        }
    }

    @FXML
    public void onBrowseCompiler() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select compiler or interpreter");
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            compilerPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void onClose() {
        if (stage != null) {
            stage.close();
        }
    }

    public void loadConfiguration(int configId) {
        Configuration config = configDAO.findById(configId);
        if (config != null) {
            loadIntoForm(config);
            selectByName(config.getName());
        }
    }

    private void refreshList() {
        List<Configuration> all = configDAO.findAll();
        configList.setItems(FXCollections.observableArrayList(
                all.stream().map(Configuration::getName).toList()));
    }

    private void selectByName(String name) {
        configList.getSelectionModel().select(name);
    }

    private void loadIntoForm(Configuration config) {
        editingConfig = config;
        nameField.setText(config.getName());
        languageField.setText(config.getLanguage());
        compilerPathField.setText(config.getCompilerPath());
        compileArgsField.setText(config.getCompileArgs());
        sourceFilenameField.setText(config.getSourceFilename());
        runCommandField.setText(config.getRunCommand());
        timeoutSpinner.getValueFactory().setValue(
                config.getTimeoutSeconds() > 0 ? config.getTimeoutSeconds() : 30);
        interpretedCheck.setSelected(config.isInterpreted());
    }

    private void applyFormTo(Configuration config) {
        config.setName(nameField.getText().trim());
        config.setLanguage(languageField.getText().trim());
        config.setCompilerPath(compilerPathField.getText().trim());
        config.setCompileArgs(compileArgsField.getText().trim());
        config.setSourceFilename(sourceFilenameField.getText().trim());
        config.setRunCommand(runCommandField.getText().trim());
        config.setTimeoutSeconds(timeoutSpinner.getValue());
        config.setInterpreted(interpretedCheck.isSelected());
    }

    private void clearForm() {
        nameField.clear();
        languageField.clear();
        compilerPathField.clear();
        compileArgsField.clear();
        sourceFilenameField.clear();
        runCommandField.clear();
        timeoutSpinner.getValueFactory().setValue(30);
        interpretedCheck.setSelected(false);
    }

    private boolean validateForm() {
        if (nameField.getText() == null || nameField.getText().isBlank()) {
            showWarning("Validation", "Configuration name is required.");
            return false;
        }
        if (!interpretedCheck.isSelected()
                && (compilerPathField.getText() == null || compilerPathField.getText().isBlank())) {
            showWarning("Validation", "Compiler path is required for compiled languages.");
            return false;
        }
        if (sourceFilenameField.getText() == null || sourceFilenameField.getText().isBlank()) {
            showWarning("Validation", "Source filename is required (e.g. main.c).");
            return false;
        }
        return true;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
