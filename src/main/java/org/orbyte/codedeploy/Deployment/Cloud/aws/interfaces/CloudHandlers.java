package org.orbyte.codedeploy.Deployment.Cloud.aws.interfaces;


import org.orbyte.codedeploy.Deployment.Cloud.aws.Response.VMStatusResponse;
import org.orbyte.codedeploy.Deployment.Cloud.aws.Response.VirtualMachineHandlerResponse;
import software.amazon.awssdk.regions.Region;

public interface CloudHandlers {

      VirtualMachineHandlerResponse setupVirtualMachine (String accessKey, String secretKey, Region region, String userID);

     VMStatusResponse checkIfInstanceIsRunning(String accessKey, String secretKey, Region region, String instanceID);
}
