package org.orbyte.codedeploy.services;

import org.orbyte.codedeploy.Log.LoggingClass;
import org.orbyte.codedeploy.ssh.SshSessionManager;

import java.io.IOException;

public class SSHHandlerService {

    String userName = "ubuntu";
    String host = "ec2-43-205-217-18.ap-south-1.compute.amazonaws.com";
    int port = 22;
    String path = "src/main/resources/static/first.pem";
    String ngfilpath = "src/main/resources/static/ngnixsetup.sh";
    String react = "src/main/resources/static/react.conf";

    public boolean DeployViaSSH(String host, String pemString, String S3Url) throws Exception {


        try {
            SshSessionManager sshSessionManager = new SshSessionManager();

            sshSessionManager.createConnectionUsingString(userName, host, port, path);

            sshSessionManager.createConnectionUsingString(userName, host, port, pemString);
            sshSessionManager.uploadFile(ngfilpath, "/home/ubuntu/");
            sshSessionManager.uploadFile(react, "/home/ubuntu/");
            sshSessionManager.runCommands("sudo chmod +x /home/ubuntu/ngnixsetup.sh");
            sshSessionManager.runCommands("/home/ubuntu/ngnixsetup.sh \"" + S3Url + "\"");
            sshSessionManager.closeConnection();
            return true;
        } catch (IOException e) {
            LoggingClass.logError(SSHHandlerService.class, " DeployViaSSH", e.getMessage());
            return false;
        }
//        assertEquals("", sshSessionManager.runCommands("echo hello"));
    }

}
