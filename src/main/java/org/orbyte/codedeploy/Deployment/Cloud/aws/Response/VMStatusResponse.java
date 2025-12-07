package org.orbyte.codedeploy.Deployment.Cloud.aws.Response;



// Process the response

//import software.amazon.awssdk.services.ec2.model.InstanceStatus;
//import software.amazon.awssdk.services.ec2.model.InstanceStatusDetails;for (InstanceStatus instanceStatus : response.instanceStatuses()) {
//        System.out.println("Instance ID: " + instanceStatus.instanceId());
//        System.out.println("Instance State: " + instanceStatus.instanceState().name());
//        System.out.println("System Status: " + instanceStatus.systemStatus().status());
//        System.out.println("Instance Status: " + instanceStatus.instanceStatus().status());
//
//        // You can also get details about specific status checks
//        for (
//InstanceStatusDetails details : instanceStatus.instanceStatus().details()) {
//        System.out.println("  Detail Name: " + details.name());
//        System.out.println("  Detail Status: " + details.status());
//        if (details.impairedSince() != null) {
//        System.out.println("  Impaired Since: " + details.impairedSince());
//        }
//        }
//        System.out.println("------------------------------------");
//            }

//Instance ID: i-01fb7e21884a7ccbd
//Instance State: running
//System Status: initializing
//Instance Status: initializing

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VMStatusResponse {
    String InstanceID;
    String InstantStatus;
    String SystemStatus;
    String InstanceState;

}
