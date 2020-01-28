package data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.IOException;

/**
 * 1/9/2020
 * @author Tristan
 * Data Structure representing a project which is formatted as such to be able to be displayed in a TableView.
 * Properties of the class (name, numFiles, dateCreated, checked) all have getters and setters in order to be changed by
 * the UI as the user requests.
 */
public class Project {

    private SimpleStringProperty name;
    private SimpleStringProperty numFiles;
    private SimpleStringProperty dateCreated;
    private BooleanProperty checked;
    private FileIOFactory mFileFactory = new FileIOFactory();
    private String mDirectory;
    File[] allFiles;


    /**
     * Project class represents a Data Structure holding information about a user's project. This will contain information
     * such as the project name, owner, associated organization, and relative files.
     * @param projectName project name set here.
     */
    public Project(String projectName, String dateCreated){
        super();
        mDirectory = "files/projects/" + projectName;
        this.name = new SimpleStringProperty(projectName);
        this.dateCreated = new SimpleStringProperty(dateCreated);
        allFiles = mFileFactory.getAllFiles(mDirectory);

        this.numFiles = new SimpleStringProperty(String.valueOf(allFiles.length));
        this.checked = new SimpleBooleanProperty(false);
    }


    /**
     * @return the name of the project
     */
    public String getName() {
        return name.get();
    }

    public boolean isChecked() {
        return checked.get();
    }

    public void setChecked(boolean isChecked){
        this.checked.set(isChecked);
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    /**
     * @return date created in string
     */
    public String getDate() {
        return dateCreated.get();
    }

    /**
     * @return number of files in project with string
     */
    public String getNumFiles() {
        return numFiles.get();
    }

    /**
     * Adds a new file to be associated with this project. These files will be programmatically created upon this method
     * being called.
     * @param fileName name of the file.
     * @return whether file creation was successful.
     */
    public boolean addFile(String fileName) throws IOException {
        return mFileFactory.createFile(mDirectory, fileName);
    }

    public File addAndReturnFile(String file) throws IOException {
        return mFileFactory.createAndReturnFile(mDirectory, file);
    }


    public File[] getFiles(){
        return allFiles;
    }

    /**
     * Straight forward. Deletes selected projects
     */
    public void removeFile(String file){

        mFileFactory.deleteFile(mDirectory+"/"+file);
    }




}
