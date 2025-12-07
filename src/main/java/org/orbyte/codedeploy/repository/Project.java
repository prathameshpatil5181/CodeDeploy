package org.orbyte.codedeploy.repository;

import org.orbyte.codedeploy.Constants;
import org.orbyte.codedeploy.dto.AccessAndSecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;




@Component
public class Project {

 AccessAndSecretKey key = null;

// @Autowired
// private

    public Project(Constants consts) {
        AccessAndSecretKey accessAndSecretKey = new AccessAndSecretKey();
        accessAndSecretKey.setAccessKey(consts.getAccessKey());
        accessAndSecretKey.setSecretKey(consts.getSecretKey());
        accessAndSecretKey.setRegion(consts.getRegion());

        this.key = accessAndSecretKey;

    }

    public AccessAndSecretKey getAccessKeyAndSecretKey(String projectId) throws Exception {
        // Placeholder implementation
        return this.key;
    }
}
