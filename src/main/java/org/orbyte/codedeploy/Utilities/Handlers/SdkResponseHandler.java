package org.orbyte.codedeploy.Utilities.Handlers;

import software.amazon.awssdk.http.SdkHttpResponse;

public class SdkResponseHandler {

    public static String toStringResponse(SdkHttpResponse sdkHttpResponse) {
        return "Status Code: " + sdkHttpResponse.statusCode() +
                " Headers: " + sdkHttpResponse.headers().toString()  +
                " isSuccessful: " + sdkHttpResponse.isSuccessful() + " StatusMessage: " + sdkHttpResponse.statusText().orElse("N/A");
    }

}
