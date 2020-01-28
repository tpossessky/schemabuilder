import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import data.FileIOFactory;
import data.Project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 1/9/2020
 * @author Tristan
 * Main Controller for the ProjectPane loaded via the DashboardController. From here the user can create, delete, and
 * edit projects containing Schema files. Individual projects can be loaded into a new window via a double click on
 * the table row.
 */
public class ProjectPaneController implements Initializable {

    public TableView<Project> projectTable;
    public Label deleteProj;
    public Label createProj;
    private FileIOFactory fileIOFactory = new FileIOFactory();
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    TableColumn<Project, Boolean> selectCol = new TableColumn<>("Select");
    TableColumn<Project, String> projectName = new TableColumn<>("Project");
    TableColumn<Project, String> dateCreated = new TableColumn<>("Date Created");
    TableColumn<Project, String> numFiles = new TableColumn<>("Number of Files");

    ObservableList<Project> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Get projects
        File[] files = fileIOFactory.getAllFiles("files/projects");

        String[] projectNames = new String[files.length];
        FileTime[] creationTime = new FileTime[files.length];

        for(int i = 0; i < files.length; i++){
            try {
                projectNames[i] = files[i].getName();
                creationTime[i] = (FileTime) Files.getAttribute(files[0].toPath(), "creationTime");
                //Dynamically creates project based on information from files
                tableData.add(new Project(projectNames[i],df.format(creationTime[i].toMillis())));
            }catch(IOException e){e.printStackTrace();}
        }
        tableFormatter();
        displayProjects();
    }

    /**
     * Just moving all the table formatting stuff here to clean up initialize()
     */
    private void tableFormatter(){
        Label placeHolder = new Label("No Projects Found");
        placeHolder.setStyle("-fx-font-family: Calibri Light");
        projectTable.setPlaceholder(placeHolder);
        projectTable.getColumns().addAll(selectCol, projectName, dateCreated, numFiles);
        projectTable.setStyle("-fx-font-family: Calibri Light; -fx-selection-bar: #AED6F1; -fx-selection-bar-non-focused: lightgrey;");

        selectCol.prefWidthProperty().bind(projectTable.widthProperty().multiply(.1));
        projectName.prefWidthProperty().bind(projectTable.widthProperty().multiply(0.47));
        dateCreated.prefWidthProperty().bind(projectTable.widthProperty().multiply(0.2));
        numFiles.prefWidthProperty().bind(projectTable.widthProperty().multiply(0.2));
        //Set text alignment
        dateCreated.setStyle("-fx-alignment: BASELINE_CENTER");
        numFiles.setStyle("-fx-alignment: BASELINE_CENTER");
        selectCol.setStyle("-fx-alignment: BASELINE_CENTER");
        selectCol.setResizable(false);
        projectName.setStyle("-fx-padding: 8 0 8 8");
        //Create checkbox column
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(param -> tableData.get(param).checkedProperty()));
        selectCol.setEditable(true);
        projectTable.setEditable(true);

        //Found it on stackoverflow. It works, no touch.
        tableData.addListener((ListChangeListener<Project>) c -> {
            while (c.next()) {
                if (c.wasUpdated()) {
                    System.out.println("Project "+tableData.get(c.getFrom()).getName()+" changed value to " + tableData.get(c.getFrom()).isChecked());
                }
            }
        });

        //double click to open project
        projectTable.setOnMouseClicked(event -> {
            if(event.getClickCount() >= 2) {
                System.out.println("Project Selected: " + projectTable.getSelectionModel().getSelectedItem().getName());
                openProject(projectTable.getSelectionModel().getSelectedItem());
            }
        });

        //can also click enter
        projectTable.setOnKeyPressed(event -> {
            if(event.getCode().equals( KeyCode.ENTER)){
                System.out.println("Project Selected: " + projectTable.getSelectionModel().getSelectedItem().getName());
                openProject(projectTable.getSelectionModel().getSelectedItem());
            }
        });

        projectName.setResizable(false);
        dateCreated.setResizable(false);
        numFiles.setResizable(false);

        //Mouse Hover handling
        deleteProj.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> deleteProj.setStyle("-fx-text-fill: #2283D0;"));
        deleteProj.addEventHandler(MouseEvent.MOUSE_EXITED, event -> deleteProj.setStyle("-fx-text-fill: #7c8184;"));
        createProj.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> createProj.setStyle("-fx-text-fill: #2283D0;"));
        createProj.addEventHandler(MouseEvent.MOUSE_EXITED, event -> createProj.setStyle("-fx-text-fill: #7c8184;"));
    }

    /**
     * Method to update the table UI.
     */
    private void displayProjects(){

        //Basic table creation specs from JavaFX doc
        selectCol.setCellValueFactory(new PropertyValueFactory<>("isChecked"));
        projectName.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateCreated.setCellValueFactory(new PropertyValueFactory<>("date"));
        numFiles.setCellValueFactory(new PropertyValueFactory<>("numFiles"));
        projectTable.setItems(tableData);

    }

    /**
     * Onclick handler for menu to delete selected projects
     */
    public void deleteProject(){

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Delete Project");
        alert.setHeaderText("Are You Sure You Want To Delete This Project?");
        alert.setContentText("All files within this project will also be deleted.");
        alert.getButtonTypes().add(ButtonType.CANCEL);
        alert.showAndWait();

        if(alert.getResult() == ButtonType.OK) {
            //initial starting size
            int size = tableData.size();
            //iterate through and remove if the check box is checked
            for (int i = size - 1; i >= 0; i--) {
                Project x = tableData.get(i);

                if (x.isChecked()) {
                    fileIOFactory.deleteFile("files/projects/" + tableData.get(i).getName());
                    tableData.remove(i);
                }
            }
            //Update UI
            displayProjects();
        }
    }

    /**
     * Onclick handler for menu item to create new project
     */
    public void createNewProject() {
        //Initial table size for for loop
        int size = tableData.size();
        //project creation dialog
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Create New Project");
        dialog.setHeaderText("Create a New Project");
        dialog.setContentText("Please enter the Project Name:");
        //dialog result
        Optional<String> result = dialog.showAndWait();

        Date date = new Date();
        String strDate= df.format(date);

        if (result.isPresent() && !result.get().equals("")){
            boolean matches = false;
            //make sure no current project names match entered name
            for(int i = size - 1; i >= 0; i--){
                Project x = tableData.get(i);
                if (x.getName().equals(result.get()))
                    matches = true;
            }
            //if no matches found create the new project
            if(!matches){
                System.out.println("File creation returns: " + fileIOFactory.createDirectory("files/projects/" + result.get()));
                File newProject = fileIOFactory.getFile("files/projects/" + result.get());
                tableData.add(new Project(newProject.getName(), strDate));
                displayProjects();
            }
            //match found, make a new name
            else{
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Creating Project");
                alert.setHeaderText("Error Creating Project");
                alert.setContentText("Project with that name already exists");
                alert.showAndWait();
            }

        }
        //no name entered
        else if(result.isPresent()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Creating Project");
            alert.setHeaderText("Error Creating Project");
            alert.setContentText("Please enter a name for your project");
            alert.showAndWait();
        }
    }

    public void openProject(Project project) {
        OpenProjectController controller = new OpenProjectController(project);
        controller.initialize(null, null);
    }

}