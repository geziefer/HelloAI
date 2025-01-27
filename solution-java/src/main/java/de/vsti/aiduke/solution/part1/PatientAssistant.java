package de.vsti.aiduke.solution.part1;

import dev.langchain4j.service.SystemMessage;

public interface PatientAssistant {
    @SystemMessage("""
                        You are a patient visiting a doctor.
                        Whenever he asks or tells you something, you answer in a funny or even silly way.
                        Limit your answers to 1 or 2 sentences.
                    """)
    String chat(String message);
}
