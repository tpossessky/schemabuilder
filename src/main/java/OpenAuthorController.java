import data.FileIOFactory;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * 1/26/2020
 * @author Tristan Possessky
 * UI Controller for opening an author file
 */
public class OpenAuthorController implements Initializable {

    private AnchorPane parent = new AnchorPane();
    private FlowPane fileViewer = new FlowPane();
    private FlowPane sameAsPanel = new FlowPane();
    private ArrayList<String> links = new ArrayList<>();
    private ArrayList<TextField> sameAsLinks = new ArrayList<>();
    Label authorNameLabel = new Label("Author Name: ");
    TextField editAltName = new TextField();
    TextField editName = new TextField();
    Label altName = new Label("Alternate Name: ");
    Label sameAs = new Label("Same As: ");
    Button addMore =  new Button("Add");
    private Label saveFile = new Label("Save");

    private File authorFile;
    private String authorName;



    /**
     * Sets Title and file
     * @param file file
     */
    public OpenAuthorController(File file){
        authorFile = file;
        ;
        StringBuilder name = new StringBuilder();
        for(char x : authorFile.getName().toCharArray()){
            if(x != '.')
                name.append(x);
            else
                break;
        }

        authorName = name.toString();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Scene scene = new Scene(parent, 1024, 600);
        scene.getStylesheets().add("openProject.css");
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.setResizable(false);

        fileViewer.setPrefHeight(495);
        fileViewer.setPrefWidth(64);
        fileViewer.setLayoutX(8);
        fileViewer.setLayoutY(113);
        fileViewer.setOrientation(Orientation.VERTICAL);
        fileViewer.setVgap(8);

        sameAsPanel.setLayoutX(200);
        sameAsPanel.setLayoutY(113);
        sameAsPanel.setPrefWidth(824);
        sameAsPanel.setOrientation(Orientation.HORIZONTAL);
        sameAsPanel.setHgap(8);
        sameAsPanel.setVgap(8);
        setBasics();

        addMore.setOnMouseClicked(event -> {
            if(event.getClickCount() == 1)
                addNewSameAsBox();
        });

        appStage.setOnCloseRequest(event -> {
                try {
                    saveFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                appStage.close();
        });

        appStage.show();
    }

    private void addNewSameAsBox() {
        sameAsLinks.add(new TextField());
        sameAsPanel.getChildren().add(sameAsLinks.get(sameAsLinks.size() -1));
    }

    private void setBasics() {
        Pane titleBar = new Pane();

        titleBar.setPrefSize(1024, 75);
        titleBar.setStyle("-fx-background-color: #DCDCDC;");
        Label title = new Label(authorName);
        title.setStyle("-fx-text-fill: #7c8184;");
        title.setLayoutX(11);
        title.setLayoutY(26);
        title.setFont(Font.font(18));
        titleBar.getChildren().addAll(title);
        parent.getChildren().add(titleBar);

        FlowPane projectInfoPane = new FlowPane();

        projectInfoPane.setPrefSize(1024, 30);
        projectInfoPane.setLayoutY(75);
        projectInfoPane.setStyle("-fx-background-color: #333645;");
        Label deleteInfo = new Label("If you'd like to delete a link, leave the field blank and it will not be saved to the file.");
        deleteInfo.setStyle("-fx-text-fill: white;");
        projectInfoPane.getChildren().add(deleteInfo);

        saveFile.setLayoutX(950);
        saveFile.setLayoutY(25);
        saveFile.setStyle("-fx-text-fill: #7c8184;");

        saveFile.setOnMouseClicked(event -> {
            try {
                saveFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        saveFile.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> saveFile.setStyle("-fx-text-fill: #2283D0;"));
        saveFile.addEventHandler(MouseEvent.MOUSE_EXITED, event -> saveFile.setStyle("-fx-text-fill: #7c8184;"));


        setFileViewer();
        titleBar.getChildren().add(saveFile);
        parent.getChildren().addAll(projectInfoPane,fileViewer,sameAsPanel);
    }

    /**
     * Method to handle updating the author file within storage to reflect changes in the UI.
     */
    private void saveFile() throws IOException {

        StringBuilder links = new StringBuilder();
        int count = 0;
        for(TextField x: sameAsLinks){

            if(count != sameAsLinks.size())
                if(!x.getText().equals(""))
                    links.append("\t\t\"").append(x.getText()).append("\",\n");
            else
                if(!x.getText().equals(""))
                    links.append("\t\t\"").append(x.getText()).append("\"\n");
            count++;
        }

         String FILE_CONTENTS =
                "\"author\":{\n" +
                        "    \"@type\":\"Person\",\n" +
                        "    \"name\":\""+editName.getText()+"\",\n" +
                        "    \"alternateName\":\""+editAltName.getText()+"\",\n" +
                        "    \"sameAs\":[\n" + links.toString() +
                        "    ]\n" +
                        "  }";
        FileIOFactory factory = new FileIOFactory();

            factory.setFileContents(authorFile.getPath(),FILE_CONTENTS);
    }

    private void setFileViewer(){

        authorNameLabel.setLayoutX(8);
        authorNameLabel.setLayoutY(8);
        try {
            getAltName();
            getAuthorName();
            if(!authorName.equals(""))
                getSameAs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        editName.setText(authorName);
        fileViewer.getChildren().addAll(authorNameLabel,editName,altName,editAltName);
        sameAsPanel.getChildren().addAll(sameAs,addMore);
        for (int i = 0; i < links.size(); i++) {
            String x = links.get(i);
            sameAsLinks.add(new TextField(x));
            sameAsLinks.get(i).setMinWidth(100);
            sameAsPanel.getChildren().add(sameAsLinks.get(i));
        }

    }

    /**
     * Sets authorName a string containing an authors name given an author file
     */
    public void getAuthorName() throws IOException {
        String[] fileContents = new String(Files.readAllBytes(Paths.get(authorFile.toPath().toString()))).split("\n");
        char[] fileType = fileContents[2].toCharArray();
        int count = 0;
        StringBuilder authorName = new StringBuilder();
        //pick out author name from text
        for(int i = 0; i <fileType.length; i++){
            if(fileType[i] == '"'){
                count++;
                if(count == 3){
                    for(int j = i + 1; j < fileType.length; j++){
                        if(fileType[j] != '"')
                            authorName.append(fileType[j]);
                        else
                            break;
                    }
                }
                else if(authorName.length() > 1){
                    break;
                }
            }
        }
        System.out.println("NAME " + authorName.toString());

        if(authorName.toString().equals("$$$"))
            this.authorName = "";
        else
            this.authorName = authorName.toString();
    }


    public void getAltName() throws IOException {
        String[] fileContents = new String(Files.readAllBytes(Paths.get(authorFile.toPath().toString()))).split("\n");
        char[] fileType = fileContents[3].toCharArray();
        int count = 0;
        StringBuilder authorName = new StringBuilder();
        //pick out alt name from text
        for(int i = 0; i <fileType.length; i++){
            if(fileType[i] == '"'){
                count++;
                if(count == 3){
                    for(int j = i + 1; j < fileType.length; j++){
                        if(fileType[j] != '"')
                            authorName.append(fileType[j]);
                        else
                            break;
                    }
                }
                else if(authorName.length() > 1){
                    break;
                }
            }
        }
        System.out.println("ALT NAME " + authorName.toString());
        if(authorName.toString().equals("$$$"))
            editAltName.setText("");
        else
            editAltName.setText(authorName.toString());
    }

    /**
     * Very ugly. Very slow O(n^3). Works though and input size should never be an issue
     * @throws IOException
     */
    private void getSameAs() throws IOException {
        String[] fileContents = new String(Files.readAllBytes(Paths.get(authorFile.toPath().toString()))).split("\n");
        int count;
        for(int i = 5; i < fileContents.length; i++){
            if(fileContents[i].contains("]")){
                break;
            }
            StringBuilder sameAs = new StringBuilder();
            char[] link = fileContents[i].toCharArray();
            count = 0;
            for(int j = 0; j < link.length; j++){
                if(link[j] == '"'){
                    count++;

                    if(count == 1){
                        for(int k = j+1; k < link.length; k++){
                            if(link[k] != '"')
                                sameAs.append(link[k]);
                            else {
                                links.add(sameAs.toString());
                                System.out.println(sameAs.toString());
                                break;
                            }
                        }
                    }
                }

            }
        }

    }



}
