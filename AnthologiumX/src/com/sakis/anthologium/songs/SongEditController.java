package com.sakis.anthologium.songs;

import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sakis.anthologium.actorlookup.ActorLookUpController;
import com.sakis.anthologium.actorlookup.ActorLookUpData;
import com.sakis.anthologium.browser.BrowserController;
import com.sakis.anthologium.util.DbConnector;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SongEditController implements Initializable {

    final String[] tone = new String[]{
            "C", "Cb", "C#", "D", "Db", "D#", "E", "Eb", "E#", "F", "Fb", "F#", "G", "Gb", "G#", "A", "Ab", "A#", "B", "Bb", "B#"
    };

    final String[] scale = new String[]{
            "ΜΑΤΖΟΡΕ", "ΧΙΤΖΑΖ", "ΧΙΤΖΑΣΚΑΡ", "ΠΕΙΡΑΙΩΤΙΚΟΣ", "ΧΟΥΖΑΜ", "ΣΕΓΚΙΑΧ", "ΡΑΣΤ",
            "ΜΙΝΟΡΕ", "ΝΙΑΒΕΝΤ", "ΚΙΟΥΡΝΤΙ", "ΣΑΜΠΑΧ", "ΟΥΣΑΚ"
    };

    private Connection connection;
    private IntegerProperty song_id = new SimpleIntegerProperty(-1);
    private SongData currentSong;
    private ActorLookUpData composer;
    private ActorLookUpData lyrics;
    private ObservableList<UrlData> urlList;

    private int returnCode = -1;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField composerTextField;

    @FXML
    private Button composerSelectButton;

    @FXML
    private TextField lyricsTextField;

    @FXML
    private Button lyricsSelectButton;

    @FXML
    private ChoiceBox<String> toneChoiceBox;

    @FXML
    private ChoiceBox<String> scaleChoiceBox;

    @FXML
    private TableView<UrlData> urlTableView;

    @FXML
    private TableColumn<UrlData, String> urlColumn;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;


    @FXML
    private void composerSelectButtonClicked(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/actorlookup/actorlookup.fxml"));
            Pane root = (Pane) loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Select Actor");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            ActorLookUpController alc = loader.<ActorLookUpController>getController();
            composer = alc.getCurrentActor();
            composerTextField.setText(composer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void lyricsSelectButtonClicked(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/actorlookup/actorlookup.fxml"));
            Pane root = (Pane) loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Select Actor");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            ActorLookUpController alc = loader.<ActorLookUpController>getController();
            lyrics = alc.getCurrentActor();
            lyricsTextField.setText(lyrics.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void saveButtonClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Confirm New Entry");
        alert.setContentText("Please confirm your choice !");
        Optional<ButtonType> result = alert.showAndWait();
        int composerId = -1;
        int lyricsId = -1;
        if (result.get() == ButtonType.OK) {
            if (currentSong == null) {
                currentSong = new SongData(song_id.get(), titleTextField.getText(), composerId, lyricsId, toneChoiceBox.getValue(), scaleChoiceBox.getValue());
            } else {
                if (composer != null) composerId = composer.getActor_id();
                if (lyrics != null) lyricsId = lyrics.getActor_id();
                if (song_id.get() > -1) currentSong.setSong_id(song_id.get());
                currentSong.setTitle(titleTextField.getText());
                if (composerId > -1) currentSong.setComposer(composerId);
                if (lyricsId > -1) currentSong.setLyrics(lyricsId);

                currentSong.setTone(toneChoiceBox.getValue());
                currentSong.setScale(scaleChoiceBox.getValue());
            }
            returnCode = 0;
        } else
        {
            returnCode = -1;
        }
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void cancelButtonClicked(ActionEvent event) {

        returnCode = -1;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @Override

    public void initialize(URL location, ResourceBundle resources) {

        // get a conection
        try {
            connection = DbConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //populate the choiceboxes
        for (int i = 0; i < tone.length; i++) {
            toneChoiceBox.getItems().add(tone[i]);
        }

        for (int i = 0; i < scale.length; i++) {
            scaleChoiceBox.getItems().add(scale[i]);
        }

        // handle the song_id (paramter given from calling funktion)
        // and poluplate the fields
        song_id.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                populateFields();
            }
        });
        populateFields();

        // create a contextmenu for the url tableView
        MenuItem enterNewUrlMenuItem = new MenuItem("Enter new URL");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem openInBrowserMenuItem = new MenuItem("Open in browser");

        enterNewUrlMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enterNewUrlFromContextMenu();
            }
        });
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteUrlFromContextMenu();
            }
        });
        openInBrowserMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openInBrowserFromContextMenu();
            }
        });

        ContextMenu cm = new ContextMenu();
        cm.getItems().addAll(enterNewUrlMenuItem, deleteMenuItem, new SeparatorMenuItem(), openInBrowserMenuItem);
        urlTableView.setContextMenu(cm);
    }


    private void deleteUrlFromContextMenu() {
        UrlData currentData = urlTableView.getSelectionModel().getSelectedItem();
        if (currentData!=null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Please confirm");
            alert.setContentText("Are you sure that you want to delete the url :\n" + currentData.getUrl());
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.OK) {
                try {
                    String sql = "DELETE FROM song_url WHERE song_id = ? AND url = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, currentData.getSong_id());
                    statement.setString(2, currentData.getUrl());
                    statement.execute();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                loadUrlsForCurrentSong();
            }
        }
    }


    private void enterNewUrlFromContextMenu() {

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setWidth(800);
        dialog.setTitle("Enter new URL");
        dialog.setHeaderText("New URL");
        dialog.setContentText("Please enter here:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && song_id.get() == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Caution");
            alert.setContentText("Not yet possible!");
            alert.showAndWait();
        }
        if (result.isPresent() && song_id.get()>-1) {

            String sql = "INSERT INTO song_url (song_id, url) VALUES (?, ?)";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, song_id.get());
                statement.setString(2, result.get());
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadUrlsForCurrentSong();
        }
    }


    private void openInBrowserFromContextMenu() {

        UrlData currentData = urlTableView.getSelectionModel().getSelectedItem();
        if (currentData!=null) {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/browser/browser.fxml"));
                Pane root = (Pane)loader.load();
                BrowserController ctrl = loader.getController();
                ctrl.setUrl(currentData.getUrl());
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Browser");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public int getSong_id() {
        return song_id.get();
    }


    public IntegerProperty song_idProperty() {
        return song_id;
    }


    public void setSong_id(int song_id) {
        this.song_id.set(song_id);
    }


    private void populateFields() {

        String sql = "SELECT * FROM songs WHERE song_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, getSong_id());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                currentSong = new SongData(
                        rs.getInt("song_id"),
                        rs.getString("title"),
                        rs.getInt("composer"),
                        rs.getInt("lyrics"),
                        rs.getString("tone"),
                        rs.getString("scale")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (currentSong != null) {
            titleTextField.setText(currentSong.getTitle());
            composerTextField.setText(currentSong.getComposerName());
            lyricsTextField.setText(currentSong.getLyricsName());
            toneChoiceBox.setValue(currentSong.getTone());
            scaleChoiceBox.setValue(currentSong.getScale());
        }
        loadUrlsForCurrentSong();
    }


    private void loadUrlsForCurrentSong() {

        urlList = FXCollections.observableArrayList();
        urlList.clear();

        try {
            String sql = "SELECT * FROM song_url WHERE song_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, song_id.get());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                urlList.add(new UrlData(
                        resultSet.getInt("song_id"),
                        resultSet.getString("url")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        urlColumn.setCellValueFactory(new PropertyValueFactory<UrlData, String>("url"));
        urlTableView.setItems(null);
        urlTableView.setItems(urlList);
    }


    public SongData getCurrentSong() {
        return currentSong;
    }


    public int getReturnCode() {
        return returnCode;
    }


    @FXML
    private void handleDragOver(DragEvent event) {

        if (event.getDragboard().hasUrl()) {
            event.acceptTransferModes(TransferMode.LINK);
        }
    }


    @FXML
    private void handleDragDropped(DragEvent event) throws FileNotFoundException {

        try {
            String sql = "INSERT INTO song_url (song_id, url) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, song_id.get());
            statement.setString(2, event.getDragboard().getUrl());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadUrlsForCurrentSong();
    }
}
