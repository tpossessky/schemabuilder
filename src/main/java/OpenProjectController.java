import data.JSONParser;
import data.L4File;
import data.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 1/9/2020
 * @author Tristan
 * Dynamically created window called by ProjectPaneController. When a user selects a project to open, this window is
 * created and populated with information provided via a given Project. File I/O and creation is handled between here,
 * the Project class, and FileIOFactory
 */
public class OpenProjectController implements Initializable {

    private Project mProject;
    private FlowPane fileViewer = new FlowPane();
    private TableView projectFileTable = new TableView();
    private AnchorPane parent = new AnchorPane();
    private ObservableList<L4File> tableData = FXCollections.observableArrayList();
    private TableColumn<L4File, Boolean> selectCol = new TableColumn<>("Select");
    private TableColumn<L4File, String> fileName = new TableColumn<>("File Name");
    private TableColumn<L4File, String> fileDate = new TableColumn<>("Date");
    private Label createNew = new Label("+ Create New");
    private Label delete = new Label("Delete");
    private SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private L4File fileToBeLoaded;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectFileTable.setPlaceholder(new Label("No Files in Project"));
        projectFileTable.setStyle("-fx-font-family: Calibri Light; -fx-selection-bar: #AED6F1; -fx-selection-bar-non-focused: lightgrey;");

        File[] projectFiles = mProject.getFiles();

