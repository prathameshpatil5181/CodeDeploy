package org.orbyte.codedeploy.services;

import org.orbyte.codedeploy.Utilities.Exception.S3UploadException;
import org.orbyte.codedeploy.Utilities.GetFilePath;
import org.orbyte.codedeploy.Utilities.Handlers.CommandReturnResponse;
import com.github.dockerjava.api.command.CreateContainerResponse;
import org.orbyte.codedeploy.Constants;
import org.orbyte.codedeploy.Deployment.Cloud.aws.src.S3Handler;
import org.orbyte.codedeploy.Docker.Handlers.DockerHandler;
import org.orbyte.codedeploy.Log.LoggingClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DockerBuilderService {

    @Autowired
    Constants constants;

    @Autowired
    DockerHandler dockerHandler;

    public String buildProjectWithDocker(String gitRepoUrl, String userId,String projectId) {

        CreateContainerResponse container = null;

        try {
             container = dockerHandler.pullContainer("node:25.0.0");
            CommandReturnResponse result = dockerHandler.RunCommandsInsideContainer(container,"git clone "+gitRepoUrl);
            String folder = GetFilePath.getFolderPath(gitRepoUrl);
            CommandReturnResponse res = dockerHandler.RunCommandsInsideContainer(container,"cd "+ folder +" && npm install && npm run build && tar -czvf dist.tar.gz ./dist  && cp -r ./dist.tar.gz /home/node && echo done");
            CommandReturnResponse res3 = dockerHandler.RunCommandsInsideContainer(container,"chmod -R 777 /home/node/dist.tar.gz");
            dockerHandler.clearContainer(container);

            // upload to s3

            String s3UplaodResponse = uplaodToS3Bucket(projectId,"/home/ubuntu/builds/dist.tar.gz",userId);

            if (s3UplaodResponse != null){
                return s3UplaodResponse;
            }
            else {
                throw new S3UploadException("failed to uplaod file to s3 bucket");
            }

        } catch (InterruptedException e) {
            LoggingClass.logError(DockerBuilderService.class, " buildProjectWithDocker", e.getMessage());
            return null;
        }

        // upload the file in s3 bucket




    }

    public String uplaodToS3Bucket(String projectId, String filePath, String UserId){
        // upload the file in s3 bucket

        // fetch the access key and password from Db
//        Constants constants = dummyS3Creds();
        String bucketName = "codedeploy5181"; //fetch from Db
        String folderPath = UserId + "/"+projectId;

        System.out.println(constants.getAccessKey());


        S3Handler s3handler = new S3Handler.Builder()
                .accessKeyId(constants.getAccessKey())
                .secretAccessKey(constants.getSecretKey()).region(constants.getRegion()).build();


        s3handler.createFolder(bucketName,folderPath);

        boolean result= s3handler.uploadFiles(bucketName,folderPath, filePath);

        if (result){
            String presignedUrl = s3handler.generatePresignedUrl(bucketName,folderPath+ "/dist.tar.gz",10);

            LoggingClass.logMessage(DockerBuilderService.class,"uplaodToS3Bucket","Presigned URL: " + presignedUrl);

            return presignedUrl;
        }
        else {
            return null;
        }

    }

}
