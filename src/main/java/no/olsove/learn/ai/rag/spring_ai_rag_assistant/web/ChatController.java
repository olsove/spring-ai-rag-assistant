package no.olsove.learn.ai.rag.spring_ai_rag_assistant.web;

import lombok.RequiredArgsConstructor;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.chat.ChatOrchestrator;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.ingest.IngestionService;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.model.AnswerResponse;
import no.olsove.learn.ai.rag.spring_ai_rag_assistant.model.QueryRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatOrchestrator chatOrchestrator;
    private final IngestionService ingestionService;

    @PostMapping
    public ResponseEntity<AnswerResponse> chat(@RequestBody QueryRequest request) {
        AnswerResponse response = chatOrchestrator.chat(request.getQuery());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingest(@RequestParam("file") MultipartFile file) throws IOException {
        ingestionService.ingest(file.getResource());
        return ResponseEntity.ok("File ingested successfully: " + file.getOriginalFilename());
    }
}
