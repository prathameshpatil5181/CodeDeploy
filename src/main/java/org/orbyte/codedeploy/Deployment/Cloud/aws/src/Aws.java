package org.orbyte.codedeploy.Deployment.Cloud.aws.src;


import org.orbyte.codedeploy.Constants;
import org.orbyte.codedeploy.Log.LoggingClass;

import org.orbyte.codedeploy.Deployment.Cloud.aws.interfaces.InitAws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.concurrent.CompletableFuture;


public class Aws extends InitAws {




    private LoggingClass logger = new LoggingClass();
    private String amiId = Constants.AMI_ID;

    public Aws(String secretKey, String accessKey, Region region) {
        super(secretKey,region, accessKey);
        logger.logDebug(InitAws.class,"aws","Constructor with region");

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
            logger.logError(InitAws.class, "getEc2Details", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public String createEC2Instance( String name ) {

        Ec2Client ec2 = getAmazonEc2();

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId(amiId)
                .instanceType(InstanceType.T2_MICRO)
                .maxCount(1)
                .minCount(1)
                .build();

        RunInstancesResponse response = ec2.runInstances(runRequest);
        String instanceId = response.instances().get(0).instanceId();

        Tag tag = Tag.builder()
                .key("Name")
                .value(name)
                .build();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(tag)
                .build();

        try {
            ec2.createTags(tagRequest);
            System.out.printf(
                    "Successfully started EC2 Instance %s based on AMI %s",
                    instanceId, amiId);

            return instanceId;

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }

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
                      logger.logMessage(Aws.class,"createEC2InstanceAsyncImpl","Instance with ID is " + instance.instanceId() + " created with state" +  instance.state().name());


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

            logger.logError(InitAws.class, "createEC2InstanceAsyncImpl", e.getMessage());
        }

        CompletableFutureResponse.join();


        return "";
    }

    public String createSshKeyPair(){

        return "";
    }






}
