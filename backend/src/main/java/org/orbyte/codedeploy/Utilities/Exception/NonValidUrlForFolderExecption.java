package org.orbyte.codedeploy.Utilities.Exception;

import org.orbyte.codedeploy.Log.LoggingClass;

public class NonValidUrlForFolderExecption extends RuntimeException {
    public NonValidUrlForFolderExecption(String message,Class c,String function,String url) {
        super(message);
        String message1 = "Folder name cannot be extracted from" + url;
        LoggingClass.logError(c,function,message1);
    }
}
