package org.orbyte.codedeploy.Deployment.ssh;

import Utilities.Pair;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;

import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.orbyte.codedeploy.Log.LoggingClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Collections;

import java.util.concurrent.TimeUnit;

public class SshSessionManager {


    private SshClient ssh = null;

    private ClientSession session = null;



    public SshSessionManager createConnection(String userName, String host, int port, String keyFile) {

        try {
            if(ssh == null || session==null || session.isClosed()) {
                ssh = SshClient.setUpDefaultClient();
                ssh.start();
                session = ssh.connect(userName, host, port).verify(7000).getSession();
                FileKeyPairProvider fileKeyPairProvider = new FileKeyPairProvider(Paths.get(keyFile));
                session.setKeyIdentityProvider(fileKeyPairProvider);

                LoggingClass.logMessage(SshSessionManager.class,"createConnection","Connection created ");
            }
            return this;

        } catch (Exception e) {
            LoggingClass.logError(SshSessionManager.class,"createConnection","Error in creating connection: " + e.getMessage());
        }

        return this;
    }


    public SshSessionManager getSshClient() {
        if (ssh == null) {
            ssh = SshClient.setUpDefaultClient();
            return this;
        }
        return this;
    }



    public String runCommands(String command){

        try{

            if(session.isOpen()){
                try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                     ByteArrayOutputStream err = new ByteArrayOutputStream();
                     ClientChannel channel = session.createExecChannel(command)) {

                    channel.setOut(out);
                    channel.setErr(err);
                    channel.open().verify(5, TimeUnit.SECONDS);
                    channel.waitFor(Collections.singleton(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(5000));

                    if (err.size() > 0) {
                        LoggingClass.logError(SshSessionManager.class,"runCommands",err.toString());
                        return "Error: " + err.toString();
                    }
                    LoggingClass.logMessage(SshSessionManager.class,"runCommands","Output: " + out.toString());
                    return out.toString();
                }

            }
            else{
                LoggingClass.logError(SshSessionManager.class,"runCommands","Session is not open");
                return null;
            }

        }
        catch(Exception e){
            LoggingClass.logError(SshSessionManager.class,"runCommands","Error in running command: " + e.getMessage());
            return null;
        }
    }

    public void CloseConnection() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
                LoggingClass.logMessage(SshSessionManager.class,"closeConnection","Connection closed ");
            }
            if (ssh != null && ssh.isOpen()) {
                ssh.stop();
            }
        } catch (IOException e) {
            LoggingClass.logError(SshSessionManager.class,"CloseConnection","Error in closing connection: " + e.getMessage());
        }
    }


    public Pair<String,String> getAndProcessKey(){
        try {
           String value = Files.readString(Paths.get("src/main/resources/static/first.pem"));

            System.out.println("Value: " + value);
            return new Pair<String,String>("first",value);
        } catch (IOException e) {
            LoggingClass.logError(SshSessionManager.class,"getAndProcessKey","File not found: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
