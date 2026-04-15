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
import java.util.stream.IntStream;

@Slf4j
@Service
public class ChatOrchestrator {

    private final RetrievalService retrievalService;
    private final ChatClient chatClient;

    public ChatOrchestrator(RetrievalService retrievalService, ChatClient.Builder chatClientBuilder) {
        this.retrievalService = retrievalService;
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                You are a technical documentation assistant.
                Answer only from the provided context.
                If context is missing, reply: "I don't know based on the provided context."
                Never invent or merge values across sections.
                Never change numbers, units, durations, status codes, or names.
                If the user asks to list steps, include all steps in order with original numbering.
                If a query is broad or ambiguous (for example "limits" or "timeouts"), group the answer by relevant section and include all clearly relevant section matches from context.
                Prefer section headings that most directly match the user query terms.
                For queries containing "limit" or "limits", prioritise "Known Limits" and "Rate Limiting" sections before timeout or change-history entries.
                Keep answers concise and factual.
                """)
                .build();
    }

    public AnswerResponse chat(String query) {
        log.info("Processing chat query: {}", query);

        List<Document> contextDocs = retrievalService.retrieve(query);

        String context = IntStream.range(0, contextDocs.size())
                .mapToObj(i -> formatContextChunk(contextDocs.get(i), i + 1))
                .collect(Collectors.joining("\n\n"));

        String answer = chatClient.prompt()
                .user(u -> u.text("""
                                Query: {query}
                                
                                Context:
                                {context}
                                
                                Instructions:
                                - Only use context.
                                - If query terms can match multiple sections, answer with grouped sections rather than a single fragment.
                                - If asked for steps, return every step in order.
                                - Preserve exact numeric values and units.
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

    private String formatContextChunk(Document document, int chunkNumber) {
        String source = (String) document.getMetadata().getOrDefault("file_name", "Unknown Source");
        return """
                [Source: %s | Chunk: %d]
                %s
                """.formatted(source, chunkNumber, document.getContent());
    }
}
