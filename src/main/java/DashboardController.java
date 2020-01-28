import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import sun.jvm.hotspot.runtime.linux_aarch64.LinuxAARCH64JavaThreadPDAccess;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 1/9/2020
 * @author Tristan
 * Main Controller of the application hooked up to dashboard.fxml. Called via a successful login on LoginController.
 * From here the user can select the piece of the application they'd like to access. From there the UI and business
 * logic is handed over to that individual controller.
 */
public class DashboardController implements Initializable {

    @FXML
    public BorderPane appFrame;
    public AnchorPane parent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Switches view in panel to show user selected operation
     * @param mouseEvent defined click
     * @throws IOException needed for the layouts. Thrown because of hard coded destinations within project
     */
    @FXML
    public void handleViewChanged(MouseEvent mouseEvent) throws IOException {

        String menuItemID = ((HBox) mouseEvent.getSource()).getId();

        //loads panel
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource( menuItemID + ".fxml"));
        appFrame.setCenter(loader.load());

    }


}
