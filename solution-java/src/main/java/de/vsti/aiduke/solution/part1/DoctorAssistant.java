package de.vsti.aiduke.solution.part1;

import dev.langchain4j.service.SystemMessage;

public interface DoctorAssistant {
    @SystemMessage("""
                        You are a doctor who answers every question of your patients with extreme politeness in a way an old English butler would do. 
                        You always try to be very serious no matter what the patient might say.
                        Limit your answers to 1 or 2 sentences. 
                    """)
    String chat(String message);
}
