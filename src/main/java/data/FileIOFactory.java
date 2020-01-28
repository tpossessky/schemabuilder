package data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 1/9/2020
 * @author Tristan Possessky
 * This is a generic file finding class to be used throughout the application in order to find files in directories
 * and perform basic I/O operations on them. More advanced fuctions such as editing the files will be done once they
 * are given to the user.
 */
public class FileIOFactory {

    public FileIOFactory(){
    }

    /**
     * Returns a File object to the user with a specified path
     * @param path pathname to file
     */
    public File getFile(String path){
        return new File(path);
    }

    /**
     * Returns all files in a directory
     * @param path pathway to get all files
     * @return array of files in a directory
     */
    public File[] getAllFiles(String path){
        File folder = new File(path);
        return folder.listFiles();
    }

    public boolean createFile(String directoryPath, String fileName) throws IOException {
        File newFile = new File(directoryPath + "/" + fileName);
        return newFile.createNewFile();
    }

    /**
     * Creates a new file and returns it. Called by OpenProjectController when a new file is created.
     * @param directoryPath directory in which the file exists
     * @param fileName new file name
     * @return new file created
     * @throws IOException directory name always will exist via project creation
     */
    public File createAndReturnFile(String directoryPath, String fileName) throws IOException {
        File newFile = new File(directoryPath + "/" + fileName + ".txt");
        newFile.createNewFile();
        return newFile;
    }

    /**
     * Deletes a file given a specified path
     * @param directoryPath path to the file to be deleted
     * @return boolean value whether deletion was successful
     */
    public boolean deleteFile(String directoryPath) {
        File fileToDelete = new File(directoryPath);
        String[]entries = fileToDelete.list();
        if (entries != null) {
            for(String s: entries){
                File currentFile = new File(fileToDelete.getPath(),s);
                currentFile.delete();
            }
        }
        return fileToDelete.delete();
    }

    /**
     * Creates a new directory
     * @param directoryPathWithNewDirectory directory
     * @return whether directory creation was successful
     */
    public boolean createDirectory(String directoryPathWithNewDirectory){
        File file = new File(directoryPathWithNewDirectory);
        return file.mkdir();
    }

    public void setFileContents(String filePath, String data) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(data);
        fileWriter.close();
    }


}