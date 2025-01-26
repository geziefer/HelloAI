package de.vsti.aiduke.demo.service;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/hello-world")
public class HelloWorldService {
    @Inject
    HelloWorldAssistant assistant;

    @GET
    @Produces("text/plain")
    public String hello(@QueryParam("name") String name) {
        if (name != null) {
            return assistant.chat("Hello from " + name);
        } else {
            return assistant.chat("Hello there");
        }
    }
}
