package no.olsove.learn.ai.rag.spring_ai_rag_assistant.ingest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("Ingestion Logic Tests")
class IngestionServiceTest {

    @Test
    @DisplayName("Should load, split, and add documents to vector store")
    void shouldIngestResourceCorrectly() {
        // Given: A mock vector store and a document resource
        VectorStore vectorStore = mock(VectorStore.class);
        IngestionService ingestionService = new IngestionService(vectorStore);
        
        // Manual injection of @Value fields for unit test
        org.springframework.test.util.ReflectionTestUtils.setField(ingestionService, "chunkSize", 100);
        org.springframework.test.util.ReflectionTestUtils.setField(ingestionService, "chunkOverlap", 10);
        
        String content = "This is a test document content that should be split into chunks.";
        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "test-doc.txt";
            }
        };

        // When: We ingest the resource
        ingestionService.ingest(resource);

        // Then: The vector store should receive a list of split documents
        verify(vectorStore).add(anyList());
    }
}
