package org.imperial.fastquantanalysis;

import jakarta.annotation.Resource;
import org.imperial.fastquantanalysis.util.RandomStringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FastQuantAnalysisApplicationTests {

    @Resource
    private RandomStringGenerator randomStringGenerator;

    @Test
    void contextLoads() {
    }

    @Test
    void testRandomStringGenerator() {
        String s = randomStringGenerator.generateRandomString();
        System.out.println(s);
    }

}
