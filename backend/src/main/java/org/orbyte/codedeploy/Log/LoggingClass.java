package org.orbyte.codedeploy.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingClass {
    private static final Logger logger = LoggerFactory.getLogger(LoggingClass.class);


    public void logReqRes(Class c, String Function, String api,String data){
        logger.info("class: {} | function:{} | api:{} | data:{}", c.toString(), Function, api, data);
    }

    public static void logMessage(Class c, String Function,String Message){
        logger.info("class: {} | function:{} | {}",c.toString(),Function,Message);
    }

    public static void logDebug(Class c, String Function, String Message){
        logger.debug("class: {} | function:{} | {}",c.toString(),Function,Message);
    }

    public static void logError(Class c, String Function, String Message){
        logger.error("class: {} | function:{} | {}",c.toString(),Function,Message);
    }

    public void logTrace(Class c, String Function,String Message){
        logger.trace("class: {} | function:{} | {}",c.toString(),Function,Message);
    }


}