package kg.edgepos.posservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PosServiceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PosServiceServiceApplication.class, args);
    }
}
