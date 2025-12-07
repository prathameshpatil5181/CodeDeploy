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

         String pemString = "-----BEGIN RSA PRIVATE KEY-----\n" +
                 "MIIEowIBAAKCAQEAzNOVl4d6EbpGgv/nL3zyaA1exk6HLvCOQTL+YSAyPCaO/Oq+\n" +
                 "qlwEnEBRyPJq0JxoKzpqux3VaKWA1/ebYweHLAzgpS9XF5zSFx6C9pUiCv0eRn0U\n" +
                 "sf8iGWvStxe9PF3MplKqERNQ7NLs9/+jZQmurAJBDRCfsPr6m6BOvEdEtkEkE4TX\n" +
                 "t2eJ9AB3RlFOQKxTGnyK4Ts+xehzVc/9xxin7NXys67MFGOAI8RP9GxVZ68DJUKX\n" +
                 "PXgF7RbKWgZMH530ENlEJljOPg8Q/4cQyH7GBGBD5htt+NW7VuL1Oj98ck5dW5I4\n" +
                 "YYsMMeAafHQw/qTBA8IVupxpILBTTnVSbYSFsQIDAQABAoIBAA5qBWj4e7qrpTKq\n" +
                 "15Yb8OMprbW+6PyAqUIlWIl1PpPFEuLxkiFjAGAOlrLRfHrZxurj3Yb1foMywnJw\n" +
                 "VlsNuJF9BoIZZsOeoZpG52h67j5fubW0L7SnA7tOPzHrVcHdx/0cbCNvfI5t+Ycq\n" +
                 "UlFV8Rjhv7ovpU1LHD+/dEsDqO46XxeL10oaaVbDboa8gFI984F/GQSJQ8t9ZdQC\n" +
                 "GNxO3pODwI1OVrQEPYWhMk3CqpPtskFCIl6Lb0PD3j0ZxoAk7bLzyLrAGD//pa4f\n" +
                 "6H7caLf7bQr91EMJn2anmcGpmUeEBqUqODiYQMQ1ugETvglkncxuTXHHGPabhWIC\n" +
                 "mJEC4cECgYEA6RIQrBRIDFSA0G9EwgI10l1ivvUzMCl8ztEMktSJAPT/VSjdowez\n" +
                 "hwgxz2ZayzJp63dIJ1T75tcFMuVglZ0FhQwa+/U8C9y3Z8PCCvUVQuQsk5AJtMr/\n" +
                 "PbHIqLHp3UyDCVOKkTj/BcfzSu5SoCm7fduGT9W/+kKo2jHf+fC7DU0CgYEA4Pov\n" +
                 "oMLHYD/7sdI35Vj69RzffDvV3CsehndyG1PTNgt4P8l9rT+XPg4Zf/JfOz5bnkmZ\n" +
                 "blD16kmgsH2/7z499yEUq0xgT87PBQ4qWERf/P+OOE5cXeB3jIFpUZGyC2+oWYQY\n" +
                 "rBvdby+dbF/aHxbtv29/1PzPZzlBpTXlWvOad/UCgYAPrghzGzYxpim4XBP7HVzz\n" +
                 "lxbClApqjg/4dfXrluLUrqbS2tn2UtlQF0wKkO9AxlZ74d13z+7We9a8stPJnPO3\n" +
                 "HLZgBwKQJbws/e17ATv58RHSp5kPkayUQPan6rg6DCCwqu8ij/wQLoB+pQPoBIa5\n" +
                 "k+A4VMACnSNw8nQIA5sB7QKBgAZf8HOMsVuP1JDwVPchs1K4s7hwdZnceuvyfIA7\n" +
                 "utNgNdMQ4YfEMoPmoGBYvMwP80aj7T4L+zvdRDlcBtE/oT/cZxqHen8obANHqHmB\n" +
                 "znp7v0rnJYTfdGO90v8lsrbSPiHzNUPDMl4RKlPEz4hr/OacwTGGzFWvcKW+xjXy\n" +
                 "iyD1AoGBAMKau/UVm0vpSU4H3fgM31kGNjmCKxPf0C1q06nj5gH0Wx3zD9bJOwPz\n" +
                 "RjZPUkfruMQTmKzIG4pDuBonM+BtImvIAOn02kCLMMDP8c4UIyL8al+g/4xTSDsI\n" +
                 "SiCKW/SuoPC+FaNS0MA2SopdC0L9i5UuQXDRl9/bX28gZr1R2GEv\n" +
                 "-----END RSA PRIVATE KEY-----";

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

