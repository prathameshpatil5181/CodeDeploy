package org.orbyte.codedeploy.services;

import org.orbyte.codedeploy.Deployment.Cloud.aws.CloudHandlers.AwsHandler;
import org.orbyte.codedeploy.Deployment.Cloud.aws.Response.GetInstanceDetailResponse;
import org.orbyte.codedeploy.Deployment.Cloud.aws.Response.VMStatusResponse;
import org.orbyte.codedeploy.Deployment.Cloud.aws.Response.VirtualMachineHandlerResponse;
import org.orbyte.codedeploy.Log.LoggingClass;
import org.orbyte.codedeploy.Utilities.GenerateUnique;
import org.orbyte.codedeploy.dto.AccessAndSecretKey;
import org.orbyte.codedeploy.repository.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.annotations.NotNull;
import software.amazon.awssdk.regions.Region;


    @Component
    public class DeployOnCloudService {

        int tries = 3;
        int waitTiming = 60000;
        // 1 min
        @Autowired
        private Project project;

        @Autowired
        DockerBuilderService projectBuilder;

    //    public DeployOnCloudService(Project project) {
    //        this.project = project;
    //    }

        public void deploy(@NotNull  String userName, @NotNull String gitUrl) throws Exception {

                String projectId = GenerateUnique.generateUniqueProjectId(userName);

                try{

                    AwsHandler awsHandler = new AwsHandler();

                    // get the details from DB or catche

                    LoggingClass.logMessage(DeployOnCloudService.class, "deploy", "Fetching Access and Secret Key for projectId: " + projectId);
                    AccessAndSecretKey values =  project.getAccessKeyAndSecretKey(projectId);

                VirtualMachineHandlerResponse ec2Stats =  awsHandler.setupVirtualMachine(values.getAccessKey(), values.getSecretKey(), values.getRegion(),projectId);

//                VirtualMachineHandlerResponse ec2Stats = null;

                // build it on docker

//                DockerBuilderService projectBuilder = new DockerBuilderService();

                String status = projectBuilder.buildProjectWithDocker( gitUrl,userName,projectId);

                if(status==null) throw new RuntimeException("Failed while building");

                // check if the ec2 has started

                Thread th = Thread.ofVirtual().start(()->{


                    for (int i = 0; i < tries; i++) {
                       boolean vmStatus =  checkIfVMIsRunning(values.getAccessKey(),values.getSecretKey(),values.getRegion(),ec2Stats.getEc2InstanceId(),projectId);

                       if(!vmStatus){
                           try {
                               Thread.sleep(waitTiming);// wait for 1 min
                           } catch (InterruptedException e) {
                               throw new RuntimeException(e);
                           }
                       }
                       return;
                    }

                    throw new RuntimeException("VM did not start in expected time");

                });
                th.join();

                // get the instance public dns

                GetInstanceDetailResponse publicDNS = awsHandler.getInstanceDetails(values.getAccessKey(),values.getSecretKey(),values.getRegion(),ec2Stats.getEc2InstanceId());

                SSHHandlerService sshHandlerService = new SSHHandlerService();

               boolean DeploymentStatus =  sshHandlerService.DeployViaSSH(publicDNS.getPublicIpAddress(),ec2Stats.getAwsKeyPairStore().getKeyValue(),status);


            }
            catch (Exception e){
                LoggingClass.logError(DeployOnCloudService.class, "deploy"+ projectId, e.getMessage());
            }


    }


    public boolean checkIfVMIsRunning(@NotNull String accessKey, @NotNull String secretKey, @NotNull Region region, @NotNull String instanceID, @NotNull String projectId) {

        try{

            AwsHandler awsHandler = new AwsHandler();
            VMStatusResponse resp =  awsHandler.checkIfInstanceIsRunning(accessKey,secretKey,region,instanceID);

            if(resp.getInstanceState()=="running" && resp.getInstantStatus()=="ok" && resp.getSystemStatus()=="ok"){
                return true;
            }
            return false;
        }
        catch(Exception e){
            LoggingClass.logError(DeployOnCloudService.class, "checkIfVMIsRunning"+ projectId, e.getMessage());
            return false;
        }


    }

}
