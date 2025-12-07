package org.orbyte.codedeploy.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.orbyte.codedeploy.Constants;
import org.orbyte.codedeploy.repository.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeployOnCloudServiceTest {

    @Autowired
    Constants constants;

    @Autowired
    DeployOnCloudService deployOnCloudService;

    @Test
    public void  deployTest() {
//        Project project = new Project("prathamesh", constants.getAccessKey(), constants.getSecretKey(), constants.getRegion());



            Assertions.assertDoesNotThrow(()->deployOnCloudService.deploy("prathamesh", "https://github.com/prathameshpatil5181/TestDeploy.git"));

    }

}
