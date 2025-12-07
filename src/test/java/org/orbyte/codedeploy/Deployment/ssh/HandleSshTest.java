package org.orbyte.codedeploy.Deployment.ssh;

import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.orbyte.codedeploy.ssh.SshSessionManager;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HandleSshTest {

    private SshSessionManager sshSessionManager = new SshSessionManager();

    @Test
    public void testHandleSsh() {
        // Create an instance of HandleSsh

        // Call the method to be tested

        String path = "src/main/resources/static/first.pem";


        assertDoesNotThrow(()->sshSessionManager.getAndProcessKey (path));

        // Add assertions to verify the expected behavior
        // For example, you can check if the SSH connection was established successfully
        // or if the expected output was produced.
    }

    @Test
    public void testCreateConnection() throws IOException {
        String userName = "ubuntu";
        String host = "ec2-13-201-12-180.ap-south-1.compute.amazonaws.com";
        int port = 22;
        String path = "src/main/resources/static/first.pem";
        String ngfilpath = "src/main/resources/static/ngnixsetup.sh";
        String react = "src/main/resources/static/react.conf";

       sshSessionManager.createConnection2(userName, host, port, path);
//        sshSessionManager.uploadFile(ngfilpath, "/home/ubuntu/");
//        sshSessionManager.uploadFile(ngfilpath, "/home/ubuntu/");
//        sshSessionManager.runCommands("/home/ubuntu/ngnixsetup.sh");
//        sshSessionManager.closeConnection();
        assertEquals("", sshSessionManager.runCommands("echo hello"));
    }

    @Test
    public void testSSHConnection() throws IOException {
        String userName = "ubuntu";
        String host = "ec2-13-201-12-180.ap-south-1.compute.amazonaws.com";
        int port = 22;
        String path = "src/main/resources/static/first.pem";
        String ngfilpath = "src/main/resources/static/ngnixsetup.sh";
        String react = "src/main/resources/static/react.conf";

        sshSessionManager.createConnection2(userName, host, port, path);
        sshSessionManager.uploadFile(ngfilpath, "/home/ubuntu/");
        sshSessionManager.uploadFile(react, "/home/ubuntu/");
        sshSessionManager.runCommands("sudo chmod +x /home/ubuntu/ngnixsetup.sh");
        sshSessionManager.runCommands("/home/ubuntu/ngnixsetup.sh \"https://codedeploy5181.s3.ap-south-1.amazonaws.com/prathamesh/dist.tar.gz?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251026T081320Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAXTOCQ2H265GSPFOK%2F20251026%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Expires=1800&X-Amz-Signature=36c926a9a8a38c57883a809859f016c30753c73e7f1cfb44fbc9eda894092c5a\"");
        sshSessionManager.closeConnection();
//        assertEquals("", sshSessionManager.runCommands("echo hello"));
    }

    @Test
    public void testSSHConnectionString() throws IOException {
        String userName = "ubuntu";
        String host = "ec2-43-205-217-18.ap-south-1.compute.amazonaws.com";
        int port = 22;
        String path = "src/main/resources/static/first.pem";
        String ngfilpath = "src/main/resources/static/ngnixsetup.sh";
        String react = "src/main/resources/static/react.conf";

         String pemString = "";

        sshSessionManager.createConnectionUsingString(userName, host, port, pemString);
        sshSessionManager.uploadFile(ngfilpath, "/home/ubuntu/");
        sshSessionManager.uploadFile(react, "/home/ubuntu/");
        sshSessionManager.runCommands("sudo chmod +x /home/ubuntu/ngnixsetup.sh");
        sshSessionManager.runCommands("/home/ubuntu/ngnixsetup.sh \"https://codedeploy5181.s3.ap-south-1.amazonaws.com/prathamesh/dist.tar.gz?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251026T081320Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAXTOCQ2H265GSPFOK%2F20251026%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Expires=1800&X-Amz-Signature=36c926a9a8a38c57883a809859f016c30753c73e7f1cfb44fbc9eda894092c5a\"");
        sshSessionManager.closeConnection();
//        assertEquals("", sshSessionManager.runCommands("echo hello"));
    }

    //createConnection3

}

