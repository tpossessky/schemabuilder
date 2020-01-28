import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    static Scene scene;
    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Schema Builder");
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static Scene getScene(){
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
