package org.orbyte.codedeploy.Docker.Handlers;

import org.orbyte.codedeploy.Utilities.Handlers.CommandReturnResponse;
import org.orbyte.codedeploy.Utilities.Handlers.Status;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import org.orbyte.codedeploy.Log.LoggingClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DockerHandler {

    @Autowired
    private DockerClient dockerClient ;


    public void pullImage(String ImageName){
        dockerClient.pullImageCmd(ImageName).exec(new PullImageResultCallback());
    }

    public CreateContainerResponse pullContainer(String ContainerName) throws InterruptedException {

        CreateContainerResponse container =null;

        try{

            dockerClient.pullImageCmd("node:25.0.0").start().awaitCompletion();

            String hostPath = "/home/node";
            String localPath = "/home/ubuntu/builds";


            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withBinds(new Bind(localPath, new Volume(hostPath)));


            // Create container
            container = dockerClient.createContainerCmd("node:25.0.0")
                    // command to run inside container
                    .withName("my-node-container").withAttachStderr(true)
                    .withCmd("bash")
                    .withHostConfig(hostConfig)
                    .withTty(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    // Optional: expose port 3000 inside container to 3000 outside
                    // .withExposedPorts(ExposedPort.tcp(3000))
                    // .withPortBindings(new Ports(ExposedPort.tcp(3000), Ports.Binding.bindPort(3000)))
                    // Optional: mount host folder
                    // .withBinds(new Bind("/host/path", new Volume("/container/path")))
                    .exec();


            // Start container

            System.out.println("container created");
            dockerClient.startContainerCmd(container.getId()).exec();

            return container;


// Step 2: Start exec and read output
//            dockerClient.execStartCmd(execCreateCmdResponse.getId())
//                    .exec(new ResultCallback.Adapter<Frame>() {
//                        @Override
//                        public void onNext(Frame frame) {
//                            System.out.print(new String(frame.getPayload()));
//                        }
//                    }).awaitCompletion();

//        dockerClient.startContainerCmd(container.getId()).exec();

//            System.out.println("logging");
//        dockerClient.logContainerCmd(container.getId()).withStdOut(true)
//                .withStdErr(true)
//                .withFollowStream(true)
//                .withTailAll()
//                .exec(new ResultCallback.Adapter<Frame>() {
//                    @Override
//                        public void onNext(Frame frame) {
//                        System.out.print(new String(frame.getPayload()));
//                    }
//                }).awaitCompletion();

//            System.out.println("execomresp");
//
//            LoggingClass.logMessage(DockerHandler.class,"pullContainer",RunCommandsInsideContainer(container,"git", "clone", "https://github.com/prathameshpatil5181/TestDeploy.git").toString());


        }
        catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }



    }

    public CommandReturnResponse RunCommandsInsideContainer(CreateContainerResponse container, String command){
        StringBuilder stdout = new StringBuilder();
        try{

            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(container.getId())
                    .withAttachStdout(true)
                    .withAttachStdin(true)
                    .withAttachStderr(true)
                    .withCmd("/bin/sh", "-c", command)
                    .exec();

            System.out.println("final");

            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ResultCallback.Adapter<Frame>() {
                        @Override
                        public void onNext(Frame frame) {
                                    stdout.append(new String(frame.getPayload()));
                                    LoggingClass.logMessage(DockerHandler.class,"RunCommandsInsideContainer stdout",new String(frame.getPayload()));

                        }
                    }).awaitCompletion();

            Long exitCode = dockerClient.inspectExecCmd(execCreateCmdResponse.getId())
                    .exec()
                    .getExitCodeLong();

            if (exitCode == null || exitCode != 0) {
                throw new RuntimeException(stdout.toString());
            }

            return CommandReturnResponse.builder().status(Status.SUCCESS).stdout(stdout.toString()).message(stdout.toString()).build();

        }
        catch (Exception e){
            LoggingClass.logError(DockerHandler.class,"RunCommandsInsideContainer stderr",e.getMessage());
            return CommandReturnResponse.builder().status(Status.FAILED).stderr(stdout.toString()).message(e.getMessage()).build();
        }
    }

    public void clearContainer(CreateContainerResponse container){
        if (container == null) return;

        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(container.getId()).exec();
        InspectContainerResponse.ContainerState state = containerInfo.getState();

        if (Boolean.TRUE.equals(state.getRunning())) {
            System.out.println("Container is running");
            dockerClient.stopContainerCmd(container.getId()).exec();
            dockerClient.removeContainerCmd(container.getId()).exec();
        } else {
            System.out.println("Container is not running");
            dockerClient.removeContainerCmd(container.getId()).exec();
        }
    }




}
