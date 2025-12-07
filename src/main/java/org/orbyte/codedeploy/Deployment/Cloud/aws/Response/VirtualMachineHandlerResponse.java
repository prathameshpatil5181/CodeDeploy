package org.orbyte.codedeploy.Deployment.Cloud.aws.Response;


import lombok.Getter;
import org.orbyte.codedeploy.Utilities.AwsKeyPairStore;
import lombok.Builder;

@Builder
@Getter
public class VirtualMachineHandlerResponse {

    private Boolean success = null;
    private String message =  null;
    private String securityGroupId =  null;
    private String securityGroupName =null ;
    private AwsKeyPairStore awsKeyPairStore = null;
    private String ec2InstanceId;

}
