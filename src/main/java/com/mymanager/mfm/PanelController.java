package com.mymanager.mfm;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {
    @FXML
    public TableView<FileInfo> filesTable;

    @FXML
    public TextField pathField;

    @FXML
    public ComboBox<String> disksBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<FileInfo, String> typeColumn = new TableColumn<>();
        typeColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getType().getName()));
        typeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFileName()));
        nameColumn.setPrefWidth(320);

        TableColumn<FileInfo, Long> sizeColumn = new TableColumn<>("Размер");
        sizeColumn.setCellValueFactory(item -> new SimpleObjectProperty(item.getValue().getSize()));
        sizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>(){
                @Override
                protected void updateItem(Long item, boolean empty) {
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1) {
                            setText("[DIR]");
                        } else {
                            setText(text);
                        }
                    }
                }
            };
        });
        sizeColumn.setPrefWidth(120);

        TableColumn<FileInfo, String> lastModifiedColumn = new TableColumn<>("Дата изменения");
        lastModifiedColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLastModified()));
        lastModifiedColumn.setPrefWidth(120);

        filesTable.getColumns().addAll(typeColumn, nameColumn, sizeColumn, lastModifiedColumn);
        filesTable.getSortOrder().add(typeColumn);

        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Path path = Paths.get(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
                    if (Files.isDirectory(path)) {
                        updateList(path);
                    }
                }
            }
        });

        updateList(Paths.get("."));
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path).map(item -> new FileInfo(item)).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось получить список файлов");
            alert.showAndWait();
        }
    }

    public void previousCatalogAction(ActionEvent actionEvent) {
        Path previousPath = Paths.get(pathField.getText()).getParent();
        if (previousPath != null) {
            updateList(previousPath);
        }
    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> currentBox = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(currentBox.getSelectionModel().getSelectedItem()));
    }

    public String getSelectedFilename() {
        if (!filesTable.isFocused()) {
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath() {
        return pathField.getText();
    }
}
