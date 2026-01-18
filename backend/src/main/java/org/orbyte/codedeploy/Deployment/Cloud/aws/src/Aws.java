package org.orbyte.codedeploy.Deployment.Cloud.aws.src;


import org.orbyte.codedeploy.Utilities.AwsKeyPairStore;
import org.orbyte.codedeploy.Utilities.Handlers.SdkResponseHandler;
import org.orbyte.codedeploy.Constants;
import org.orbyte.codedeploy.Log.LoggingClass;

import org.orbyte.codedeploy.Deployment.Cloud.aws.interfaces.InitAws;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.concurrent.CompletableFuture;


public class Aws extends InitAws implements AutoCloseable {

    private final String amiId = Constants.AMI_ID;

    public Aws(String secretKey, String accessKey, Region region) {
        super(secretKey,region, accessKey);
        LoggingClass.logDebug(InitAws.class,"aws","Constructor with region");

    }

    public void getEc2Details(){

        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();

        try {
            DescribeInstancesResponse response = getAmazonEc2().describeInstances(request);

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    System.out.println("Instance Id is " + instance.instanceId());
                    System.out.println("Image id is "+  instance.imageId());
                    System.out.println("Instance type is "+  instance.instanceType());
                    System.out.println("Instance state name is "+  instance.state().name());
                    System.out.println("monitoring information is "+  instance.monitoring().state());

                }
            }
        } catch (Exception e) {
            LoggingClass.logError(InitAws.class, "getEc2Details", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public String createEC2Instance( String name,String keyPairName,String securityGroupName ) {

        try {
            Ec2Client ec2 = getAmazonEc2();

            RunInstancesRequest runRequest = RunInstancesRequest.builder()
                    .imageId(amiId)
                    .instanceType(InstanceType.T3_MICRO)
                    .keyName(keyPairName)
                    .maxCount(1)
                    .securityGroups(securityGroupName)
                    .minCount(1)
                    .build();

            RunInstancesResponse response = ec2.runInstances(runRequest);
            String instanceId = response.instances().getFirst().instanceId();



            LoggingClass.logMessage(Aws.class,"createEC2Instance", SdkResponseHandler.toStringResponse(response.sdkHttpResponse()));

            return instanceId;

        } catch (AwsServiceException | SdkClientException e) {
            throw new RuntimeException(e);
        }


    }


    ///  creates the ec2 instance asynchronously

    public String createEC2InstanceAsyncImpl( String name ) {

        Ec2AsyncClient ec2 = getAsyncEc2Client();

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId(amiId)
                .instanceType(InstanceType.T2_MICRO)
                .maxCount(1)
                .minCount(1)
                .build();

        CompletableFuture<RunInstancesResponse> CompletableFutureResponse = ec2.runInstances(runRequest);


        try {
            CompletableFutureResponse.whenComplete((response,error)->{

              if(error==null){
                  response.instances().forEach(instance -> {
                      LoggingClass.logMessage(Aws.class,"createEC2InstanceAsyncImpl","Instance with ID is " + instance.instanceId() + " created with state" +  instance.state().name());


                      Tag tag = Tag.builder()
                              .key("Name")
                              .value(name)
                              .build();

                      CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                              .resources(instance.instanceId())
                              .tags(tag)
                              .build();

                  });
              }
              else{
                  System.out.println(error.getMessage());
                  throw new RuntimeException(error);
              }
          });
        } catch (Exception e) {

            LoggingClass.logError(InitAws.class, "createEC2InstanceAsyncImpl", e.getMessage());
        }

        CompletableFutureResponse.join();


        return "";
    }


    public String createSecurityGroup(String groupName) {

        String securityGroupDescription = "Security group created by Java client";

        try {
            Ec2Client ec2 = getAmazonEc2();

            DescribeVpcsResponse describeVpcsResponse = ec2.describeVpcs();
            int  count = 0;

            describeVpcsResponse.vpcs().forEach(vpc -> {
                LoggingClass.logDebug(Aws.class,"createSecurityGroup","vpc "+count + " : " + vpc.vpcId());
            });

            CreateSecurityGroupRequest createRequest = CreateSecurityGroupRequest.builder()
                    .groupName(groupName)
                    .description(securityGroupDescription)
                    .vpcId(describeVpcsResponse.vpcs().getFirst().vpcId())
                    .build();

            CreateSecurityGroupResponse createResponse = ec2.createSecurityGroup(createRequest);
            String groupId = createResponse.groupId();
            LoggingClass.logDebug(Aws.class,"createSecurityGroup","Security Group created with ID: " + groupId);


            IpRange iprange = IpRange.builder().cidrIp("0.0.0.0/0").build();

            IpPermission ipPermissionssh = IpPermission.builder()
                    .ipProtocol("tcp")
                    .fromPort(22)
                    .toPort(22)
                    .ipRanges(iprange)
                    .build();

            IpPermission ipPermissionhttp = IpPermission.builder()
                    .ipProtocol("tcp")
                    .fromPort(80)
                    .toPort(80)
                    .ipRanges(iprange)
                    .build();

            AuthorizeSecurityGroupIngressRequest   request = AuthorizeSecurityGroupIngressRequest.builder()
                    .groupName(groupName)
                    .ipPermissions(ipPermissionssh,ipPermissionhttp)
                    .build();


         AuthorizeSecurityGroupIngressResponse response = ec2.authorizeSecurityGroupIngress(request);

            LoggingClass.logMessage(Aws.class,"createSecurityGroup",response.sdkHttpResponse().statusCode() + "  " + response.sdkHttpResponse().statusText() + " "+ response.sdkHttpResponse().isSuccessful());

            return groupName;

        } catch (AwsServiceException e) {
            LoggingClass.logError(InitAws.class, "createSecurityGroup", e.getMessage());
            throw new RuntimeException(e);
        } catch (SdkClientException e) {
            LoggingClass.logError(InitAws.class, "createSecurityGroup", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /// creates the ssh key pair

    public AwsKeyPairStore createSshKeyPair(String keyName){

        try {
            Ec2Client ec2 = getAmazonEc2();

            CreateKeyPairRequest keyPairRequest = CreateKeyPairRequest.builder()
                    .keyName(keyName)
                    .build();


            CreateKeyPairResponse keyPairResponse = ec2.createKeyPair(keyPairRequest);

            AwsKeyPairStore awsKeyPairStore = new AwsKeyPairStore(
                    keyPairResponse.keyName(),
                    keyPairResponse.keyFingerprint(),
                    keyPairResponse.keyMaterial()
            );


            LoggingClass.logMessage(Aws.class,"createSshKeyPair","Key pair created : " + awsKeyPairStore.toString());

            return awsKeyPairStore;
        } catch (AwsServiceException e) {
            LoggingClass.logError(InitAws.class, "createSshKeyPair", e.getMessage());
            throw new RuntimeException(e);
        } catch (SdkClientException e) {
            LoggingClass.logError(InitAws.class, "createSshKeyPair", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public DescribeInstanceStatusResponse checkStatus(String InstanceId){
        DescribeInstanceStatusRequest request = DescribeInstanceStatusRequest.builder()
                 .instanceIds(InstanceId) // Optional: specify instance ID(s)
                .includeAllInstances(false) // Include instances in all states (running, stopped, etc.)
                .build();

        try {
            // Call the describeInstanceStatus method
            DescribeInstanceStatusResponse response = getAmazonEc2().describeInstanceStatus(request);


            // Process the response
            for (InstanceStatus instanceStatus : response.instanceStatuses()) {
                LoggingClass.logMessage( Aws.class,"checkStatus","Instance ID: " + instanceStatus.instanceId());
                LoggingClass.logMessage( Aws.class,"checkStatus","Instance State: " + instanceStatus.instanceState().name());
                LoggingClass.logMessage( Aws.class,"checkStatus","System Status: " + instanceStatus.systemStatus().status());
                LoggingClass.logMessage( Aws.class,"checkStatus","Instance Status: " + instanceStatus.instanceStatus().status());

                // You can also get details about specific status checks
                for (InstanceStatusDetails details : instanceStatus.instanceStatus().details()) {
                    System.out.println("  Detail Name: " + details.name());
                    System.out.println("  Detail Status: " + details.status());
                    if (details.impairedSince() != null) {
                        System.out.println("  Impaired Since: " + details.impairedSince());
                    }
                }
                System.out.println("------------------------------------");
            }
            return response;

        } catch (Exception e) {
            System.err.println("Error describing instance status: " + e.getMessage());
            return null;
        } finally {
            getAmazonEc2().close();
        }
    }

    public DescribeInstancesResponse getInstanceDetails(String instanceId){
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().instanceIds(instanceId).build();

        try {
            DescribeInstancesResponse response = getAmazonEc2().describeInstances(request);

//            for (Reservation reservation : response.reservations()) {
//                for (Instance instance : reservation.instances()) {
//                    System.out.println("Instance Id is " + instance.instanceId());
//                    System.out.println("Image id is "+  instance.imageId());
//                    System.out.println("Instance type is "+  instance.instanceType());
//                    System.out.println("Instance state name is "+  instance.state().name());
//                    System.out.println("monitoring information is "+  instance.monitoring().state());
//
//                }
//            }

            return response;

        } catch (Exception e) {
            LoggingClass.logError(InitAws.class, "getEc2Details", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void closeClient(){
        try {
            closeEc2Client();
        } catch (Exception e) {
            LoggingClass.logError(InitAws.class, "closeClient", e.getMessage());
            throw new RuntimeException(e);

        }
    }

    @Override
    public void close() throws Exception {
        try {
            closeEc2Client();
        } catch (Exception e) {
            LoggingClass.logError(InitAws.class, "closeClient", e.getMessage());
            throw new RuntimeException(e);

        }
    }


    public static class Builder {
        private String secretKey;
        private String accessKey;
        private Region region;

        public Builder() {}

        public Builder withSecretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder withAccessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder withRegion(Region region) {
            this.region = region;
            return this;
        }

        public Aws build() {
            if (secretKey == null || accessKey == null || region == null) {
                throw new IllegalStateException("secretKey, accessKey and region must be provided");
            }
            return new Aws(secretKey, accessKey, region);
        }
    }


}
