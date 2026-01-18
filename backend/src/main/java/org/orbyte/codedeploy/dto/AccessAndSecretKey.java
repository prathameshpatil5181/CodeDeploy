package org.orbyte.codedeploy.dto;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.regions.Region;

@Getter
@Setter
public class AccessAndSecretKey {
    private String accessKey;
    private String secretKey;
    private Region region;
}
