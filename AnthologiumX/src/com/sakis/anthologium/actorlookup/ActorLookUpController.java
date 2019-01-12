package com.sakis.anthologium.actorlookup;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.sakis.anthologium.util.DbConnector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ActorLookUpController implements Initializable{


    private Connection connection;
    private ObservableList<ActorLookUpData> actorLookUpDataList;
    private ActorLookUpData currentActor;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<ActorLookUpData> actorDataTableView;

    @FXML
    private TableColumn<ActorLookUpData, String> lastnameColumn;

    @FXML
    private TableColumn<ActorLookUpData, String> firstnameColumn;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;


    public ActorLookUpData getCurrentActor() {
        return currentActor;
    }


    public void setCurrentActor(ActorLookUpData currentActor) {
        this.currentActor = currentActor;
    }


    @FXML
    void cancelButtonClicked(ActionEvent event) {

        try {
            setCurrentActor(null);
            Stage stage = (Stage) this.cancelButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void okButtonClicked(ActionEvent event) {

        try {
            Stage stage = (Stage) this.okButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void searchButtonClicked(ActionEvent event) {
        loadActorData(searchTextField.getText());
    }


    @FXML
    void searchButtonPressed(KeyEvent event) {
        if(event.getCode()== KeyCode.ENTER) {
            loadActorData(searchTextField.getText());
        }
    }


    @FXML
    void tableViewClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            try {
                Stage stage = (Stage) this.okButton.getScene().getWindow();
                stage.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = DbConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadActorData("");

        actorDataTableView.getSelectionModel().select(0);
        currentActor = actorDataTableView.getSelectionModel().selectedItemProperty().getValue();

        actorDataTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActorLookUpData>() {
            @Override
            public void changed(ObservableValue<? extends ActorLookUpData> observable, ActorLookUpData oldValue, ActorLookUpData newValue) {
                if (newValue != null) {
                    currentActor = newValue;
                } else {
                    currentActor = oldValue;
                }
            }
        });
    }


    /**
     * Loads the tableView with the data, corresp to the searchfield
     * @param searchString
     */
    private void loadActorData(String searchString) {
        actorLookUpDataList = FXCollections.observableArrayList();
        actorLookUpDataList.clear();

        try {

            String sql = "SELECT * FROM actor WHERE lastname LIKE '%" + searchString + "%' " + "OR firstname LIKE '%" + searchString + "%'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                actorLookUpDataList.add(new ActorLookUpData(
                   resultSet.getInt("actor_id"),
                   resultSet.getString("firstname"),
                   resultSet.getString("lastname")
                ));
            }
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        lastnameColumn.setCellValueFactory(new PropertyValueFactory<ActorLookUpData, String>("lastName"));
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<ActorLookUpData, String>("firstName"));

        actorDataTableView.setItems(null);
        actorDataTableView.setItems(actorLookUpDataList);
    }


}
