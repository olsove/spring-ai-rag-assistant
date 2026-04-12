package no.olsove.learn.ai.rag.spring_ai_rag_assistant.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.chat.ChatOrchestrator;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.ingest.IngestionService;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.model.AnswerResponse;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.model.QueryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@DisplayName("API Endpoint Tests")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatOrchestrator chatOrchestrator;

    @MockitoBean
    private IngestionService ingestionService;

    @Test
    @DisplayName("Should process chat query and return answer with citations")
    void shouldChatSuccessfully() throws Exception {
        // Given: A query and a mock response
        QueryRequest request = new QueryRequest("Hello AI?");
        AnswerResponse expectedResponse = new AnswerResponse("Hello!", List.of("source1.txt"));
        when(chatOrchestrator.chat(anyString())).thenReturn(expectedResponse);

        // When/Then: We POST to /api/chat
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Hello!"))
                .andExpect(jsonPath("$.citations[0]").value("source1.txt"));
    }

    @Test
    @DisplayName("Should ingest file successfully via multipart request")
    void shouldIngestFileSuccessfully() throws Exception {
        // Given: A mock file
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "document content".getBytes()
        );

        // When/Then: We POST to /api/chat/ingest
        mockMvc.perform(multipart("/api/chat/ingest").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File ingested successfully: test.txt"));

        verify(ingestionService).ingest(any());
    }
}
