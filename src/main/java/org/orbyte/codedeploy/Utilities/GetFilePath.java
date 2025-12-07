package org.orbyte.codedeploy.Utilities;

import org.orbyte.codedeploy.Utilities.Exception.NonValidUrlForFolderExecption;
import org.orbyte.codedeploy.Log.LoggingClass;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class GetFilePath {

    public static String getDokcerUrl(){
        return  System.getProperty("os.name").toLowerCase();
    }

    public static String getFolderPath(String gitUrl){
        Pattern pattern = Pattern.compile(".*/([^/]+?)(\\.git)?$");

            Matcher matcher = pattern.matcher(gitUrl);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new NonValidUrlForFolderExecption("Error", GetFilePath.class,"getFolderPath",gitUrl);
            }

        }

    public File zipFile(File fileToZip, String outputFileName) throws IOException {
        File zipFile = new File(outputFileName);
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            zipFileRecursive(fileToZip, fileToZip.getName(), zipOut);
        }



        return zipFile;
    }

    private void zipFileRecursive(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) return;

        if (fileToZip.isDirectory()) {
            if (!fileName.endsWith("/")) {
                fileName += "/";
            }
            zipOut.putNextEntry(new ZipEntry(fileName));
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFileRecursive(childFile, fileName + childFile.getName(), zipOut);
                }
            }
            return;
        }

        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }


    public static Boolean createFile(String filePath, String content) {
        try {
            Path path = Paths.get(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent); // create directories if missing
            }
            // Write the content (creates file if missing, truncates if exists)
            Files.writeString(path, content,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return true;
        }
        catch (IOException e) {
            LoggingClass.logError(GetFilePath.class, "createFile", e.getMessage()
            );

return false;
        }
    }

}
