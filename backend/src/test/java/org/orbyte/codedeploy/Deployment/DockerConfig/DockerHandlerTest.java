package org.orbyte.codedeploy.Deployment.DockerConfig;

import org.orbyte.codedeploy.Utilities.GetFilePath;
import org.orbyte.codedeploy.Utilities.Handlers.CommandReturnResponse;
import com.github.dockerjava.api.command.CreateContainerResponse;
import org.junit.jupiter.api.Test;
import org.orbyte.codedeploy.Docker.Handlers.DockerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DockerHandlerTest {

    @Autowired
    DockerHandler dockerHandler;

    @Test
    public void TestRunContainer(String gitUrl) throws InterruptedException {
        CreateContainerResponse container = dockerHandler.pullContainer("node:25.0.0");
        CommandReturnResponse result = dockerHandler.RunCommandsInsideContainer(container,"git clone "+gitUrl);
        String folder = GetFilePath.getFolderPath(gitUrl);
        CommandReturnResponse res = dockerHandler.RunCommandsInsideContainer(container,"cd "+ folder +" && npm install && npm run build && tar -czvf dist.tar.gz ./dist  && cp -r ./dist.tar.gz /home/node && echo done");
        CommandReturnResponse res3 = dockerHandler.RunCommandsInsideContainer(container,"chmod -R 777 /home/node/dist.tar.gz");
        dockerHandler.clearContainer(container);
    }

}
