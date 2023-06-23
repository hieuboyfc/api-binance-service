package com.hieuboy.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BinanceApi {

    @GetMapping(value = "/api/v1/ping")
    public void checkPing() {

    }

}
