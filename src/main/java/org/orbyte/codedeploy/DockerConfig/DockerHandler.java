package org.orbyte.codedeploy.DockerConfig;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DockerHandler {

    @Autowired
    private DockerClient dockerClient ;


    public void pullImage(String ImageName){
        dockerClient.pullImageCmd(ImageName).exec(new PullImageResultCallback());
    }

    public void pullContainer(String ContainerName) throws InterruptedException {
        dockerClient.pullImageCmd("node:18").start().awaitCompletion();

        // Create container
        CreateContainerResponse container = dockerClient.createContainerCmd("node:18")
                .withCmd("node", "-v") // command to run inside container
                .withName("my-node-container")
                // Optional: expose port 3000 inside container to 3000 outside
//              //  .withExposedPorts(ExposedPort.tcp(3000))
//               // .withPortBindings(new Ports(ExposedPort.tcp(3000), Ports.Binding.bindPort(3000)))
                // Optional: mount host folder
                // .withBinds(new Bind("/host/path", new Volume("/container/path")))
                .exec();

        // Start container
        dockerClient.startContainerCmd(container.getId()).exec();

        System.out.println("Container started: " + container.getId());
    }





}
