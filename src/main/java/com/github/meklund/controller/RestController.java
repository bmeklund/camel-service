package com.github.meklund.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping("/ping")
    public String ping() {
        return "Pong!! " + new Date();
    }

}
