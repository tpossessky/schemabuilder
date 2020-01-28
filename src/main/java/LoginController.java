import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML public PasswordField passwordField;
    @FXML public Button submitButton;
    @FXML public ImageView logoView;
    @FXML public TextField userField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image("logo.png");
        logoView.setImage(image);
        submitButton.setDisable(true);
        //Listeners for both text fields which will enable/disable log in button
        userField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!passwordField.getText().isEmpty() && !userField.getText().isEmpty())
                submitButton.setDisable(false);
            else
                submitButton.setDisable(true);

        });
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!passwordField.getText().isEmpty() && !userField.getText().isEmpty())
                submitButton.setDisable(false);
            else
                submitButton.setDisable(true);
        });
    }



    public void onLogInPressed(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        Scene scene = new Scene(parent);
        Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        appStage.setScene(scene);

        appStage.show();
    }


    public void registerUser(MouseEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("register.fxml"));
        Scene scene = new Scene(parent);
        Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.setResizable(false);

        appStage.show();
    }


}

