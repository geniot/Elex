package io.github.geniot.elex;

import io.github.geniot.elex.ftindexer.FtServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElexApplication {
    public static void main(String[] args) {
        new Thread(() -> FtServer.getInstance()).start();
        SpringApplication.run(ElexApplication.class, args);
    }
}
