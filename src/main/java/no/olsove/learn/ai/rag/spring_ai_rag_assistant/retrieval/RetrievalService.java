package no.olsove.learn.ai.rag.spring_ai_rag_assistant.retrieval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final VectorStore vectorStore;

    @Value("${rag.retrieval.top-k:3}")
    private int topK;

    @Value("${rag.retrieval.similarity-threshold:0.5}")
    private double similarityThreshold;

    public List<Document> retrieve(String query) {
        log.info("Retrieving documents for query: {}", query);
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(similarityThreshold)
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        log.info("Retrieved {} documents", documents.size());
        return documents;
    }
}
