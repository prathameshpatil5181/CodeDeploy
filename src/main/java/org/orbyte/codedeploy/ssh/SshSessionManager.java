package org.orbyte.codedeploy.ssh;

import Utilities.Exception.SFTPConnectionException;
import Utilities.Pair;
import lombok.NoArgsConstructor;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;

import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.orbyte.codedeploy.Log.LoggingClass;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class SshSessionManager {


    private SshClient ssh = null;
    private SftpClient sftpClient = null;
    private ClientSession session = null;


    public SshSessionManager(String userName, String host, int port, String keyFile){
        try {
            if(ssh == null || session==null || session.isClosed()) {
                ssh = SshClient.setUpDefaultClient();
                FileKeyPairProvider fileKeyPairProvider = new FileKeyPairProvider(Paths.get(keyFile));
                ssh.setKeyIdentityProvider(fileKeyPairProvider);
                ssh.start();


                session = ssh.connect(userName, host, port).verify(7000).getSession();

//                session.setKeyIdentityProvider(fileKeyPairProvider);
//                session.addPublicKeyIdentity(fileKeyPairProvider);
                session.auth().verify(7000);

//                session = ssh.connect(userName, host, port).verify(7000).getSession();

                LoggingClass.logMessage(SshSessionManager.class,"createConnection","Connection created ");
            }

        } catch (Exception e) {
            LoggingClass.logError(SshSessionManager.class,"createConnection","Error in creating connection: " + e.getMessage());
        }

    }

    public SshSessionManager createConnection(String userName, String host, int port, String keyFile) {

        try {
            if(ssh == null || session==null || session.isClosed()) {
                ssh = SshClient.setUpDefaultClient();
                FileKeyPairProvider fileKeyPairProvider = new FileKeyPairProvider(Paths.get(keyFile));
                ssh.setKeyIdentityProvider(fileKeyPairProvider);
                ssh.start();


                session = ssh.connect(userName, host, port).verify(7000).getSession();

//                session.setKeyIdentityProvider(fileKeyPairProvider);
//                session.addPublicKeyIdentity(fileKeyPairProvider);
                session.auth().verify(7000);

//                session = ssh.connect(userName, host, port).verify(7000).getSession();

                LoggingClass.logMessage(SshSessionManager.class,"createConnection","Connection created");
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

                    System.out.println("hii");
                    channel.setOut(out);
                    channel.setErr(err);
                    channel.open().verify(5, TimeUnit.SECONDS);
                    System.out.println(channel.isOpen());
                    channel.waitFor(Collections.singleton(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(8000));

                    if (err.size() > 0) {
                        LoggingClass.logError(SshSessionManager.class,"runCommands",err.toString());
                        throw new RuntimeException(err.toString());
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
                if(sftpClient!=null){sftpClient.close();}
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


    public Pair<String,String> getAndProcessKey(String keyFile) {
        try {
            String value = Files.readString(Paths.get(keyFile));

            System.out.println("Value: " + value);
            return new Pair<String,String>("first",value);
        } catch (IOException e) {
            LoggingClass.logError(SshSessionManager.class,"getAndProcessKey","File not found: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public SftpClient createSftpConnection(){

        if(ssh == null || session==null || session.isClosed()) {
            throw new SFTPConnectionException("Create ssh connection first");
        }

        try{
            if(sftpClient==null || !sftpClient.isOpen()){
                SftpClientFactory factory  = SftpClientFactory.instance();
                sftpClient = factory.createSftpClient(session);
                LoggingClass.logMessage(SshSessionManager.class,"createSftpConnection","Connection created");
                return sftpClient;
            }
            return sftpClient;
        }
        catch(Exception e){
            LoggingClass.logMessage(SshSessionManager.class,"createSftpConnection","Connection failed");
            throw new SFTPConnectionException(e.getMessage());
        }


    }

    public void uploadFile(String localFilePath, String remoteDir) {
        Path path = Paths.get(localFilePath);
        String fileName = path.getFileName().toString();

        // Temporary and final remote file paths
        String remoteTempFile = (remoteDir.endsWith("/") ? remoteDir : remoteDir + "/") + fileName + ".tmp";
        String remoteFinalFile = (remoteDir.endsWith("/") ? remoteDir : remoteDir + "/") + fileName;

        SftpClient sftp2 = null;
        boolean retry = false;

        try {
            // 🔄 Try to create SFTP connection
            sftp2 = createSftpConnection();

            if (sftp2 == null || !sftp2.isOpen()) {
                LoggingClass.logError(SshSessionManager.class, "uploadFile",
                        "SFTP connection was closed. Retrying...");
                retry = true;
            } else {
                doUpload(sftp2, path, remoteTempFile, remoteFinalFile);
            }

            // 🔄 Retry once with a new connection
            if (retry) {
                try (SftpClient retrySftp = createSftpConnection()) {
                    if (retrySftp != null && retrySftp.isOpen()) {
                        doUpload(retrySftp, path, remoteTempFile, remoteFinalFile);
                    } else {
                        LoggingClass.logError(SshSessionManager.class, "uploadFile",
                                "Retry failed: Unable to establish SFTP connection.");
                    }
                }
            }
        } catch (Exception e) {
            LoggingClass.logError(SshSessionManager.class, "uploadFile",
                    "Error uploading file: " + e.getMessage());
        } finally {
            if (sftp2 != null) {
                try {
                    sftp2.close();
                } catch (IOException ignore) {}
            }
        }
    }

    private void doUpload(SftpClient sftp, Path localPath, String remoteTempFile, String remoteFinalFile) throws IOException {
        try (InputStream inputStream = Files.newInputStream(localPath);
             SftpClient.CloseableHandle handle = sftp.open(remoteTempFile,
                     SftpClient.OpenMode.Create,
                     SftpClient.OpenMode.Write,
                     SftpClient.OpenMode.Truncate)) {

            byte[] buffer = new byte[8192];
            long offset = 0;
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                sftp.write(handle, offset, buffer, 0, bytesRead);
                offset += bytesRead;
            }

            // ✅ Rename after upload completes
            sftp.rename(remoteTempFile, remoteFinalFile);

            LoggingClass.logMessage(SshSessionManager.class, "uploadFile",
                    "Uploaded " + localPath + " → " + remoteFinalFile);
        }
    }



    public void closeConnection() throws IOException {

        if(ssh == null || session==null || session.isClosed()){
            LoggingClass.logMessage(SshSessionManager.class,"closeConnection","Connection does not exist ");
            return;
        }
        if(sftpClient!=null || !sftpClient.isClosing()) sftpClient.close();
        session.close();
        ssh.stop();

    }



}
