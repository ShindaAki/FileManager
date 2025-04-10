package com.mymanager.mfm;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    public VBox leftPanel, rightPanel;

    PanelController leftPC, rightPC, sourcePC = null, destinationPC = null;
    Path sourcePath, destinationPath;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        leftPC = (PanelController) leftPanel.getProperties().get("tabController");
        rightPC = (PanelController) rightPanel.getProperties().get("tabController");
    }

    public void btnExit(ActionEvent actionEvent) {
       Platform.exit();
    }

    public void copyBtnAction(ActionEvent actionEvent) {
        if (!checkForFileSelection()) {
            return;
        }
       findSourceAndDestinationPath();

        try {
            if(!confirmAction("Копировать файл?", sourcePC)) {
                return;
            }
            Files.copy(sourcePath,destinationPath);
            destinationPC.updateList(Paths.get(destinationPC.getCurrentPath()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл с таким именем уже существует", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void moveBtnAction(ActionEvent actionEvent) {
        if (!checkForFileSelection()) {
            return;
        }
        findSourceAndDestinationPath();

        try {
            if(!confirmAction("Переместить файл?", sourcePC)) {
                return;
            }
            Files.move(sourcePath,destinationPath);
            destinationPC.updateList(Paths.get(destinationPC.getCurrentPath()));
            sourcePC.updateList(Paths.get(sourcePC.getCurrentPath()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл с таким именем уже существует", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void deleteBtnAction(ActionEvent actionEvent) {
        if (!checkForFileSelection()) {
            return;
        }
        findSourceAndDestinationPath();
        
        try {
            if(!confirmAction("Удалить файл?", sourcePC)) {
                return;
            }
            Files.delete(sourcePath);
            sourcePC.updateList(Paths.get(sourcePC.getCurrentPath()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл с таким именем уже не существует", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private boolean checkForFileSelection() {
        if (leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не выбран ни один файл", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void findSourceAndDestinationPath() {
        sourcePC = null;
        destinationPC = null;

        if (leftPC.getSelectedFilename() != null) {
            sourcePC = leftPC;
            destinationPC = rightPC;
        }

        if (rightPC.getSelectedFilename() != null) {
            sourcePC = rightPC;
            destinationPC = leftPC;
        }

        sourcePath = Paths.get(sourcePC.getCurrentPath(), sourcePC.getSelectedFilename());
        destinationPath = Paths.get(destinationPC.getCurrentPath()).resolve(sourcePath.getFileName().toString());
    }

    private boolean confirmAction(String headerText, PanelController sourcePC) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение");
        confirmAlert.setHeaderText(headerText);
        confirmAlert.setContentText(sourcePC.getSelectedFilename());
        Optional<ButtonType> option = confirmAlert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL || option.get() == ButtonType.CLOSE) {
            return false;
        }
        return true;
    }

}
