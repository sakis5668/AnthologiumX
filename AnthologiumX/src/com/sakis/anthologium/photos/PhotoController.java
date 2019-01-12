package com.sakis.anthologium.photos;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.sakis.anthologium.actorlookup.ActorLookUpController;
import com.sakis.anthologium.actorlookup.ActorLookUpData;
import com.sakis.anthologium.util.DbConnector;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PhotoController implements Initializable {

	private Connection connection;
	private IntegerProperty photoId = new SimpleIntegerProperty(-1);
	private ObservableList<PhotoActorData> photoActorDataObservableList;
	private List<File> files = null;
	private File imageFile;
	private int currentFileIndex;

	private boolean editMode;

	@FXML
	private TableView<PhotoActorData> photoActorDataTableView;
	@FXML
	private TableColumn<PhotoActorData, String> lastnameColumn;
	@FXML
	private TableColumn<PhotoActorData, String> firstnameColumn;

	@FXML
	private ImageView imageView;
	@FXML
	private Button saveButton;
	@FXML
	private Button exitButton;
	@FXML
	private Button addNameButton;
	@FXML
	private Button removeNameButton;
	@FXML
	private Button previousButton;
	@FXML
	private Button nextButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			connection = DbConnector.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		photoId.addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() >= 0) {
				getPictureFromDb(newValue.intValue());
				loadPhotoActorData(getPhotoId());
				saveButton.setDisable(true);
			}
		});
		previousButton.setDisable(true);
		nextButton.setDisable(true);
	}

	private void loadPhotoActorData(int photo_id) {

		photoActorDataObservableList = FXCollections.observableArrayList();
		photoActorDataObservableList.clear();
		try {
			String sql = "select actor_photo.*, actor.lastname, actor.firstname\n" + "from actor_photo\n"
					+ "inner join actor on actor_photo.actor_id = actor.actor_id\n" + "where photo_id=?\n";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, photo_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				photoActorDataObservableList.add(new PhotoActorData(resultSet.getInt("actor_photo_id"),
						resultSet.getInt("photo_id"), resultSet.getInt("actor_id"), resultSet.getString("lastname"),
						resultSet.getString("firstname")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		lastnameColumn.setCellValueFactory(new PropertyValueFactory<PhotoActorData, String>("lastname"));
		firstnameColumn.setCellValueFactory(new PropertyValueFactory<PhotoActorData, String>("firstname"));
		photoActorDataTableView.setItems(null);
		photoActorDataTableView.setItems(photoActorDataObservableList);

	}

	public int getPhotoId() {
		return photoId.get();
	}

	public IntegerProperty photoIdProperty() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId.set(photoId);
	}

	@FXML
	private void handlePreviousButton(ActionEvent event) throws FileNotFoundException {

		if (currentFileIndex > 0) {
			currentFileIndex--;
			imageFile = files.get(currentFileIndex);
			Image image = new Image(new FileInputStream(imageFile));
			imageView.setImage(image);
		}
	}

	@FXML
	private void handleNextButton(ActionEvent event) throws FileNotFoundException {

		if (currentFileIndex < files.size() - 1) {
			currentFileIndex++;
			imageFile = files.get(currentFileIndex);
			Image image = new Image(new FileInputStream(imageFile));
			imageView.setImage(image);
		}

	}

	@FXML
	private void handleDragOver(DragEvent event) {
		if (!isEditMode()) {
			if (event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.ANY);
			}
		}
	}

	@FXML
	private void handleDragDropped(DragEvent event) throws FileNotFoundException {
		if (!isEditMode()) {
			files = event.getDragboard().getFiles();
			if (files.size() > 0) {
				previousButton.setDisable(false);
				nextButton.setDisable(false);
			}
			currentFileIndex = 0;
			imageFile = files.get(currentFileIndex);
			Image image = new Image(new FileInputStream(imageFile));
			imageView.setImage(image);
		}
	}

	@FXML
	private void handleExitButton(ActionEvent event) {
		try {

			Stage stage = (Stage) this.exitButton.getScene().getWindow();
			stage.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleSaveButton(ActionEvent event) {
		/*
		 * try { PreparedStatement statement =
		 * connection.prepareStatement("INSERT INTO photo (image) VALUES (?)");
		 * statement.setBytes(1, imageToByteArray(imageView.getImage()));
		 * statement.execute(); statement.close(); } catch (SQLException e) {
		 * e.printStackTrace(); }
		 */

		for (File file : files) {
			try {
				PreparedStatement statement = connection.prepareStatement("INSERT INTO photo (image) VALUES (?)");
				statement.setBytes(1, readImageFromFile(file.getPath()));
				statement.execute();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@FXML
	private void handleAddNameButton(ActionEvent event) {

		// show actor lookup dialog and return actor id
		// if actor id is -1, nothing selected

		int actor_id = -1;
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/com/sakis/anthologium/actorlookup/actorlookup.fxml"));
			Pane root = (Pane) loader.load();

			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Select Actor");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			ActorLookUpController alc = loader.<ActorLookUpController>getController();
			ActorLookUpData ald = alc.getCurrentActor();

			if (ald != null) {
				actor_id = ald.getActor_id();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (actor_id > -1) {

			// look if actor_id is set for given photo_id
			// if so, do nothing
			// else insert data into database
			int rowCount = 0;
			try {
				String sql = "SELECT COUNT(*) FROM actor_photo WHERE photo_id = ? AND actor_id = ?";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, getPhotoId());
				preparedStatement.setInt(2, actor_id);
				ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					rowCount = resultSet.getInt("count(*)");
				}
				resultSet.close();
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (rowCount == 0) {
				try {
					String sql = "INSERT INTO actor_photo (photo_id, actor_id) VALUES (?, ?)";
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setInt(1, getPhotoId());
					preparedStatement.setInt(2, actor_id);
					preparedStatement.execute();
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		loadPhotoActorData(getPhotoId());
	}

	@FXML
	private void handleRemoveNameButton(ActionEvent event) {
		PhotoActorData currentData = photoActorDataTableView.getSelectionModel().getSelectedItem();
		if (currentData != null) {
			String sql = "DELETE FROM actor_photo WHERE photo_id = ? AND actor_id = ?";
			try {
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, currentData.getPhoto_id());
				preparedStatement.setInt(2, currentData.getActor_id());
				preparedStatement.execute();
				preparedStatement.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
			loadPhotoActorData(getPhotoId());
		}
	}

	/**
	 * Read image from file and return it as byte[]
	 *
	 * @param filename
	 * @return byte[]
	 */
	private byte[] readImageFromFile(String filename) {
		ByteArrayOutputStream bos = null;
		try {
			File f = new File(filename);
			FileInputStream fis = new FileInputStream(f);
			byte[] buffer = new byte[1024];
			bos = new ByteArrayOutputStream();
			for (int len; (len = fis.read(buffer)) != -1;) {
				bos.write(buffer, 0, len);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e2) {
			System.err.println(e2.getMessage());
		}
		return bos != null ? bos.toByteArray() : null;
	}

	/**
	 * Reads an image from the database for the given photo_id
	 *
	 * @param photo_id
	 */

	public void getPictureFromDb(int photo_id) {
		String selectSQL = "SELECT image FROM photo WHERE photo_id=?";
		ByteArrayInputStream inputStream = null;
		Image image = null;

		try {
			PreparedStatement pstmt = connection.prepareStatement(selectSQL);
			pstmt.setInt(1, photo_id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				inputStream = (ByteArrayInputStream) rs.getBinaryStream("image");
			}
			image = new Image(inputStream);
			imageView.setImage(image);
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Converts an image (from inside an imageview to a byte[], so you can save it
	 * in the database
	 *
	 * @param image
	 * @return byte[]
	 */
	@SuppressWarnings("unused")
	private byte[] imageToByteArray(Image image) {

		ByteArrayOutputStream bos = null;
		try {
			BufferedImage bImge = SwingFXUtils.fromFXImage(image, null);
			bos = new ByteArrayOutputStream();
			ImageIO.write(bImge, "PNG", bos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos != null ? bos.toByteArray() : null;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

}
