package org.orbyte.codedeploy.Deployment.Cloud.aws.src.CloudHandlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.orbyte.codedeploy.Constants;
import org.orbyte.codedeploy.Deployment.Cloud.aws.CloudHandlers.AwsHandler;
import org.orbyte.codedeploy.Deployment.Cloud.aws.interfaces.CloudHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AwsHandlerTests {

    @Autowired
    Constants constants;

    @Test
    public void setupVirtualMachineTest(){

        CloudHandlers cloudHandlers = new AwsHandler();

        Assertions.assertDoesNotThrow(()->cloudHandlers.setupVirtualMachine(constants.getAccessKey(),constants.getSecretKey(),constants.getRegion(),"prathamesh"));


    }

    @Test
    public void checkIfInstanceIsRunningTest(){

        CloudHandlers cloudHandlers = new AwsHandler();

        cloudHandlers.checkIfInstanceIsRunning(constants.getAccessKey(),constants.getSecretKey(),constants.getRegion(),"i-01fb7e21884a7ccbd");

    }


}
