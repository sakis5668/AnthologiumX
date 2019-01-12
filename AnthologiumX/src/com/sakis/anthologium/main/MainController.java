package com.sakis.anthologium.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController implements Initializable {

	MainModel mainModel;

	@FXML private Button actorButton;
	@FXML private Button photoButton;
	@FXML private Button songButton;
	@FXML private Button exitButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		mainModel = new MainModel();
	}

	@FXML
	private void handleActorButton(ActionEvent event) throws Exception {
		Stage entryStage = new Stage();
		Pane root = (Pane) FXMLLoader.load(getClass().getResource("/com/sakis/anthologium/actors/actors.fxml"));
		Scene scene = new Scene(root);
		entryStage.setScene(scene);
		entryStage.setTitle("Actors");
		entryStage.initModality(Modality.APPLICATION_MODAL);
		//entryStage.initStyle(StageStyle.UNDECORATED);
		entryStage.showAndWait();
	}

	@FXML
	private void handlePhotoButton(ActionEvent event) throws Exception {
		Stage entryStage = new Stage();
		Pane root = (Pane) FXMLLoader.load(getClass().getResource("/com/sakis/anthologium/photos/photooverview.fxml"));
		Scene scene = new Scene(root);
		entryStage.setScene(scene);
		entryStage.setTitle("Photos");
		entryStage.initModality(Modality.APPLICATION_MODAL);
		entryStage.showAndWait();

	}

	@FXML
	private void handleSongButton(ActionEvent event) throws Exception {
		Stage entryStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sakis/anthologium/songs/songs.fxml"));
		Pane root = (Pane) loader.load();
		Scene scene = new Scene(root);
		entryStage.setScene(scene);
		entryStage.setTitle("Song Overview");
		entryStage.initModality(Modality.APPLICATION_MODAL);
		entryStage.showAndWait();
	}

	@FXML
	private void handleExitButton(ActionEvent event) {
		System.exit(0);
	}
}
