package no.olsove.learn.ai.rag.spring_ai_rag_assistant.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.model.AnswerResponse;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.retrieval.RetrievalService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatOrchestrator {

    private final RetrievalService retrievalService;
    private final ChatClient chatClient;

    public ChatOrchestrator(RetrievalService retrievalService, ChatClient.Builder chatClientBuilder) {
        this.retrievalService = retrievalService;
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are a helpful assistant providing answers based on the provided technical documentation.
                        If you don't know the answer based on the context, say that you don't know.
                        Always use the provided context to ground your answers.
                        """)
                .build();
    }

    public AnswerResponse chat(String query) {
        log.info("Processing chat query: {}", query);

        List<Document> contextDocs = retrievalService.retrieve(query);

        String context = contextDocs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        String answer = chatClient.prompt()
                .user(u -> u.text("""
                                Query: {query}
                                
                                Context:
                                {context}
                                """)
                        .param("query", query)
                        .param("context", context))
                .call()
                .content();

        List<String> citations = contextDocs.stream()
                .map(doc -> (String) doc.getMetadata().getOrDefault("file_name", "Unknown Source"))
                .distinct()
                .toList();

        return new AnswerResponse(answer, citations);
    }
}
