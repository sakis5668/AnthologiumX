package com.sakis.anthologium.songs;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sakis.anthologium.browser.BrowserController;
import com.sakis.anthologium.util.DbConnector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SongsController implements Initializable {


    private Connection connection;
    private ObservableList<SongData> songList;
    private SongData currentSong;
    private ObservableList<UrlData> urlList;


    @FXML
    private TableView<SongData> songDataTableView;

    @FXML
    private TableColumn<SongData, String> titleTableColumn;

    @FXML
    private TableView<UrlData> urlTableView;

    @FXML
    private TableColumn<UrlData, String> urlColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Button newButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button goBackButton;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField composerTextField;

    @FXML
    private TextField lyricsTextField;

    @FXML private Label toneLabel;

    @FXML private Label scaleLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            connection = DbConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadSongData("");

        songDataTableView.getSelectionModel().select(0);
        currentSong = songDataTableView.getSelectionModel().getSelectedItem();

        songDataTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SongData>() {
            @Override
            public void changed(ObservableValue<? extends SongData> observable, SongData oldValue, SongData newValue) {
                if (newValue != null) {
                    currentSong = newValue;
                } else {
                    currentSong = oldValue;
                }
                populateFields(currentSong);
            }
        });

        if (currentSong != null) {
            populateFields(currentSong);
        }

        setFieldsEditable(false);

        // set a contextmenu for the url tableview
        MenuItem openInBrowserMenuItem = new MenuItem("Open in browser");
        openInBrowserMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openInBrowserFromContextMenu();
            }
        });
        ContextMenu cm = new ContextMenu();
        cm.getItems().addAll( openInBrowserMenuItem);
        urlTableView.setContextMenu(cm);
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


    @FXML
    void deleteButtonClicked(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Please confirm");
        alert.setContentText("Are you sure ?");

        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get() == ButtonType.OK) {
            try {
                String sql = "DELETE from songs WHERE song_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, currentSong.getSong_id());
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadSongData("");
            songDataTableView.getSelectionModel().select(0);
            currentSong = songDataTableView.getSelectionModel().getSelectedItem();
        }
    }


    @FXML
    void editButtonClicked(ActionEvent event) {
        try {
            Stage entryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/songs/songedit.fxml"));
            Pane root = (Pane) loader.load();
            SongEditController ctrl = loader.getController();
            ctrl.setSong_id(currentSong.getSong_id());
            Scene scene = new Scene(root);
            entryStage.setScene(scene);
            entryStage.setTitle("Actors");
            entryStage.initModality(Modality.APPLICATION_MODAL);
            entryStage.showAndWait();

            if (ctrl.getReturnCode() == 0) {
                currentSong = ctrl.getCurrentSong();
                try {
                    String sql = "UPDATE songs SET title = ?, composer = ?, lyrics = ?, tone = ?, scale = ? WHERE song_id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);

                    statement.setString(1, currentSong.getTitle());
                    if (currentSong.getComposer() == -1) {
                        statement.setNull(2, Types.INTEGER);
                    } else {
                        statement.setInt(2, currentSong.getComposer());
                    }
                    if (currentSong.getLyrics() == -1) {
                        statement.setNull(3, Types.INTEGER);
                    } else {
                        statement.setInt(3, currentSong.getLyrics());
                    }
                    statement.setString(4, currentSong.getTone());
                    statement.setString(5, currentSong.getScale());
                    statement.setInt(6, currentSong.getSong_id());

                    statement.execute();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        loadSongData("");
        songDataTableView.getSelectionModel().select(0);
        currentSong = songDataTableView.getSelectionModel().getSelectedItem();
    }


    @FXML
    void goBackButtonClicked(ActionEvent event) {
        try {

            Stage stage = (Stage) this.goBackButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void newButtonClicked(ActionEvent event) {
        try {
            Stage entryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/songs/songedit.fxml"));
            Pane root = (Pane) loader.load();
            SongEditController ctrl = loader.<SongEditController>getController();
            ctrl.setSong_id(-1);
            Scene scene = new Scene(root);
            entryStage.setScene(scene);
            entryStage.setTitle("Songs");
            entryStage.initModality(Modality.APPLICATION_MODAL);
            entryStage.showAndWait();

            if (ctrl.getReturnCode() == 0) {
                currentSong = ctrl.getCurrentSong();

                try {
                    String sql = "INSERT INTO songs (title, composer, lyrics, tone, scale) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, currentSong.getTitle());
                    if (currentSong.getComposer() == -1) {
                        statement.setNull(2, Types.INTEGER);
                    } else {
                        statement.setInt(2, currentSong.getComposer());
                    }
                    if (currentSong.getLyrics() == -1) {
                        statement.setNull(3, Types.INTEGER);
                    } else {
                        statement.setInt(3, currentSong.getLyrics());
                    }
                    statement.setString(4, currentSong.getTone());
                    statement.setString(5, currentSong.getScale());
                    statement.execute();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadSongData("");
        songDataTableView.getSelectionModel().select(0);
        currentSong = songDataTableView.getSelectionModel().getSelectedItem();
    }


    @FXML
    void searchButtonClicked(ActionEvent event) {
        loadSongData(searchTextField.getText());
    }


    @FXML
    void searchButtonKeypressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadSongData(searchTextField.getText());
        }
    }


    private void loadSongData(String searchString) {

        songList = FXCollections.observableArrayList();
        songList.clear();

        try {
            String sql = "SELECT * FROM songs WHERE title LIKE '%" + searchString + "%'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                songList.add(new SongData(
                        resultSet.getInt("song_id"),
                        resultSet.getString("title"),
                        resultSet.getInt("composer"),
                        resultSet.getInt("lyrics"),
                        resultSet.getString("tone"),
                        resultSet.getString("scale")
                ));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        titleTableColumn.setCellValueFactory(new PropertyValueFactory<SongData, String>("title"));

        songDataTableView.setItems(null);
        songDataTableView.setItems(songList);

    }


    private void setFieldsEditable(boolean editable) {
        titleTextField.setEditable(editable);
        composerTextField.setEditable(editable);
        lyricsTextField.setEditable(editable);

    }


    private void loadUrlsForCurrentSong() {

        urlList = FXCollections.observableArrayList();
        urlList.clear();

        try {
            String sql = "SELECT * FROM song_url WHERE song_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, currentSong.getSong_id());
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


    private void populateFields(SongData data) {
        titleTextField.setText(data.getTitle());
        composerTextField.setText(data.getComposerName());
        lyricsTextField.setText(data.getLyricsName());
        toneLabel.setText(data.getTone());
        scaleLabel.setText(data.getScale());
        loadUrlsForCurrentSong();
    }
}
