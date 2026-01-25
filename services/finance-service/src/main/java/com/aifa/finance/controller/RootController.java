package com.aifa.finance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, String> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "AI Finance Advisor - Finance Service");
        response.put("status", "OK");
        response.put("version", "1.0.0");
        return response;
    }
}
