package no.olsove.learn.ai.rag.spring_ai_rag_assistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {
    private String query;
}
