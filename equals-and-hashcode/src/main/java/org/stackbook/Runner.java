package org.stackbook;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("good");
    }
}
