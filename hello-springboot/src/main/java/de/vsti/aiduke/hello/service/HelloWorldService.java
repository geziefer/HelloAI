package de.vsti.aiduke.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldService {

    @Autowired
    HelloWorldAssistant assistant;

    @GetMapping("/hello-world")
    public String index(@RequestParam(value = "name", required = false) String name) {
        if (name != null) {
            return assistant.chat("Hello from " + name);
        } else {
            return "Hello there";
        }
    }

}