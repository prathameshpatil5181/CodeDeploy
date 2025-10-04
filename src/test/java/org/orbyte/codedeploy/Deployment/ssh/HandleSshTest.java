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
        String host = "ec2-3-108-218-38.ap-south-1.compute.amazonaws.com";
        int port = 22;
        String path = "src/main/resources/static/first.pem";
        String ngfilpath = "src/main/resources/static/ngnixsetup.sh";
        String react = "src/main/resources/static/react.conf";

        sshSessionManager.createConnection(userName, host, port, path);
        sshSessionManager.uploadFile(ngfilpath, "/home/ubuntu/");
        sshSessionManager.uploadFile(ngfilpath, "/home/ubuntu/");
        sshSessionManager.runCommands("/home/ubuntu/ngnixsetup.sh");
        sshSessionManager.closeConnection();
        assertEquals("", sshSessionManager.runCommands("echo hello"));
    }

    @Test
    public void testSSHConnection() throws IOException {
        String userName = "ubuntu";
        String host = "ec2-3-108-218-38.ap-south-1.compute.amazonaws.com";
        int port = 22;
        String path = "src/main/resources/static/first.pem";
        String ngfilpath = "src/main/resources/static/ngnixsetup.sh";
        String react = "src/main/resources/static/react.conf";

        sshSessionManager.createConnection(userName, host, port, path);
        sshSessionManager.uploadFile(ngfilpath, "/home/ubuntu/");
        sshSessionManager.uploadFile(react, "/home/ubuntu/");
        sshSessionManager.runCommands("sudo chmod +x /home/ubuntu/ngnixsetup.sh");
        sshSessionManager.runCommands("/home/ubuntu/ngnixsetup.sh");
        sshSessionManager.closeConnection();
//        assertEquals("", sshSessionManager.runCommands("echo hello"));
    }

}

