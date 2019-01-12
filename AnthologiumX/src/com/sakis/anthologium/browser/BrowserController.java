package com.sakis.anthologium.browser;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class BrowserController implements Initializable {

    @FXML
    private TextField urlTextField;

    @FXML
    private WebView webView;

    @FXML
    private Button closeButton;

    private WebEngine webEngine;

    private StringProperty url = new SimpleStringProperty("");

    @FXML
    private void closeButtonClicked() {

        webEngine.load("");
        Stage stage = (Stage)closeButton.getScene().getWindow();
        stage.close();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEngine = webView.getEngine();
        webEngine.load(urlTextField.getText());

        url.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    url.set(newValue);
                    urlTextField.setText(url.get());
                    webEngine.load(url.get());
                }
            }
        });
    }

    public void setUrl(String url) {
        this.url.set(url);
    }
}
