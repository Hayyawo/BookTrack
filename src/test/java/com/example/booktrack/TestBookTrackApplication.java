package com.example.booktrack;

import org.springframework.boot.SpringApplication;

public class TestBookTrackApplication {

    public static void main(String[] args) {
        SpringApplication.from(BookTrackApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
