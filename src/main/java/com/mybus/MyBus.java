package com.mybus;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This configuration class has three responsibilities:
 * <ol>
 *     <li>It enables the auto configuration of the Spring application context.</li>
 *     <li>
 *         It ensures that Spring looks for other components (controllers, services, and repositories) from the
 *         <code>com.javaadvent.bootrest.dao</code> package.
 *     </li>
 *     <li>It launches our application in the main() method.</li>
 * </ol>
 * @author Petri Kainulainen
 */

@SpringBootApplication
public class MyBus implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MyBus.class, args);
    }
    @Override
    public void run(String... strings) throws Exception {
        String data = "Spring Kafka Producer and Consumer Example is new";
    }
}
