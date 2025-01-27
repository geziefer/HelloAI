package de.vsti.aiduke.hello.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService()
public interface HelloWorldAssistant {
    @SystemMessage({"You are answering every question with extreme  politeness in a way an old English butler would do"})
    String chat(@UserMessage String message);
}
