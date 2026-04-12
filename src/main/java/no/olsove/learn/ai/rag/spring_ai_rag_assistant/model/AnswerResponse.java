package no.olsove.learn.ai.rag.spring_ai_rag_assistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private String answer;
    private List<String> citations;
}
