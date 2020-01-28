package data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.nio.file.*;;

/**
 * 1/10/2020
 * @author Tristan
 * This is a backend data class that is used as a model for the TableView within the OpenProjectController
 */
public class L4File {

    private SimpleStringProperty fileName;
    private SimpleStringProperty dateCreated;
    private BooleanProperty checked;
    private File mFile;

    public L4File(File file) throws IOException {
        mFile = file;
        this.fileName = new SimpleStringProperty(file.getName());
        FileTime date = (FileTime) Files.getAttribute(file.toPath(), "creationTime");
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        this.dateCreated = new SimpleStringProperty(df.format(date.toMillis()));
        this.checked = new SimpleBooleanProperty(false);
    }


    public String getFileName() {
        return fileName.get();
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public String getDateCreated() {
        return dateCreated.get();
    }

    public SimpleStringProperty dateCreatedProperty() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated.set(dateCreated);
    }

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }


    public String getFileAsString() throws IOException {
       return new String(Files.readAllBytes(Paths.get(mFile.toPath().toString())));
    }
}
