package com.sakis.anthologium.actors;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sakis.anthologium.photos.PhotoOverviewController;
import com.sakis.anthologium.util.DatePickerConverter;
import com.sakis.anthologium.util.DbConnector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ActorsController implements Initializable {


    private Connection connection;
    private ObservableList<ActorData> actorDataObservableList;
    private ActorData currentActorData;

    ///////////////////////////////////////////////////////////////////////////
    // TableView and TableColumns
    ///////////////////////////////////////////////////////////////////////////
    @FXML private TableView<ActorData> actorDataTableView;
    @FXML private TableColumn<ActorData, String> lastNameColumn;
    @FXML private TableColumn<ActorData, String> firstNameColumn;

    ///////////////////////////////////////////////////////////////////////////
    // TextFields etc
    ///////////////////////////////////////////////////////////////////////////
    @FXML private TextField searchField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField nickNameField;
    @FXML private DatePicker bornDatePicker;
    @FXML private DatePicker diedDatePicker;
    @FXML private TextArea informationField;

    ///////////////////////////////////////////////////////////////////////////
    // Buttons
    ///////////////////////////////////////////////////////////////////////////
    @FXML private Button searchButton;
    @FXML private Button goBackButton;
    @FXML private Button newButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button showPhotoButton;


    /**
     * Overrides the method from the Initializable interface
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            connection = DbConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DatePickerConverter datePickerConverter = new DatePickerConverter("dd.MM.yyyy");
        bornDatePicker.setConverter(datePickerConverter);
        diedDatePicker.setConverter(datePickerConverter);
        bornDatePicker.setPromptText(datePickerConverter.getPattern());
        diedDatePicker.setPromptText(datePickerConverter.getPattern());

        loadActorData("");

        actorDataTableView.getSelectionModel().select(0);
        currentActorData = actorDataTableView.getSelectionModel().selectedItemProperty().getValue();
        if (currentActorData!=null) {
            populateFields(currentActorData);
        }


        actorDataTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActorData>() {
            @Override
            public void changed(ObservableValue<? extends ActorData> observable, ActorData oldValue, ActorData newValue) {
                if (newValue != null) {
                    currentActorData = newValue;
                } else {
                    currentActorData = oldValue;
                }
                populateFields(currentActorData);
            }
        });
        setFieldsEditable(false);
    }


    /**
     * sets the setDisable forall input textfields
     *
     * @param editable
     */
    private void setFieldsEditable(boolean editable) {
        lastNameField.setEditable(editable);
        firstNameField.setEditable(editable);
        nickNameField.setEditable(editable);
        bornDatePicker.setEditable(editable);
        bornDatePicker.setDisable(!editable);
        diedDatePicker.setEditable(editable);
        diedDatePicker.setDisable(!editable);
        informationField.setEditable(editable);
    }


    /**
     * Clears all textfields
     */
    private void clearAllFields() {
    	lastNameField.clear();
    	firstNameField.clear();
    	bornDatePicker.setValue(null);
    	diedDatePicker.setValue(null);
    	informationField.clear();
    	/*
        lastNameField.setText("");
        firstNameField.setText("");
        bornDatePicker.setValue(null);
        diedDatePicker.setValue(null);
        informationField.setText("");
        */
    }


    /**
     * load data from the database to the ObeservableArrayList.
     * Populate the tableview
     */
    private void loadActorData(String searchString) {

        actorDataObservableList = FXCollections.observableArrayList();
        actorDataObservableList.clear();
        try {

            String sql = "SELECT * FROM actor WHERE lastname LIKE '%" + searchString + "%' " +
                            "OR firstname LIKE '%" + searchString + "%' " +
                            "OR nickname LIKE '%" + searchString + "%'";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                actorDataObservableList.add(new ActorData(
                        resultSet.getInt("actor_id"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getString("nickname"),
                        resultSet.getString("born"),
                        resultSet.getString("died"),
                        resultSet.getString("information")
                ));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lastNameColumn.setCellValueFactory(new PropertyValueFactory<ActorData, String>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<ActorData, String>("firstName"));

        actorDataTableView.setItems(null);
        actorDataTableView.setItems(actorDataObservableList);

    }


    /**
     * Populates all textfields with the given actor data
     *
     * @param actorData
     */
    private void populateFields(ActorData actorData) {
        firstNameField.setText(actorData.getFirstName());
        lastNameField.setText(actorData.getLastName());
        nickNameField.setText(actorData.getNickName());
        bornDatePicker.setValue(bornDatePicker.getConverter().fromString(actorData.getBorn()));
        diedDatePicker.setValue(diedDatePicker.getConverter().fromString(actorData.getDied()));
        informationField.setText(actorData.getInformation());
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
    private void handleNewButton(ActionEvent event) {

        if (newButton.getText().equals("New")) {
            setFieldsEditable(true);
            clearAllFields();
            newButton.setText("Save");
            editButton.setDisable(true);
            deleteButton.setDisable(true);
            showPhotoButton.setDisable(true);
            goBackButton.setDisable(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Confirm New Entry");
            alert.setContentText("Please confirm your choice !");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {

                try {
                    String sql = "INSERT INTO actor (lastname, firstname, nickname, born, died, information) VALUES(?,?,?,?,?,?)";
                    PreparedStatement statement = connection.prepareStatement(sql);

                    statement.setString(1, lastNameField.getText());
                    statement.setString(2, firstNameField.getText());
                    statement.setString(3, nickNameField.getText());
                    statement.setString(4, bornDatePicker.getConverter().toString(bornDatePicker.getValue()));
                    statement.setString(5, diedDatePicker.getConverter().toString(diedDatePicker.getValue()));
                    statement.setString(6, informationField.getText());

                    statement.execute();
                    statement.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            loadActorData(searchField.getText());

            setFieldsEditable(false);
            newButton.setText("New");
            editButton.setDisable(false);
            deleteButton.setDisable(false);
            showPhotoButton.setDisable(false);
            goBackButton.setDisable(false);
        }
    }


    @FXML
    private void handleEditButton(ActionEvent event) {

        if (editButton.getText().equals("Edit")) {
            setFieldsEditable(true);
            editButton.setText("Save");
            newButton.setDisable(true);
            deleteButton.setDisable(true);
            showPhotoButton.setDisable(true);
            goBackButton.setDisable(true);
        } else {
            int row = actorDataTableView.getSelectionModel().selectedIndexProperty().get();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Confirm Edit");
            alert.setContentText("Please confirm your choice !");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                try {
                    String sql = "UPDATE actor SET lastname = ?, firstname = ?, nickname = ?, born = ?, died = ?, information = ? WHERE actor_id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, lastNameField.getText());
                    statement.setString(2, firstNameField.getText());
                    statement.setString(3, nickNameField.getText());
                    statement.setString(4, bornDatePicker.getConverter().toString(bornDatePicker.getValue()));
                    statement.setString(5, diedDatePicker.getConverter().toString(diedDatePicker.getValue()));
                    statement.setString(6, informationField.getText());
                    statement.setInt(7, currentActorData.getActor_id());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            loadActorData(searchField.getText());

            actorDataTableView.getSelectionModel().select(row);

            setFieldsEditable(false);
            editButton.setText("Edit");
            newButton.setDisable(false);
            deleteButton.setDisable(false);
            showPhotoButton.setDisable(false);
            goBackButton.setDisable(false);
        }

    }


    @FXML
    private void handleDeleteButton(ActionEvent event) {
        int row = actorDataTableView.getSelectionModel().selectedIndexProperty().get();

        if (row >= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Confirm");
            alert.setHeaderText("Confirm Delete");
            alert.setContentText("Are you sure ?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                try {
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM actor WHERE actor_id = ?");
                    statement.setInt(1, currentActorData.getActor_id());
                    statement.execute();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                loadActorData(searchField.getText());
                actorDataTableView.getSelectionModel().select(0);
                currentActorData = actorDataTableView.getSelectionModel().selectedItemProperty().getValue();
                if (currentActorData!=null) {
                    populateFields(currentActorData);
                }
            }
        }

    }


    @FXML
    private void handleShowPhotoButton(ActionEvent event) {
        try {
            Stage entryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/photos/photooverview.fxml"));
            Pane root = (Pane) loader.load();
            PhotoOverviewController ctrl = loader.<PhotoOverviewController>getController();
            ctrl.setSearchString(currentActorData.getLastName());
            Scene scene = new Scene(root);
            entryStage.setScene(scene);
            entryStage.setTitle("Photos");
            entryStage.initModality(Modality.APPLICATION_MODAL);
            entryStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleSearchButton(ActionEvent event) {
        loadActorData(searchField.getText());
    }


    @FXML private void handleReturnPressedAtSearch(KeyEvent event) {
        if (event.getCode()== KeyCode.ENTER) {
            loadActorData(searchField.getText());
        }
    }


}
