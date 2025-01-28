package de.vsti.aiduke.solution.part2;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Solution for workshop tasks part II:
 *
 * @author alexander.ruehl
 */
public class Part2 {
    // API key gets picked up from environment and must be set prior to run the solution
    static String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    // have a model, a memory and an assistant with and without RAG
    ChatLanguageModel modelRAG;
    ChatLanguageModel modelNoRAG;
    ChatMemory memoryRAG;
    ChatMemory memoryNoRAG;
    RAGAssistant assistantRAG;
    NoRAGAssistant assistantNoRAG;

    public static void main(String[] args) {
        Part2 part2 = new Part2();
        part2.execute();
    }

    Part2() {
        // models and memories are identical for both
        modelRAG = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .build();
        memoryRAG = new MessageWindowChatMemory.Builder().maxMessages(10).build();
        modelNoRAG = OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("gpt-4o-mini")
                .build();
        memoryNoRAG = new MessageWindowChatMemory.Builder().maxMessages(10).build();

        // model without RAG is just default
        assistantNoRAG = AiServices.builder(NoRAGAssistant.class)
                .chatLanguageModel(modelNoRAG)
                .chatMemory(memoryNoRAG)
                .build();

        // model with RAG is enhanced by document embeddings which is read from a text file in resources folder
        String filename = "doc/ApisPerplexa.txt";
        Path path;
        try {
            path = Paths.get(Objects.requireNonNull(Part2.class.getClassLoader().getResource(filename)).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        DocumentParser parser = new TextDocumentParser();
        Document document = FileSystemDocumentLoader.loadDocument(path, parser);
        // Note: the manual document embedding steps can be hidden behind EmbeddingStoreIngestor
        // use 300 characters as document chunk size
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);
        // choose a local in-process embedding model (additional dependency)
        EmbeddingModel embeddingModel =  new BgeSmallEnV15QuantizedEmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        // choose an im-memory embedding store
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);
        // create content retriever with the created embeddings
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.5)
                .build();
        assistantRAG = AiServices.builder(RAGAssistant.class)
                .chatLanguageModel(modelRAG)
                .chatMemory(memoryRAG)
                .contentRetriever(contentRetriever)
                .build();
    }

    public void execute() {
        boolean ragMode = false;
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Hello, I am your assistant. Please enter your question. Type 'exit' to quit or 'rag'/'norag' to switch modes (currently norag).\n");
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                System.out.println();

                switch (input) {
                    case "exit" -> {
                        System.out.println("Good bye!");
                        System.exit(0);
                    }
                    case "rag" -> {
                        ragMode = true;
                        System.out.println("RAG on\n");
                    }
                    case "norag" -> {
                        ragMode = false;
                        System.out.println("RAG off\n");
                    }
                    default -> {
                        String answer;
                        if (ragMode) {
                            answer = assistantRAG.chat(input);
                        } else {
                            answer = assistantNoRAG.chat(input);
                        }
                        System.out.println(answer + "\n");
                    }
                }
            }
        }
    }
}
