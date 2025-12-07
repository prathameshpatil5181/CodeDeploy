package org.orbyte.codedeploy.Deployment.Cloud.aws.Response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetInstanceDetailResponse {
    private String instanceId;
    private String publicIpAddress;
    private String privateIpAddress;
}
