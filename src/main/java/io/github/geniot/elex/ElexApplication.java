package io.github.geniot.elex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ElexApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElexApplication.class, args);
    }
}
