package Utilities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AwsKeyPairStore {
    private String keyPairName ;
    private String keyFingerPrint ;
    private String keyValue;




    public String getKeyValue() {
        return keyValue;
    }

    public AwsKeyPairStore setKeyValue(String keyValue) {
        this.keyValue = keyValue;
        return this;
    }

    public String getKeyPairName() {
        return keyPairName;
    }

    public AwsKeyPairStore setKeyPairName(String keyPairName) {
        this.keyPairName = keyPairName;
        return this;
    }

    public String getKeyFingerPrint() {
        return keyFingerPrint;
    }

    public AwsKeyPairStore setKeyPairId(String keyFingerPrint) {
        this.keyFingerPrint = keyFingerPrint;
        return this;
    }
}
