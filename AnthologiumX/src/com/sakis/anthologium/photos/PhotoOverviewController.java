package com.sakis.anthologium.photos;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sakis.anthologium.util.DbConnector;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PhotoOverviewController implements Initializable {

    private Connection connection;
    private ObservableList<PhotoActorData> photoActorDataObservableList;
    private StringProperty searchString = new SimpleStringProperty("");
    private int currentPhotoId = -1;

    @FXML
    private TilePane hBox;
    @FXML
    private ImageView bigImageView;
    @FXML
    private Button goBackButton;
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @FXML
    private TableView<PhotoActorData> photoActorDataTableView;
    @FXML
    private TableColumn<PhotoActorData, String> lastnameColumn;
    @FXML
    private TableColumn<PhotoActorData, String> firstnameColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            connection = DbConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        searchString.addListener((observable, oldValue, newValue) -> populateTilePane(newValue));

        populateTilePane(getSearchString());
        currentPhotoId = -1;

    }


    private void populateTilePane(String searchString) {

        hBox.getChildren().clear();
        String sql = "";
        if (!searchString.equals("")) {
            sql =
                    "SELECT photo.*, actor_photo.*, actor.lastname, actor.firstname, actor.nickname\n" +
                            "FROM ((photo\n" +
                            "INNER JOIN actor_photo ON photo.photo_id=actor_photo.photo_id)\n" +
                            "INNER JOIN actor ON actor_photo.actor_id=actor.actor_id)\n" +
                            "WHERE actor.lastname LIKE '%" + searchString + "%'\n" +
                            "OR actor.firstname LIKE '%" + searchString + "%'\n" +
                            "OR actor.nickname LIKE '%" + searchString + "%'";
        } else {
            sql = "SELECT * FROM photo";
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ByteArrayInputStream byteArrayInputStream = null;
            while (resultSet.next()) {
                currentPhotoId = resultSet.getInt("photo_id");
                byteArrayInputStream = (ByteArrayInputStream) resultSet.getBinaryStream("image");
                hBox.getChildren().add(createImageView(currentPhotoId, byteArrayInputStream));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private ImageView createImageView(int photo_id, ByteArrayInputStream img) {

        ImageView imageView = null;
        final Image image = new Image(img);
        imageView = new ImageView(image);
        //imageView.setFitHeight(150);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 1) {
                        bigImageView.setImage(image);
                        currentPhotoId = photo_id;
                        loadPhotoActorData(photo_id);
                    }
                }
            }
        });
        return imageView;
    }


    private void loadPhotoActorData(int photo_id) {

        photoActorDataObservableList = FXCollections.observableArrayList();
        photoActorDataObservableList.clear();
        try {
            String sql = "select actor_photo.*, actor.lastname, actor.firstname\n" +
                    "from actor_photo\n" +
                    "inner join actor on actor_photo.actor_id = actor.actor_id\n" +
                    "where photo_id=?\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, photo_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                photoActorDataObservableList.add(new PhotoActorData(
                        resultSet.getInt("actor_photo_id"),
                        resultSet.getInt("photo_id"),
                        resultSet.getInt("actor_id"),
                        resultSet.getString("lastname"),
                        resultSet.getString("firstname")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<PhotoActorData, String>("lastname"));
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<PhotoActorData, String>("firstname"));
        photoActorDataTableView.setItems(null);
        photoActorDataTableView.setItems(photoActorDataObservableList);

    }


    @FXML
    private void handleGoBackButton(ActionEvent event) {
        try {

            Stage stage = (Stage) this.goBackButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleSearchButton(ActionEvent event) {
        populateTilePane(searchField.getText());
    }


    @FXML
    private void handleReturnPressedAtSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            populateTilePane(searchField.getText());
        }
    }


    @FXML
    private void handleNewButton(ActionEvent event) {

        try {

            Stage entryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/photos/photo.fxml"));
            Pane root = (Pane) loader.load();
            PhotoController ctrl = loader.getController();
            ctrl.setPhotoId(-1);
            ctrl.setEditMode(false);
            Scene scene = new Scene(root);
            entryStage.setScene(scene);
            entryStage.setTitle("Photo");
            entryStage.initModality(Modality.APPLICATION_MODAL);
            entryStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @FXML
    private void handleEditButton(ActionEvent event) {
        try {

            Stage entryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/photos/photo.fxml"));
            Pane root = (Pane) loader.load();
            PhotoController ctrl = loader.getController();
            ctrl.setPhotoId(currentPhotoId);
            ctrl.setEditMode(true);
            Scene scene = new Scene(root);
            entryStage.setScene(scene);
            entryStage.setTitle("Photo");
            entryStage.initModality(Modality.APPLICATION_MODAL);
            entryStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
        loadPhotoActorData(currentPhotoId);
    }


    @FXML
    private void handleDeleteButton(ActionEvent event) {

        if (currentPhotoId >= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Confirm");
            alert.setHeaderText("Confirm Delete");
            alert.setContentText("Are you sure ?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                try {
                    String sql = "DELETE FROM photo WHERE photo_id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, currentPhotoId);
                    statement.execute();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                populateTilePane(getSearchString());
                bigImageView.setImage(null);
                currentPhotoId = -1;
            }
        }

    }


    public String getSearchString() {
        return searchString.get();
    }


    public StringProperty searchStringProperty() {
        return searchString;
    }


    public void setSearchString(String searchString) {
        this.searchString.set(searchString);
    }

}