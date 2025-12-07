package org.orbyte.codedeploy.Deployment.Cloud.aws.src;

import jakarta.ws.rs.GET;
import org.orbyte.codedeploy.Deployment.Cloud.aws.interfaces.S3builder;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class S3Handler extends S3builder {

    private S3Handler(String accessKeyId, String secretAccessKey,Region region) {
        super(secretAccessKey,accessKeyId,region);
    }

        public static class Builder{
            private String accessKeyId;
            private String secretAccessKey;
            private Region region;

            public Builder accessKeyId(String accessKeyId) {
                this.accessKeyId = accessKeyId;
                return this;
            }

            public Builder secretAccessKey(String secretAccessKey) {
                this.secretAccessKey = secretAccessKey;
                return this;
            }

            public Builder region(Region region) {
                this.region = region;
                return this;
            }

            public S3Handler build() {
                if (accessKeyId == null || secretAccessKey == null || region == null) {
                    throw new IllegalStateException("accessKeyId, secretAccessKey and region must be provided");
                }
                return new S3Handler(accessKeyId, secretAccessKey, region);
            }
        }

        public List<Bucket> getBucketList() throws RuntimeException {

            ListBucketsResponse listBucketsResponse = null;
            String continutaionToken = null;
            try {
                listBucketsResponse = getS3Client().listBuckets(request->request.continuationToken(continutaionToken));
            } catch (AwsServiceException | SdkClientException e) {
                throw new RuntimeException(e);
            }
                List<Bucket> buckets = listBucketsResponse.buckets();
                return buckets;
        }




        public Boolean createFolder(String bucketName,String folderName){

            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(folderName+"/").build();
            RequestBody requestBody = RequestBody.empty();
            PutObjectResponse putObjectResponse = getS3Client().putObject(putObjectRequest,requestBody);
            return putObjectResponse.sdkHttpResponse().isSuccessful();
        }

        public Boolean uploadFiles(String bucketName,String folderName,String path){

            File artifact = new File(path);
            String key = folderName + "/" + artifact.getName();
            System.out.println("creating bucket with key: " + key);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();
            RequestBody requestBody = RequestBody.empty();
            System.out.println("making request to upload file");
            PutObjectResponse putObjectResponse = getS3Client().putObject(putObjectRequest, Paths.get(artifact.getAbsolutePath()));
            return putObjectResponse.sdkHttpResponse().isSuccessful();


        }

        public String generatePresignedUrl(String bucketName, String objectKey, int expirationInMinutes) {


            try {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(Math.max(1, expirationInMinutes)))
                        .getObjectRequest(getObjectRequest)
                        .build();

                PresignedGetObjectRequest presignedRequest = getPreSignedClient().presignGetObject(presignRequest);
                return presignedRequest.url().toString();
            } catch (AwsServiceException | SdkClientException e) {
                throw new RuntimeException(e);
            }
        }


}
