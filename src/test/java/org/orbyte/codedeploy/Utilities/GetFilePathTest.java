package org.orbyte.codedeploy.Utilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;


public class GetFilePathTest {

    @Test
    public void TestGetFilePath(){

        System.out.println(GetFilePath.getDokcerUrl());

    }



    @Nested
    public class GetFolderPathTest{
        @Test
        public void getFolderPathTest1(){

            String gitUrl = "https://github.com/prathameshpatil5181/TestDeploy.git";
            System.out.println(GetFilePath.getFolderPath(gitUrl));
            Assertions.assertEquals("TestDeploy",GetFilePath.getFolderPath(gitUrl));

        }

        @Test
        public void getFolderPathTest2(){

            String gitUrl = "";
            System.out.println(GetFilePath.getFolderPath(gitUrl));
            Assertions.assertThrows(Throwable.class,()->GetFilePath.getFolderPath(gitUrl));

        }

        @Test
        public void zipFileTest() throws Exception {

            GetFilePath getFilePath = new GetFilePath();
            File fileToZip = new File("/home/ubuntu/builds/dist");
            File zippedFile = getFilePath.zipFile(fileToZip, "/home/ubuntu/builds/dist.zip");

            Assertions.assertTrue(zippedFile.exists());
            Assertions.assertTrue(zippedFile.length() > 0);

        }

    }


}
