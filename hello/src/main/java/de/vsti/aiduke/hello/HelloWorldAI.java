package de.vsti.aiduke.hello;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class HelloWorldAI {
    static String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .build();
        String answer = model.generate("Say something nice");
        System.out.println(answer);
    }
}
