package org.orbyte.codedeploy.Deployment.DockerConfig;

import org.junit.jupiter.api.Test;
import org.orbyte.codedeploy.DockerConfig.DockerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DockerHandlerTest {

    @Autowired
    DockerHandler dockerHandler;

    @Test
    public void TestRunContainer() throws InterruptedException {
        dockerHandler.pullContainer("node");
    }

}
