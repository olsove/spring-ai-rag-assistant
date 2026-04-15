package no.olsove.learn.ai.rag.spring_ai_rag_assistant.ingest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {

    private final VectorStore vectorStore;

    @Value("${rag.ingestion.chunk-size:1000}")
    private int chunkSize;

    @Value("${rag.ingestion.chunk-overlap:100}")
    private int chunkOverlap;

    public void ingest(Resource resource) {
        log.info("Ingesting resource: {} with chunk-size: {} and chunk-overlap: {}", 
                resource.getFilename(), chunkSize, chunkOverlap);
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.get();
        String fileName = resource.getFilename() != null ? resource.getFilename() : "unknown-file";
        List<Document> documentsWithMetadata = documents.stream()
                .map(document -> new Document(document.getContent(), Map.of("file_name", fileName)))
                .toList();

        TokenTextSplitter splitter = new TokenTextSplitter(chunkSize, chunkOverlap, 5, 10000, true);
        List<Document> splitDocuments = splitter.apply(documentsWithMetadata);

        log.info("Adding {} chunks to vector store", splitDocuments.size());
        vectorStore.add(splitDocuments);
        log.info("Ingestion completed");
    }
}
