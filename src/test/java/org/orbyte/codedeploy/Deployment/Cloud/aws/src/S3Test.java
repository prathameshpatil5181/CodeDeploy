package org.orbyte.codedeploy.Deployment.Cloud.aws.src;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.orbyte.codedeploy.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

@SpringBootTest
public class S3Test {

    @Autowired
    Constants constants;

    @Test
   public void testGetBucketList() {
       S3Handler s3handler = new S3Handler.Builder()
               .accessKeyId(constants.getAccessKey())
                .secretAccessKey(constants.getSecretKey()).region(constants.getRegion()).build();

       List<Bucket> lt = s3handler.getBucketList();

        lt.forEach(bucket-> System.out.println(bucket.name()));

        Assertions.assertEquals(1, lt.size());

   }

   @Test
    public void testCreatFolder() {
        S3Handler s3handler = new S3Handler.Builder()
                .accessKeyId(constants.getAccessKey())
                .secretAccessKey(constants.getSecretKey()).region(constants.getRegion()).build();

//        Assertions.assertEquals(true, s3handler.createFolder("codedeploy5181","prathamesh"));
       Assertions.assertEquals(true, s3handler.uploadFiles("codedeploy5181","prathamesh","/home/ubuntu/builds/dist.tar.gz"));





    }

    @Nested
    public class tests3Flow{

        S3Handler s3handler=new S3Handler.Builder()
                .accessKeyId(constants.getAccessKey())
                .secretAccessKey(constants.getSecretKey()).region(constants.getRegion()).build();

        @Test
        public void testCreatFolder() {

            Assertions.assertEquals(true, s3handler.uploadFiles("codedeploy5181","prathamesh","/home/ubuntu/builds/dist.tar.gz"));

        }

        @Test
        public void testPresignedUrl(){
            String url = s3handler.generatePresignedUrl("codedeploy5181","prathamesh/dist.tar.gz", 30);
            System.out.println("Presigned URL: " + url);
            Assertions.assertNotNull( url);
        }

    }

}
