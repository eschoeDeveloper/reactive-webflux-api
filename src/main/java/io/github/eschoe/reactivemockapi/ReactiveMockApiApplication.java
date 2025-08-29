package io.github.eschoe.reactivemockapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class ReactiveMockApiApplication {

    public static void main(String[] args) {
        BlockHound.install(); // blocking 코드 식별
        SpringApplication.run(ReactiveMockApiApplication.class, args);
    }

}
