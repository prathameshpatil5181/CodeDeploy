package org.orbyte.codedeploy.Utilities;

import org.springframework.stereotype.Component;

@Component
public class FileReaderWrapper {
    public void readFile(String filePath) {
        // Implement the logic to read the file
        // For example, using BufferedReader or Files.readAllLines
        System.out.println("Reading file from: " + filePath);
    }
}
