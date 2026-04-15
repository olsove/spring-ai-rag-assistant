package no.olsove.learn.ai.rag.spring_ai_rag_assistant.chat;

import no.olsove.learn.ai.rag.spring_ai_rag_assistant.model.AnswerResponse;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.retrieval.RetrievalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Chat Orchestration Tests")
class ChatOrchestratorTest {

    private RetrievalService retrievalService;
    private ChatClient chatClient;
    private ChatClient.Builder chatClientBuilder;
    private ChatOrchestrator chatOrchestrator;

    @BeforeEach
    void setUp() {
        retrievalService = mock(RetrievalService.class);
        chatClient = mock(ChatClient.class);
        chatClientBuilder = mock(ChatClient.Builder.class);

        when(chatClientBuilder.defaultSystem(anyString())).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);

        chatOrchestrator = new ChatOrchestrator(retrievalService, chatClientBuilder);
    }

    @Test
    @DisplayName("Should extract and deduplicate citations from context documents")
    void shouldExtractAndDeduplicateCitations() {
        // Given: Retrieval service returns documents with file_name metadata
        List<Document> documents = List.of(
            new Document("content 1", Map.of("file_name", "doc1.pdf")),
            new Document("content 2", Map.of("file_name", "doc1.pdf")),
            new Document("content 3", Map.of("file_name", "doc2.pdf"))
        );
        when(retrievalService.retrieve(anyString())).thenReturn(documents);

        // Mock ChatClient fluent API
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(any(java.util.function.Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("AI response");

        // When: Chat is called
        AnswerResponse response = chatOrchestrator.chat("What is documentation?");

        // Then: Citations should be doc1.pdf and doc2.pdf, deduplicated
        assertThat(response.getCitations()).containsExactlyInAnyOrder("doc1.pdf", "doc2.pdf");
        assertThat(response.getAnswer()).isEqualTo("AI response");
    }

    @Test
    @DisplayName("Should return 'Unknown Source' if file_name is missing")
    void shouldHandleMissingSourceMetadata() {
        // Given: Document without file_name metadata
        List<Document> documents = List.of(new Document("content 1", Map.of()));
        when(retrievalService.retrieve(anyString())).thenReturn(documents);

        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(any(java.util.function.Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("AI response");

        // When: Chat is called
        AnswerResponse response = chatOrchestrator.chat("What is documentation?");

        // Then: Citation should be 'Unknown Source'
        assertThat(response.getCitations()).containsExactly("Unknown Source");
    }

    @Test
    @DisplayName("Should configure system prompt with ambiguity handling instructions")
    void shouldConfigureSystemPromptWithAmbiguityHandlingInstructions() {
        verify(chatClientBuilder).defaultSystem(org.mockito.ArgumentMatchers.<String>argThat(prompt ->
                prompt != null
                        && prompt.contains("broad or ambiguous")
                        && prompt.contains("group the answer by relevant section")
        ));
    }
}
