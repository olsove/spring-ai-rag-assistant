package no.olsove.learn.ai.rag.spring_ai_rag_assistant.retrieval;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Retrieval Logic Tests")
class RetrievalServiceTest {

    @Test
    @DisplayName("Should retrieve documents within similarity threshold")
    void shouldRetrieveDocumentsWithinThreshold() {
        // Given: A mock vector store and a service with configured thresholds
        VectorStore vectorStore = mock(VectorStore.class);
        RetrievalService retrievalService = new RetrievalService(vectorStore);
        
        // Manual injection of @Value fields for unit test
        ReflectionTestUtils.setField(retrievalService, "topK", 2);
        ReflectionTestUtils.setField(retrievalService, "similarityThreshold", 0.7);

        List<Document> expectedDocs = List.of(
            new Document("Spring AI is great", Map.of("file_name", "test.txt")),
            new Document("RAG systems are powerful", Map.of("file_name", "test.txt"))
        );

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(expectedDocs);

        // When: We search for a query
        List<Document> results = retrievalService.retrieve("How does Spring AI work?");

        // Then: We get the expected documents
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getContent()).contains("Spring AI");
    }

    @Test
    @DisplayName("Should rank limit-focused sections above timeout-only matches")
    void shouldRankLimitFocusedSectionsAboveTimeoutOnlyMatches() {
        VectorStore vectorStore = mock(VectorStore.class);
        RetrievalService retrievalService = new RetrievalService(vectorStore);

        ReflectionTestUtils.setField(retrievalService, "topK", 2);
        ReflectionTestUtils.setField(retrievalService, "similarityThreshold", 0.2);

        List<Document> primaryDocs = List.of(
                new Document("Change History: backend read timeout reduced from 8s to 5s"),
                new Document("Known Limits: Maximum request body size is 2MB")
        );
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(primaryDocs);

        List<Document> results = retrievalService.retrieve("What are the limits?");

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getContent()).contains("Known Limits");
    }

    @Test
    @DisplayName("Should fallback to relaxed retrieval when primary results miss query terms")
    void shouldFallbackToRelaxedRetrievalWhenPrimaryResultsMissQueryTerms() {
        VectorStore vectorStore = mock(VectorStore.class);
        RetrievalService retrievalService = new RetrievalService(vectorStore);

        ReflectionTestUtils.setField(retrievalService, "topK", 2);
        ReflectionTestUtils.setField(retrievalService, "similarityThreshold", 0.2);

        List<Document> primaryDocs = List.of(
                new Document("Service Overview: gateway supports authentication and tracing")
        );
        List<Document> fallbackDocs = List.of(
                new Document("Rate Limiting: Default limit is 120 requests/second"),
                new Document("Known Limits: Maximum header size is 16KB")
        );

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(primaryDocs)
                .thenReturn(fallbackDocs);

        List<Document> results = retrievalService.retrieve("What are the limits?");

        assertThat(results).hasSize(2);
        assertThat(results.stream().map(Document::getContent))
                .anyMatch(content -> content.contains("Rate Limiting"));
        verify(vectorStore, times(2)).similaritySearch(any(SearchRequest.class));
    }
}
