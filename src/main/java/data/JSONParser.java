package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 1/26/2020
 * @author Tristan Possessky
 * This class is meant to interpret JSON via a provided File object. From here, the individual JSON Objects will be
 * returned to the user via
 */
public class JSONParser {
    String[] fileContents;
    String fileType;
    /**
     * Constructor containing one file parameter which will be converted to a String array
     * @param file file to be loaded
     * @throws IOException thrown because of previous IO checks
     */
    public JSONParser(File file) throws IOException {
        //sets file contents to
        fileContents = new String(Files.readAllBytes(Paths.get(file.toPath().toString()))).split("\n");
        fileType = createFileType();
        System.out.println("JSONPARSER FILE TYPE: " + fileType);
    }


    /**
     * Constructor containing String[] containing file contents
     * @param data string array of data.
     */
    public JSONParser(String[] data){
        fileContents = data;
        fileType = createFileType();
        System.out.println("JSONPARSER FILE TYPE: " + fileType);

    }


    /**
     * First method called by constructors to establish type of file being worked on
     * @return string containing file type
     */
    private String createFileType(){
        char[] fileType = fileContents[2].toCharArray();
        StringBuilder type = new StringBuilder();
        int count = 0;

        for(int i = 0; i < fileType.length; i++){
            if(fileType[i] == '"'){
                count++;
                if(count == 3){
                    for(int j = i + 1; j < fileType.length; j++){
                       if(fileType[j] != '"')
                           type.append(fileType[j]);
                       else
                           break;
                    }
                }
                else if(type.length() > 1){
                    break;
                }
            }
        }
        return type.toString();
    }

    public String getFileType(){
        return fileType;
    }


}
