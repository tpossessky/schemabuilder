import data.FileIOFactory;
import data.L4File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 1/26/2020
 * @author Tristan Possessky
 * FXML Controller class for the Author/User pane. All base controllers are very similar in design as they all rely
 * on file I/O operations. From here the user will be able to view the various users within the application
 */
public class AuthorPaneController implements Initializable {


    public Label deleteAuthor;
    public Label createAuthor;
    public TableView<L4File> authorTable;

    private FileIOFactory fileIOFactory = new FileIOFactory();

    TableColumn<L4File, Boolean> selectCol = new TableColumn<>("Select");
    TableColumn<L4File, String> authorName = new TableColumn<>("File Name");
    TableColumn<L4File, String> dateCreated = new TableColumn<>("Date");
    ObservableList<L4File> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authorTable.setPlaceholder(new Label("No Authors"));
        File[] authorFiles = fileIOFactory.getAllFiles("files/authors");

        System.out.println(authorFiles[0].getName());
        for (File file : authorFiles) {
            try {
                tableData.add(new L4File(file));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        createAuthor.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> createAuthor.setStyle("-fx-text-fill: #2283D0;"));
        createAuthor.addEventHandler(MouseEvent.MOUSE_EXITED, event -> createAuthor.setStyle("-fx-text-fill: #7c8184;"));
        createAuthor.setOnMouseClicked(event -> { createAuthor(null);});

        deleteAuthor.setOnMouseClicked(event -> deleteAuthor(null));
        deleteAuthor.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> deleteAuthor.setStyle("-fx-text-fill: #2283D0;"));
        deleteAuthor.addEventHandler(MouseEvent.MOUSE_EXITED, event -> deleteAuthor.setStyle("-fx-text-fill: #7c8184;"));

        authorTable.setOnMouseClicked(event -> {
            if(event.getClickCount() >= 2)
                openAuthor(authorTable.getSelectionModel().getSelectedIndex());
        });

        selectCol.setStyle("-fx-alignment: BASELINE_CENTER");
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(param -> tableData.get(param).checkedProperty()));
        selectCol.setEditable(true);
        selectCol.prefWidthProperty().bind(authorTable.widthProperty().multiply(.1));

        authorName.setStyle("-fx-padding: 8 0 8 8");
        authorName.prefWidthProperty().bind(authorTable.widthProperty().multiply(.45));

        dateCreated.setStyle("-fx-alignment: BASELINE_CENTER");
        dateCreated.prefWidthProperty().bind(authorTable.widthProperty().multiply(.45));

        authorTable.setEditable(true);
        authorTable.getColumns().addAll(selectCol, authorName, dateCreated);
        authorTable.setStyle("-fx-font-family: Calibri Light; -fx-selection-bar: #AED6F1; -fx-selection-bar-non-focused: lightgrey;");


        setTableData();
    }

    /**
     * Sets up the whole table for the author
     */
    private void setTableData() {
        selectCol.setCellValueFactory(new PropertyValueFactory<>("isChecked"));
        authorName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        dateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        authorTable.setEditable(true);
        authorTable.setItems(tableData);
    }


    private void openAuthor(int selectedIndex) {

        File file = fileIOFactory.getFile("files/authors/" + tableData.get(selectedIndex).getFileName());
        OpenAuthorController authorController = new OpenAuthorController(file);
        authorController.initialize(null,null);
    }

    public void deleteAuthor(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Delete Author");
        alert.setHeaderText("Are You Sure You Want To Delete This Author?");
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
                    fileIOFactory.deleteFile("files/authors/" +tableData.get(i).getFileName());
                    tableData.remove(i);
                }
            }
            //Update UI
            setTableData();
        }
    }

    /**
     * Creates a new author file within the the files/authors directory
     * @param mouseEvent null
     */
    public void createAuthor(MouseEvent mouseEvent) {

        int size = tableData.size();
        //project creation dialog
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Create New Author");
        dialog.setHeaderText("Create a New Author");
        dialog.setContentText("Please enter the Author Name:");
        //dialog result
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().equals("")) {

            boolean matches = false;
            //make sure no current project names match entered name
            for (int i = size - 1; i >= 0; i--) {
                L4File x = tableData.get(i);
                if (x.getFileName().equals(result.get() + ".txt"))
                    matches = true;
            }
            if (!matches) {
                if (!result.get().contains(".") && !result.get().contains("/")) {
                    File x;
                    try{
                        x = fileIOFactory.createAndReturnFile("files/authors", result.get());
                        tableData.add(new L4File(x));
                        String DEFAULT_FILE_CONTENTS = "\"author\":{\n" +
                                "    \"@type\":\"Person\",\n" +
                                "    \"name\":\"$$$\",\n" +
                                "    \"alternateName\":\"$$$\",\n" +
                                "    \"sameAs\":[\n" +
                                "    ]\n" +
                                "  }";
                        fileIOFactory.setFileContents("files/authors/" + result.get()+".txt", DEFAULT_FILE_CONTENTS);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setTableData();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Creating File");
                    alert.setHeaderText("Error Creating File");
                    alert.setContentText("Invalid Character In File Name");
                    alert.showAndWait();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Creating Author");
                alert.setHeaderText("Error Creating Author");
                alert.setContentText("Author with that name already exists within this project");
                alert.showAndWait();
            }
        }
    }










}