        for (File file : projectFiles) {
            try {
                tableData.add(new L4File(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //method to clean up initialize
        setBasics();
        Scene scene = new Scene(parent, 1024, 600);
        scene.getStylesheets().add("openProject.css");
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.setResizable(false);
        //layoutX="548.0" layoutY="25.0"
        createNew.setLayoutX(850);
        createNew.setLayoutY(25);
        createNew.setStyle("-fx-text-fill: #7c8184;");
        createNew.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> createNew.setStyle("-fx-text-fill: #2283D0;"));
        createNew.addEventHandler(MouseEvent.MOUSE_EXITED, event -> createNew.setStyle("-fx-text-fill: #7c8184;"));
        createNew.setOnMouseClicked(event -> {
            try {
                addNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //layoutX="646.0" layoutY="25.0"
        delete.setLayoutX(950);
        delete.setLayoutY(25);
        delete.setStyle("-fx-text-fill: #7c8184;");
        delete.setOnMouseClicked(event -> deleteFile());

        delete.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> delete.setStyle("-fx-text-fill: #2283D0;"));
        delete.addEventHandler(MouseEvent.MOUSE_EXITED, event -> delete.setStyle("-fx-text-fill: #7c8184;"));

        projectFileTable.setOnMouseClicked(event -> {
            if(event.getClickCount() >= 2) {
                System.out.println("Project Selected: " + projectFileTable.getSelectionModel().getSelectedIndex());
                try {
                    openFile(projectFileTable.getSelectionModel().getSelectedIndex());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        setTableDate();
        appStage.show();
    }

    /**
     * Creates basic layouts and populates fields based on static values
     */
    private void setBasics() {
        Pane titleBar = new Pane();

        titleBar.setPrefSize(1024, 75);
        titleBar.setStyle("-fx-background-color: #DCDCDC;");
        Label title = new Label(mProject.getName());
        title.setStyle("-fx-text-fill: #7c8184;");
        title.setLayoutX(11);
        title.setLayoutY(26);
        title.setFont(Font.font(18));
        titleBar.getChildren().addAll(title,createNew,delete);
        parent.getChildren().add(titleBar);

        FlowPane projectInfoPane = new FlowPane();

        projectInfoPane.setPrefSize(1024, 30);
        projectInfoPane.setLayoutY(75);
        projectInfoPane.setStyle("-fx-background-color: #333645;");

        Label fileText = new Label("Number of Files: " + mProject.getNumFiles());
        Label dateCreated = new Label("Date Created: " + mProject.getDate());

        fileText.setStyle("-fx-text-fill: #7c8184; -fx-padding: 6 20 0 6;");
        dateCreated.setStyle("-fx-text-fill: #7c8184; -fx-padding: 6 0 0 6;");
        projectInfoPane.getChildren().addAll(fileText, dateCreated);
        selectCol.setStyle("-fx-alignment: BASELINE_CENTER");
        selectCol.setResizable(false);
        selectCol.setEditable(true);
        selectCol.prefWidthProperty().bind(projectFileTable.widthProperty().multiply(.15));
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(param -> tableData.get(param).checkedProperty()));
        selectCol.setResizable(false);

        fileName.setStyle("-fx-padding: 8 0 8 8");
        fileName.prefWidthProperty().bind(projectFileTable.widthProperty().multiply(.6));
        fileDate.prefWidthProperty().bind(projectFileTable.widthProperty().multiply(0.25));
        fileDate.setStyle("-fx-alignment: BASELINE_CENTER");
        fileDate.setResizable(false);
        projectFileTable.setPrefHeight(495);
        projectFileTable.setEditable(true);
        projectFileTable.setPrefWidth(384);
        projectFileTable.setLayoutY(105);
        projectFileTable.getColumns().addAll(selectCol, fileName, fileDate);
        fileViewer.setPrefHeight(495);
        fileViewer.setPrefWidth(640);
        fileViewer.setLayoutX(384);
        fileViewer.setLayoutY(105);

        parent.getChildren().addAll(projectFileTable,projectInfoPane,fileViewer);
    }

    /**
     * Sets up the whole table for the project
     */
    private void setTableDate() {
        selectCol.setCellValueFactory(new PropertyValueFactory<>("isChecked"));
        fileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileDate.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        projectFileTable.setEditable(true);
        projectFileTable.setItems(tableData);
    }

    /**
     * Logic for adding a new file to the project
     * @throws IOException thrown because of static directories
     */
    private void addNewFile() throws IOException {
        //Initial table size for for loop
        int size = tableData.size();
        //project creation dialog
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Create New File");
        dialog.setHeaderText("Create a New File");
        dialog.setContentText("Please enter the File Name:");
        //dialog result
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().equals("")) {
            boolean matches = false;
            //make sure no current project names match entered name
            for (int i = size - 1; i >= 0; i--) {
                L4File x = tableData.get(i);
                if (x.getFileName().equals(result.get() +".txt"))
                    matches = true;
            }
            if (!matches) {
                if(!result.get().contains(".") && !result.get().contains("/") ) {
                    File x = mProject.addAndReturnFile(result.get());
                    tableData.add(new L4File(x));
                    setTableDate();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Creating File");
                    alert.setHeaderText("Error Creating File");
                    alert.setContentText("Invalid Character In File Name");
                    alert.showAndWait();
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Creating File");
                alert.setHeaderText("Error Creating File");
                alert.setContentText("File with that name already exists within this project");
                alert.showAndWait();
            }
        }
    }

    /**
     * onClickHandler for Delete Button.
     */
    private void deleteFile(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Delete File");
        alert.setHeaderText("Are You Sure You Want To Delete This File?");
        alert.setContentText("This action cannot be undone.");
        alert.getButtonTypes().add(ButtonType.CANCEL);
        alert.showAndWait();

        if(alert.getResult() == ButtonType.OK) {
            //initial starting size
            int size = tableData.size();
            //iterate through and remove if the check box is checked
            for (int i = size - 1; i >= 0; i--) {
                L4File x = tableData.get(i);

                if (x.isChecked()) {
                   mProject.removeFile(tableData.get(i).getFileName());
                   tableData.remove(i);
                }
            }
            //Update UI
            setTableDate();
        }
    }


    public OpenProjectController(Project project) {
        this.mProject = project;
    }


    private void openFile(int index) throws IOException {

       fileToBeLoaded = tableData.get(index);
       String x = fileToBeLoaded.getFileAsString();
       String[] fileContents = x.split("\n");

       if(fileContents[0].equals("")){
           //null file

       }
       else if(fileContents[2].contains("Article")){
        loadArticle(fileContents);
       }
       else if(fileContents[2].contains("ItemList")){
        loadItemList(fileContents);
       }
       else if(fileContents[2].contains("LocalBusiness")){
        loadBusiness(fileContents);
       }
    }

    /**
     * Procedurally generate UI for Local Business UI
     * @param fileContents
     */
    private void loadBusiness(String[] fileContents) {


    }

    /**
     * Procedurally generate UI for Article UI
     * @param fileContents
     */
    private void loadItemList(String[] fileContents) {
    }

    /**
     * Procedurally generate UI for Article UI
     * @param fileContents String array of each line of a file
     */
    private void loadArticle(String[] fileContents) {
        Label fileTitle = new Label();
        String x = fileToBeLoaded.getFileName();
        StringBuilder fileName = new StringBuilder();
        char[] xx = x.toCharArray();

        for(int i = 0; i < x.length(); i++){
            if(xx[i] == '.')
                break;
            else
                fileName.append(xx[i]);
        }
        fileTitle.setText(fileName.toString());
        fileTitle.setStyle("-fx-text-alignment:center; -fx-font-size: 24;");
        fileTitle.setLayoutX(200);
        fileViewer.getChildren().add(fileTitle);
        JSONParser jsonParser = new JSONParser(fileContents);


    }







}