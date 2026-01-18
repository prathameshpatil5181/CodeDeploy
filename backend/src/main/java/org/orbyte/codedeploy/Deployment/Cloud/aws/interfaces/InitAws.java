package org.orbyte.codedeploy.Deployment.Cloud.aws.interfaces;

import org.orbyte.codedeploy.Log.LoggingClass;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Getter
@Setter
 public abstract class InitAws {
    private String accessKeyId;
    private String secretAccessKey;
    private Region region;

    private static Ec2Client amazonEc2=null;


    private  Ec2AsyncClient amazonEc2Async=null;

    private static final LoggingClass loggingClass = new LoggingClass();

    public InitAws(String SecretAccessKey, Region Region, String AccessKeyId) {
      this.secretAccessKey = SecretAccessKey;
      this.region = Region;
      this.accessKeyId = AccessKeyId;
    }
    public InitAws(String secretAccessKey, String AccessKeyId) {
       this.secretAccessKey = secretAccessKey;
       this.accessKeyId = AccessKeyId;
    }
    public Ec2Client getAmazonEc2(){

       if(amazonEc2==null){
          LoggingClass.logMessage(InitAws.class,"getAmazonEc2","Creating new Ec2 client");

           try {
               StaticCredentialsProvider credentials =  StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId,secretAccessKey));
               amazonEc2 = Ec2Client.builder().credentialsProvider(credentials).region(region).build();
               return amazonEc2;
           } catch (Exception e) {
              LoggingClass.logError(InitAws.class,"getAmazonEc2","Error in creating ec2 client");
               throw new RuntimeException(e);
           }
       }
       return amazonEc2;
    }



    public Ec2AsyncClient getAsyncEc2Client() {
        // Implement async client logic here
        if(amazonEc2Async==null){
            loggingClass.logMessage(InitAws.class,"getAmazonEc2","Creating new Ec2 async client");

            try {
                StaticCredentialsProvider credentials =  StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId,secretAccessKey));
                amazonEc2Async = Ec2AsyncClient.builder().credentialsProvider(credentials).region(region).build();
                return amazonEc2Async;
            } catch (Exception e) {
                loggingClass.logError(InitAws.class,"getAmazonEc2","Error in Creating async client");
                throw new RuntimeException(e);
            }
        }
        return amazonEc2Async;
    }

    public void closeEc2Client() {
        if (amazonEc2 != null) {
            try {
                amazonEc2.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            amazonEc2 = null;
            loggingClass.logMessage(InitAws.class, "closeEc2Client", "Ec2 client closed successfully");
        }
    }

}
