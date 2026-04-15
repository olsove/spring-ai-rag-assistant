package no.olsove.learn.ai.rag.spring_ai_rag_assistant.retrieval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrievalService {

    private static final Pattern QUERY_TERM_PATTERN = Pattern.compile("[a-z0-9]{4,}");

    private final VectorStore vectorStore;

    @Value("${rag.retrieval.top-k:3}")
    private int topK;

    @Value("${rag.retrieval.similarity-threshold:0.5}")
    private double similarityThreshold;

    public List<Document> retrieve(String query) {
        log.info("Retrieving documents for query: {}", query);
        SearchConfig primaryConfig = selectPrimarySearchConfig(query);
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(primaryConfig.topK())
                .similarityThreshold(primaryConfig.similarityThreshold())
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        log.info("Retrieved {} documents in primary search", documents.size());

        List<Document> rankedDocuments = rankByLexicalMatch(query, documents);

        if (rankedDocuments.isEmpty() || !containsAnyQueryTerm(query, rankedDocuments)) {
            SearchRequest fallbackRequest = SearchRequest.builder()
                    .query(query)
                    .topK(Math.max(topK * 2, 8))
                    .similarityThreshold(0.0)
                    .build();
            List<Document> fallbackDocuments = vectorStore.similaritySearch(fallbackRequest);
            log.info("Fallback retrieval returned {} documents", fallbackDocuments.size());
            rankedDocuments = mergeAndRank(query, rankedDocuments, fallbackDocuments);
        }

        List<Document> selectedDocuments = rankedDocuments.stream()
                .limit(topK)
                .toList();
        log.info("Retrieved {} documents after ranking", selectedDocuments.size());
        return selectedDocuments;
    }

    private SearchConfig selectPrimarySearchConfig(String query) {
        if (isBroadQuery(query)) {
            return new SearchConfig(Math.max(topK * 2, 8), 0.0);
        }
        return new SearchConfig(topK, similarityThreshold);
    }

    private boolean isBroadQuery(String query) {
        String normalizedQuery = query == null ? "" : query.toLowerCase(Locale.ROOT);
        return normalizedQuery.contains("limit")
                || normalizedQuery.contains("timeout")
                || normalizedQuery.contains("setting")
                || normalizedQuery.contains("configuration");
    }

    private List<Document> mergeAndRank(String query, List<Document> firstPass, List<Document> secondPass) {
        Map<String, Document> uniqueByContent = new LinkedHashMap<>();
        firstPass.forEach(document -> uniqueByContent.putIfAbsent(document.getContent(), document));
        secondPass.forEach(document -> uniqueByContent.putIfAbsent(document.getContent(), document));
        return rankByLexicalMatch(query, new ArrayList<>(uniqueByContent.values()));
    }

    private List<Document> rankByLexicalMatch(String query, List<Document> documents) {
        Set<String> terms = extractQueryTerms(query);
        return documents.stream()
                .sorted(Comparator.comparingInt((Document document) -> lexicalScore(document.getContent(), terms)).reversed())
                .toList();
    }

    private boolean containsAnyQueryTerm(String query, List<Document> documents) {
        Set<String> terms = extractQueryTerms(query);
        return documents.stream()
                .map(document -> document.getContent() == null ? "" : document.getContent().toLowerCase(Locale.ROOT))
                .anyMatch(content -> terms.stream().anyMatch(content::contains));
    }

    private int lexicalScore(String content, Set<String> terms) {
        String normalizedContent = content == null ? "" : content.toLowerCase(Locale.ROOT);
        int score = terms.stream()
                .mapToInt(term -> normalizedContent.contains(term) ? 1 : 0)
                .sum();

        if (terms.contains("limit") || terms.contains("limits")) {
            if (normalizedContent.contains("known limits")) {
                score += 5;
            }
            if (normalizedContent.contains("rate limiting")) {
                score += 5;
            }
            if (normalizedContent.contains("default limit")) {
                score += 3;
            }
        }
        return score;
    }

    private Set<String> extractQueryTerms(String query) {
        String normalizedQuery = query == null ? "" : query.toLowerCase(Locale.ROOT);
        java.util.regex.Matcher matcher = QUERY_TERM_PATTERN.matcher(normalizedQuery);
        java.util.Set<String> terms = new java.util.LinkedHashSet<>();
        while (matcher.find()) {
            terms.add(matcher.group());
        }
        return terms;
    }

    private record SearchConfig(int topK, double similarityThreshold) {
    }
}
