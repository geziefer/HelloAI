package de.vsti.aiduke.solution;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class Part1Starter {
    static String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    ChatLanguageModel modelDoctor;
    ChatLanguageModel modelPatient;
    ChatMemory memoryDoctor;
    ChatMemory memoryPatient;
    ConversationalChain chainDoctor;
    ConversationalChain chainPatient;

    public static void main(String[] args) {
        Part1Starter part1 = new Part1Starter();
        part1.chat();
    }

    Part1Starter() {
        // init models, 1 is the doctor, 2 is the patient
        modelDoctor = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .temperature(0.5)
                .build();
        memoryDoctor = new MessageWindowChatMemory.Builder().maxMessages(20).build();
        memoryDoctor.add(new SystemMessage(
                """
                        You are a doctor who answers every question of your patients with extreme politeness in a way an old English butler would do. 
                        You always try to be very serious no matter what the patient might say.
                        Limit your answers to 1 or 2 sentences. 
                    """));
        chainDoctor = ConversationalChain.builder()
                .chatLanguageModel(modelDoctor)
                .chatMemory(memoryDoctor)
                .build();

        modelPatient = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .temperature(1.5)
                .build();
        memoryPatient = new MessageWindowChatMemory.Builder().maxMessages(20).build();
        memoryPatient.add(new SystemMessage(
                """
                        You are a patient visiting a doctor.
                        Whenever he asks or tells you something, you answer in a funny or even silly way.
                        Limit your answers to 1 or 2 sentences.
                    """));
        chainPatient = ConversationalChain.builder()
                .chatLanguageModel(modelPatient)
                .chatMemory(memoryPatient)
                .build();
    }

    public void chat() {
        // start with a sentence from the patient and then lets both have a chat
        String answer = "Good afternoon, Sir, I do hope, you had a pleasant day so far. How may I be of service?\n";
        System.out.println("Doctor: " + answer);
        for (int i = 0; i < 5; i++) {
            answer = chainPatient.execute(answer);
            System.out.println("Patient: " + answer);
            System.out.println();

            answer = chainDoctor.execute(answer);
            System.out.println("Doctor: " + answer);
            System.out.println();
        }
    }
}
