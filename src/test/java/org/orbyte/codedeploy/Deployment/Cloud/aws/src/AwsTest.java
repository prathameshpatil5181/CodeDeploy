package org.orbyte.codedeploy.Deployment.Cloud.aws.src;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.orbyte.codedeploy.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.regions.Region;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class AwsTest {

    private Region region = Region.AP_SOUTH_1 ;



    @Nested
    public class TestGetEc2DetailsClass{
        

        @Autowired
        private Constants consts;


        @Test
        public void getEc2DetailsTest(){
            Aws aws = new Aws(consts.getSecretKey(),consts.getAccessKey(),region);
            assertDoesNotThrow(aws::getEc2Details);
        }
    }


    @Nested
    public class TestCreateEC2InstanceClass {
        @Autowired
        private Constants consts;

        @Test
        public void createEC2InstanceTest(){
            Aws aws = new Aws(consts.getSecretKey(),consts.getAccessKey(),region);
            assertDoesNotThrow(()->aws.createEC2Instance("fristRemoteInstance"));
        }
    }

    @Nested
    public class TestCreateEC2AsyncInstanceImplClass {
        @Autowired
        private Constants consts;

        @Test
        public void createEC2InstanceTest(){
            Aws aws = new Aws(consts.getSecretKey(),consts.getAccessKey(),region);
            assertDoesNotThrow(()->aws.createEC2InstanceAsyncImpl("fristRemoteInstance"));
        }
    }




}
