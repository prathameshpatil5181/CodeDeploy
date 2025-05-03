package org.orbyte.codedeploy;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

@Component
public class Constants {

    public static  String AMI_ID = "ami-0e35ddab05955cf57";


    @Value("${app.aws.secretKey}")
    private  String SECRET_KEY ;

    @Getter
    private Region region = Region.AP_SOUTH_1 ;


    @Value("${app.aws.accessKey}")
    private String ACCESS_KEY;

    public Constants() {
        System.out.println("Constants constructor called");
    }

    public void PrintValues(){
        System.out.println("Secret Key: " + this.SECRET_KEY);
        System.out.println("Access Key: " + this.ACCESS_KEY);
        System.out.println("Region: " + this.region);
    }

    public  String getSecretKey() {
        return this.SECRET_KEY;
    }


    public String getAccessKey() {
        System.out.println(this.ACCESS_KEY);
        return this.ACCESS_KEY;
    }

}
