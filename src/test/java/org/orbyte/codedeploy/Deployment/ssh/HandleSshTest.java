package org.orbyte.codedeploy.Deployment.ssh;

import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
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


        assertDoesNotThrow(sshSessionManager::getAndProcessKey);

        // Add assertions to verify the expected behavior
        // For example, you can check if the SSH connection was established successfully
        // or if the expected output was produced.
    }

    @Test
    public void testCreateConnection() {
        String userName = "ubuntu";
        String host = "ec2-65-0-97-221.ap-south-1.compute.amazonaws.com";
        int port = 22;
        String path = "src/main/resources/static/first.pem";

        sshSessionManager.createConnection(userName, host, port, path);
        assertEquals("", sshSessionManager.runCommands("test"));
    }

}

