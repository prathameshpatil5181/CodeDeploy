package org.orbyte.codedeploy.Deployment.Cloud.aws.interfaces;

import lombok.NonNull;
import org.orbyte.codedeploy.Log.LoggingClass;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

public abstract  class S3builder {

    private final String accessKeyId;
    private final String secretAccessKey;
    private final Region region;
    private S3Client s3Client=null;
    private S3AsyncClient s3AsyncClient=null;
    private S3Presigner s3Presigner=null;

    public S3builder(@NonNull String secretKey, @NonNull String accessKey, @NonNull Region region) {
        this.accessKeyId = accessKey;
        this.secretAccessKey = secretKey;
        this.region = region;
    }

    public S3Client getS3Client(){
        if(s3Client==null){
            LoggingClass.logMessage(S3builder.class,"getS3Client","Creating new S3 client");

            try {
                AwsCredentialsProvider credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(this.accessKeyId, this.secretAccessKey));
                s3Client = S3Client.builder().credentialsProvider(credentials).region(this.region).build();
                return s3Client;
            } catch (Exception e) {
                LoggingClass.logError(S3builder.class,"getS3Client","Error in creating S3 client");
                throw new RuntimeException(e);
            }
        }
        return s3Client;
    }

    public S3AsyncClient getS3AsyncClient(){
        if(s3AsyncClient==null){
            LoggingClass.logMessage(S3builder.class,"getS3Client","Creating new S3 client");

            try {
                AwsCredentialsProvider credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(this.accessKeyId, this.secretAccessKey));
                s3AsyncClient = S3AsyncClient.builder().credentialsProvider(credentials).region(this.region).build();
                return s3AsyncClient;
            } catch (Exception e) {
                LoggingClass.logError(S3builder.class,"getS3Client","Error in creating S3 client");
                throw new RuntimeException(e);
            }
        }
        return s3AsyncClient;
    }

    public S3Presigner getPreSignedClient(){
        if(s3Presigner==null){
            LoggingClass.logMessage(S3builder.class,"getS3Client","Creating new S3 presigned client");

            try {
                AwsCredentialsProvider credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(this.accessKeyId, this.secretAccessKey));
                s3Presigner = S3Presigner.builder().credentialsProvider(credentials).region(this.region).build();
                return s3Presigner;
            } catch (Exception e) {
                LoggingClass.logError(S3builder.class,"getPreSignedClient","Error in creating new S3 presigned client");
                throw new RuntimeException(e);
            }
        }
        return s3Presigner;
    }


}
