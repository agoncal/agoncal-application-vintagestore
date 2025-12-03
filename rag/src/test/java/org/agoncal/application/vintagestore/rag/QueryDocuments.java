package org.agoncal.application.vintagestore.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.cohere.CohereEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;

import static java.lang.System.exit;

public class QueryDocuments {

  // AI-Model API keys from environment variable
  private static final String COHERE_API_KEY = System.getenv("COHERE_API_KEY");
  private static final String COHERE_EMBED_ENGLISH = "embed-english-v3.0"; // or embed-english-light-v3.0
  // Constants for Qdrant configuration
  private static final String QDRANT_COLLECTION = "VintageStore";
  private static final String QDRANT_HOST = "localhost";
  private static final int QDRANT_PORT = 6334;

  private static final boolean IS_LOGGING_ENABLED = true;

  private static EmbeddingModel embeddingModel;
  private static EmbeddingStore<TextSegment> embeddingStore;

  public static void main(String[] args) throws Exception {
    embeddingModel = embeddingModel();
    embeddingStore = embeddingStore();

    // Question to ask
    Embedding embeddedQuestion = embeddingModel.embed("What is the VAT number of Vintage Store?").content();

    System.out.println("Dimensions: " + embeddedQuestion.dimension());
    System.out.println("Vector: " + embeddedQuestion.vectorAsList() + "\n");

    // Search
    EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
      .queryEmbedding(embeddedQuestion)
      .maxResults(2)
      .minScore(0.6)
      .build();

    // Results
    EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);
    searchResult.matches().forEach(match -> {
      System.out.println("Score:" + match.score());
      System.out.println("Content:" + match.embedded().text());
    });

    exit(0);
  }

  private static EmbeddingStore<TextSegment> embeddingStore() throws Exception {
    QdrantClient qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false).build());
    return QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(QDRANT_COLLECTION)
      .build();
  }

  private static EmbeddingModel embeddingModel() {
    EmbeddingModel cohereEmbeddingModel = CohereEmbeddingModel.builder()
      .apiKey(COHERE_API_KEY)
      .modelName(COHERE_EMBED_ENGLISH)
      .inputType("search_document")
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    return cohereEmbeddingModel;
  }
}
