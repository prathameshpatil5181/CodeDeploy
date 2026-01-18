package org.orbyte.codedeploy.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.orbyte.codedeploy.Utilities.GenerateUnique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DockerBuilderServiceTest {

    @Autowired
    DockerBuilderService dockerBuilderService;

    @Test
    public void buildProjectWithDockerTest(){

        String gitRepoUrl= "https://github.com/prathameshpatil5181/TestDeploy.git";

        String resp = dockerBuilderService.buildProjectWithDocker(gitRepoUrl,"john_doe", GenerateUnique.generateUniqueProjectId("john_doe"));

        Assertions.assertNotNull(resp);

    }

}
