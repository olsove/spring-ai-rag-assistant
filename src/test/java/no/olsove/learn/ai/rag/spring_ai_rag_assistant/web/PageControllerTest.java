package no.olsove.learn.ai.rag.spring_ai_rag_assistant.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PageController.class)
@DisplayName("Page Route Tests")
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should render home page")
    void shouldRenderHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Spring AI RAG Assistant")));
    }

    @Test
    @DisplayName("Should render ingest page")
    void shouldRenderIngestPage() throws Exception {
        mockMvc.perform(get("/ingest"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ingest Document")));
    }

    @Test
    @DisplayName("Should render chat page")
    void shouldRenderChatPage() throws Exception {
        mockMvc.perform(get("/chat"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ask the RAG Assistant")));
    }
}
