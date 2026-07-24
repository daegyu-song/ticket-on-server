package com.dg.ticketonserver;

import org.springframework.boot.SpringApplication;

public class TestTicketOnServerApplication {

    public static void main(String[] args) {
        SpringApplication.from(TicketOnServerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
