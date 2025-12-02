package org.agoncal.application.vintagestore.msfoundry;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.cohere.CohereEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import static dev.langchain4j.store.embedding.azure.search.AbstractAzureAiSearchEmbeddingStore.DEFAULT_INDEX_NAME;
import dev.langchain4j.store.embedding.azure.search.AzureAiSearchEmbeddingStore;

import static java.lang.System.exit;

public class QueryDocuments {

  // AI-Model API keys from environment variable
  private static final String FOUNDRY_IQ_API_KEY = System.getenv("FOUNDRY_IQ_API_KEY");
  private static final String FOUNDRY_IQ_URI = System.getenv("FOUNDRY_IQ_URI");
  private static final String COHERE_EMBED_ENGLISH = "Cohere-embed-v3-english";

  private static final boolean IS_LOGGING_ENABLED = true;

  private static EmbeddingModel embeddingModel;
  private static EmbeddingStore<TextSegment> embeddingStore;

  public static void main(String[] args) throws Exception {
    embeddingStore = embeddingStore();
    embeddingModel = embeddingModel();

    // Question to ask
    Embedding embeddedQuestion = embeddingModel.embed("What is the VAT number of Vintage Store?").content();

    // Search
    EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
      .queryEmbedding(embeddedQuestion)
      .maxResults(2)
      .minScore(0.6)
      .build();

    // Results
    EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);
    searchResult.matches().forEach(match -> {
      System.out.println(match.score());
      System.out.println(match.embedded().text());
    });

    exit(0);
  }

  private static EmbeddingStore<TextSegment> embeddingStore() throws Exception {
    return AzureAiSearchEmbeddingStore.builder()
      .endpoint(FOUNDRY_IQ_URI)
      .apiKey(FOUNDRY_IQ_API_KEY)
      .indexName(DEFAULT_INDEX_NAME)
      .dimensions(embeddingModel.dimension())
      .build();

  }

  private static EmbeddingModel embeddingModel() {
    EmbeddingModel cohereEmbeddingModel = CohereEmbeddingModel.builder()
      .apiKey(FOUNDRY_IQ_API_KEY)
      .modelName(COHERE_EMBED_ENGLISH)
      .inputType("search_document")
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    return cohereEmbeddingModel;
  }
}
