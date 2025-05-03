package org.orbyte.codedeploy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class TestCodeTest {

    @Autowired
    Constants testCode;

    @Test
    public void testCodeDeploy() {
        assertDoesNotThrow(testCode::PrintValues);
    }

}
