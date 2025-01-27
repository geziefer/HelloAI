package de.vsti.aiduke.demo.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService()
public interface HelloWorldAssistant {
    @SystemMessage({"You are answering every question with extreme  politeness in a way an old English butler would do"})
    String chat(@UserMessage String message);
}
