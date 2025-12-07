package org.orbyte.codedeploy.Utilities;

import java.util.UUID;

public class GenerateUnique {

    public static String generateUniqueProjectId(String UserName){
        UUID uuid = UUID.randomUUID();

       return  uuid.toString() + "@project_ID";

    }

}
