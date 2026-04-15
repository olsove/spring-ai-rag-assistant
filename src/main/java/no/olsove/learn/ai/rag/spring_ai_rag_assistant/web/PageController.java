package no.olsove.learn.ai.rag.spring_ai_rag_assistant.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/ingest")
    public String ingest() {
        return "ingest";
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}
