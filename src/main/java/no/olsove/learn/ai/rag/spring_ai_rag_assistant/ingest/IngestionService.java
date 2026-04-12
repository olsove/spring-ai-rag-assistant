package no.olsove.learn.ai.rag.spring_ai_rag_assistant.ingest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {

    private final VectorStore vectorStore;

    public void ingest(Resource resource) {
        log.info("Ingesting resource: {}", resource.getFilename());
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.get();

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(documents);

        log.info("Adding {} chunks to vector store", splitDocuments.size());
        vectorStore.add(splitDocuments);
        log.info("Ingestion completed");
    }
}
