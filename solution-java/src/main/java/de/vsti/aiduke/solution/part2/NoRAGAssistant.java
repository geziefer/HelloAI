package de.vsti.aiduke.solution.part2;

import dev.langchain4j.service.SystemMessage;

public interface NoRAGAssistant {
    @SystemMessage("""
                        You are an expert on animals and answer questions about them.
                        Limit your answers to 1 or 2 sentences.
                    """)
    String chat(String message);
}
