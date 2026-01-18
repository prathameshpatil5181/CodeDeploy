package org.orbyte.codedeploy.ssh;

import org.orbyte.codedeploy.Utilities.Exception.SFTPConnectionException;
import org.orbyte.codedeploy.Utilities.Pair;
import lombok.NoArgsConstructor;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;

import org.apache.sshd.common.NamedResource;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.orbyte.codedeploy.Log.LoggingClass;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyPair;
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

    public SshSessionManager createConnection2(String userName, String host, int port, String keyFile) {

        try {
            if(ssh == null || session==null || session.isClosed()) {
                ssh = SshClient.setUpDefaultClient();

                String pemString = "-----BEGIN RSA PRIVATE KEY-----\n" +
                        "MIIEowIBAAKCAQEAtTaNzVq1Ob/nDebCyxLbjwqTT0Bkmch+XgLcOsnKtv2gnkfW\n" +
                        "kWncLT/qgz7W+8LZAEPFMfda5GPQHzBDanMVmN0MeT95Xfs8tuHGSnuEJi1ADRUt\n" +
                        "wSMQbOI8uZCxpGFJxdDd+0W04PSna5tSLv6UlGrcl0+N6rKVMkoTBRryLrr7Ip/1\n" +
                        "LRbY3Z0xdNSV/BwJnR/97pZqjQvqKOe5aomCHHw8S9e/YYa0tUlIhqw4+NdhaCr2\n" +
                        "u+heBTXukcVYAyczINFb/yO/Fwnp3bszYVZV8CHTpYkqtAhy/EHqfYAVSLEU4YcK\n" +
                        "X0AxpRwEWy15R7ZQputSgXKlQLiAND+gNpf3rwIDAQABAoIBADiclGxFHDywYQSM\n" +
                        "xOogOAtV6HNn0IXihufjyN1s0TGdxqHsG3JOOgTA5QifhxYb5yMYCPD1L6YOMKSt\n" +
                        "tUH6NhMNa6otVEMyC+OuYP4/3Cb5F/t+VE70H1uEyifGtlh+NEfiWpb3TezBQdRy\n" +
                        "L5iS2+QKBmQW7GUX6VhOn+aGZoaGSPyL8LCZUfyxTEsCZcE10GJMmV4UqFRRZnKB\n" +
                        "NolrWQWirIaRiN7xPS0HNG8t485uODcRY6o97f2MEXEna39W+codMVMkf57gA0i6\n" +
                        "emSq6D2Eb0lN8dKiXHQMkxGGxI+x9KlQhtv6SBb0LTmrokOtx0ZfwQpUN0Kh3LKg\n" +
                        "5zXvcIECgYEA2hLsCY3CB+yDEhvBE9hkw6H/+qgYVKa6WoVQY0sVHdDT+HaTEvHH\n" +
                        "YALBfxKD3f/BVhzg5nMDA7o8yuMhZ9YPSHTWKBc/USqaoQvC7wnw7z/WzVxm7U4/\n" +
                        "WGmOQNFj9uD/QWDRbIle9XJnKr0c7CHX4hnrC7r7t+n0qHgWdiH8/g8CgYEA1LqE\n" +
                        "KnvrumKRuF0rNKv2EmOxgnRBcAn1GXShs5E9S40OJI7yaA9+KQgALc2noV1XFIF1\n" +
                        "ytGqge+e5oyLbEDErZ2Dxlgz0W+AsAQjMMV2S3NKsH0iM2MuuFBrZxOXmpXtPTfF\n" +
                        "8bQn2uEN37icl9wSXDGJUu2CortBY0t1MEv9DGECgYBNRIkXtX+7eQTUBLt7wZ+q\n" +
                        "ZZsigC7mx5CeBECAfhkEUHravYDNJJflH/TZhoDsxvMFBQR3Auddib6CkMnEVedi\n" +
                        "X+uFlv8bTkItr9IpClexHeiJKSVbDe+J7xS1SLvnvL8Uti5eC6p2w0tkrE80J6Wr\n" +
                        "ek17AYyxFJdYnfwvbUmL5wKBgQCOj7CjCAkiD4y4kTezRMrT6Bu94Us5WnL3bEIx\n" +
                        "pNP/Hbisp1+sfTV/Ke10xK3iZjtFcr0N/xTVn1BOEUIeeglUkIsPEW0oniEP1LYA\n" +
                        "qVRtnAYh4Lpa1c0pmDtiCK9A2djzfb0aDjHnbYtusrm+y61CO7Bb8u/pPrTDZn5S\n" +
                        "r5oWAQKBgDVIE/IvhVRUkQd6IlZuBM6tHB/BnqPbP3bWndt3lySfEiI709RnXzHS\n" +
                        "seCJHiJdW+4iashpCh42c4lVX/n8pfns+/UKwC8h8XEcgoBUe37rNgTz6wR7fBnb\n" +
                        "fbTK0FJRmpT/hKwCwZFR66mq8TlsXAco+6kw/tXg1qZlnQFJGUPb\n" +
                        "-----END RSA PRIVATE KEY-----";

                ByteArrayInputStream pemStream = new ByteArrayInputStream(pemString.getBytes(StandardCharsets.UTF_8));

                InputStream inputStream = new BufferedInputStream(pemStream);

                // FilePasswordProvider: null if PEM is unencrypted
                // Load key pairs
                Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(
                  null,NamedResource.ofName("test-key"), inputStream, null
                );

                FileKeyPairProvider fileKeyPairProvider = new FileKeyPairProvider(Paths.get(keyFile));



                ssh.setKeyIdentityProvider(fileKeyPairProvider);
                ssh.start();


                session = ssh.connect(userName, host, port).verify(7000).getSession();

                session.addPublicKeyIdentity(keyPairs.iterator().next());
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

    public SshSessionManager createConnectionUsingString(String userName, String host, int port, String pemString) {
        try {
            if (ssh == null || session == null || session.isClosed()) {

                ssh = SshClient.setUpDefaultClient();
                ssh.start();

                // Convert PEM string to InputStream
                ByteArrayInputStream pemStream =
                        new ByteArrayInputStream(pemString.getBytes(StandardCharsets.UTF_8));

                // Load RSA keypair from PEM string
                Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(
                        null,
                        NamedResource.ofName("in-memory-key"),
                        new BufferedInputStream(pemStream),
                        null // password provider (null = for unencrypted key)
                );

                KeyPair keyPair = keyPairs.iterator().next();

                // Connect
                session = ssh.connect(userName, host, port)
                        .verify(7000)
                        .getSession();

                // Add identity from PEM STRING
                session.addPublicKeyIdentity(keyPair);

                // Authenticate
                session.auth().verify(7000);

                LoggingClass.logMessage(SshSessionManager.class, "createConnection",
                        "SSH connection created successfully using string PEM key");
            }
            return this;

        } catch (Exception e) {
            LoggingClass.logError(SshSessionManager.class, "createConnection",
                    "Error creating SSH connection: " + e.getMessage());
            return null;
        }
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
