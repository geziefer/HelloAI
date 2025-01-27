package de.vsti.aiduke.solution.part1;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * Solution for workshop tasks part I:
 * A DoctorAI and a PatientAI should have a little chat together.
 * We want the doctor to be very serious and polite.
 * We want the patient be funny and silly.
 * We start the conversation manually and then let it go automatically for some iterations.
 * The answers should be limited to a sentence or two.
 * @author alexander.ruehl
 */
public class Part1 {
    // API key gets picked up from environment and must be set prior to run the solution
    static String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    // have a model, a memory and an assistant for both doctor and patient
    ChatLanguageModel modelDoctor;
    ChatLanguageModel modelPatient;
    ChatMemory memoryDoctor;
    ChatMemory memoryPatient;
    DoctorAssistant assistantDoctor;
    PatientAssistant assistantPatient;

    public static void main(String[] args) {
        Part1 part1 = new Part1();
        part1.execute();
    }

    Part1() {
        // doctor model has low temperature and its system prompt describes his serious personality and limits answers
        modelDoctor = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .temperature(0.5)
                .build();
        // doctor memory keeps messages of chat
        memoryDoctor = new MessageWindowChatMemory.Builder().maxMessages(20).build();
        // assistant interface is used and contains doctor behaviour in system message
        assistantDoctor = AiServices.builder(DoctorAssistant.class)
                .chatLanguageModel(modelDoctor)
                .chatMemory(memoryDoctor)
                .build();

        // patient model has high temperature and its system prompt describes his silly personality and limits answers
        modelPatient = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .temperature(1.5)
                .build();
        // patient memory keeps messages of chat
        memoryPatient = new MessageWindowChatMemory.Builder().maxMessages(20).build();
        // assistant interface is used and contains patient behaviour in system message
        assistantPatient = AiServices.builder(PatientAssistant.class)
                .chatLanguageModel(modelPatient)
                .chatMemory(memoryPatient)
                .build();
    }

    public void execute() {
        // start with a sentence from the patient and then let both have a chat with the help of the conversational chains
        String answer = "Good afternoon, Sir, I do hope, you had a pleasant day so far. How may I be of service?";
        System.out.printf("Doctor: %s\n\n", answer);
        for (int i = 0; i < 5; i++) {
            answer = assistantPatient.chat(answer);
            System.out.printf("Patient: %s\n\n", answer);

            answer = assistantDoctor.chat(answer);
            System.out.printf("Doctor: %s\n\n", answer);
        }
    }
}
