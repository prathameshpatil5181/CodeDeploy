package org.orbyte.codedeploy.Deployment.Cloud.aws.CloudHandlers;

import org.orbyte.codedeploy.Deployment.Cloud.aws.Response.GetInstanceDetailResponse;
import org.orbyte.codedeploy.Deployment.Cloud.aws.Response.VMStatusResponse;
import org.orbyte.codedeploy.Utilities.AwsKeyPairStore;
import org.orbyte.codedeploy.Utilities.GetFilePath;
import lombok.NonNull;
import org.orbyte.codedeploy.Deployment.Cloud.aws.Response.VirtualMachineHandlerResponse;
import org.orbyte.codedeploy.Deployment.Cloud.aws.interfaces.CloudHandlers;
import org.orbyte.codedeploy.Deployment.Cloud.aws.src.Aws;
import org.orbyte.codedeploy.Log.LoggingClass;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;


public class AwsHandler implements CloudHandlers {

            public VirtualMachineHandlerResponse setupVirtualMachine(@NonNull String accessKey, @NonNull String secretKey, @NonNull Region region, @NonNull String projectId){

                try(Aws awsClient = new Aws.Builder().withAccessKey(accessKey).withSecretKey(secretKey).withRegion(region).build()) {


                    AwsKeyPairStore awsKeyPairStore = awsClient.createSshKeyPair(projectId + "_aws_keypair");

                    // store the value in db as well

                    Thread td =  Thread.ofVirtual().start(() -> {
                        try {
                            GetFilePath.createFile(
                                    "/home/ubuntu/projects/java/CodeDeploy/src/main/resources/static/"
                                            + awsKeyPairStore.getKeyPairName()+".pem",
                                    awsKeyPairStore.getKeyValue()
                            );
                        } catch (Exception e) {
                            LoggingClass.logError(AwsHandler.class, " GetFilePath.createFile", e.getMessage());
                        }
                    });


                    // create the security group with ssh and http
                    String groupName = awsClient.createSecurityGroup(projectId+"_aws_sg");

                    //create the ec2 instance

                    String Ec2Client =  awsClient.createEC2Instance(projectId+"_aws_instance",awsKeyPairStore.getKeyPairName(),groupName);

                    System.out.println("EC2 Instance ID: " + Ec2Client);

                    awsClient.closeClient();

                    return VirtualMachineHandlerResponse.builder().success(true).message("success").securityGroupName(groupName).awsKeyPairStore(awsKeyPairStore).ec2InstanceId(Ec2Client).build();


                } catch (Exception e) {
                    LoggingClass.logError(AwsHandler.class, " setupVirtualMachine", e.getMessage());
                    return  VirtualMachineHandlerResponse.builder().success(false).message(e.getMessage()).build();
                }


            }


            public VMStatusResponse checkIfInstanceIsRunning(@NonNull String accessKey, @NonNull String secretKey, @NonNull Region region, @NonNull String instanceID){

                try(Aws awsClient = new Aws.Builder().withAccessKey(accessKey).withSecretKey(secretKey).withRegion(region).build();) {

                    DescribeInstanceStatusResponse response = awsClient.checkStatus(instanceID);

                    return VMStatusResponse.builder()
                            .InstanceID(instanceID)
                            .InstanceState(response.instanceStatuses().get(0).instanceState().nameAsString())
                            .InstantStatus(response.instanceStatuses().get(0).instanceStatus().statusAsString())
                            .SystemStatus(response.instanceStatuses().get(0).systemStatus().statusAsString())
                            .build();

                } catch (Exception e) {
                    LoggingClass.logError(AwsHandler.class, " checkIfInstanceIsRunning", e.getMessage());
                    throw new RuntimeException(e);
                }

            }

            public GetInstanceDetailResponse getInstanceDetails(@NonNull String accessKey, @NonNull String secretKey, @NonNull Region region, @NonNull String instanceID){
                try(Aws awsClient = new Aws.Builder().withAccessKey(accessKey).withSecretKey(secretKey).withRegion(region).build();) {

                    DescribeInstancesResponse response = awsClient.getInstanceDetails(instanceID);

                    return GetInstanceDetailResponse.builder()
                            .privateIpAddress(response.reservations().get(0).instances().get(0).privateIpAddress())
                            .publicIpAddress(response.reservations().get(0).instances().get(0).publicDnsName())
                            .instanceId(response.reservations().get(0).instances().get(0).instanceId())
                            .build();

                } catch (Exception e) {
                    LoggingClass.logError(AwsHandler.class, " checkIfInstanceIsRunning", e.getMessage());
                    throw new RuntimeException(e);
                }
            }


}
