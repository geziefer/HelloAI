package de.vsti.aiduke.solution;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * Solution for workshop tasks part I:
 * A DoctorAI and a PatientAI should have a little chat together.
 * We want the doctor to be very serious and polite.
 * We want the patient be funny and silly.
 * We start the conversation manually and then let it go automatically for some iterations.
 * The answers should be limited to a sentence or two.
 * The provided answers of the AIs should be streamed to output in order to see them while being created.
 * @author alexander.ruehl
 */
public class Part1 {
    // API key gets picked up from environment and must be set prior to run the solution
    static String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    // have a model, memory and conversational chain for both doctor and patient
    ChatLanguageModel modelDoctor;
    ChatLanguageModel modelPatient;
    ChatMemory memoryDoctor;
    ChatMemory memoryPatient;
    ConversationalChain chainDoctor;
    ConversationalChain chainPatient;

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

        // patient model has high temperature and its system prompt describes his silly personality and limits answers
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

    public void execute() {
        // start with a sentence from the patient and then let both have a chat with the help of the conversational chains
        String answer = "Good afternoon, Sir, I do hope, you had a pleasant day so far. How may I be of service?";
        System.out.printf("Doctor: %s\n\n", answer);
        for (int i = 0; i < 5; i++) {
            answer = chainPatient.execute(answer);
            System.out.printf("Patient: %s\n\n", answer);

            answer = chainDoctor.execute(answer);
            System.out.printf("Doctor: %s\n\n", answer);
        }
    }
}
