package org.imperial.fastquantanalysis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.imperial.fastquantanalysis.mapper")
public class FastQuantAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastQuantAnalysisApplication.class, args);
    }

}
