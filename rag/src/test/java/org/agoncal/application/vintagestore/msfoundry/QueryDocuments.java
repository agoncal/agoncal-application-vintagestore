package org.agoncal.application.vintagestore.msfoundry;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openaiofficial.OpenAiOfficialChatModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.azure.search.AzureAiSearchEmbeddingStore;

import static java.lang.System.exit;

public class QueryDocuments {

  // AI-Model API keys from environment variable
  private static final String FOUNDRY_URI = System.getenv("FOUNDRY_IQ_URI");
  private static final String FOUNDRY_API_KEY = System.getenv("FOUNDRY_IQ_API_KEY");
  private static final String COHERE_EMBED_ENGLISH = "Cohere-embed-v3-english"; // "text-embedding-3-large"

  private static final boolean IS_LOGGING_ENABLED = true;

  private static EmbeddingModel embeddingModel;
  private static EmbeddingStore<TextSegment> embeddingStore;

  public static void main(String[] args) throws Exception {
//    embeddingModel = embeddingModel();
//    embeddingStore = embeddingStore();

    chat(claudeSonnetChatModel());
    chat(gptChatModel());
    chat(gptOfficialChatModel());
    chat(routerChatModel());
    chat(deepseekChatModel());
//    embedAndSearch();

    exit(0);
  }

  private static void chat(ChatModel chatModel) {
    // Chat
    System.out.println("################################## " + chatModel.getClass().getSimpleName());
    String answer = chatModel.chat("What is the VAT number of Vintage Store?");
    System.out.println("Chat Model \n" + answer + "\n");
  }

  private static void embedAndSearch() {
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
  }

  private static EmbeddingStore<TextSegment> embeddingStore() throws Exception {
    AzureAiSearchEmbeddingStore store = AzureAiSearchEmbeddingStore.builder()
      .endpoint(FOUNDRY_URI)
      .apiKey(FOUNDRY_API_KEY)
      .dimensions(1024)
      .build();

    return store;
  }

  private static EmbeddingModel embeddingModel() {
    EmbeddingModel cohereEmbeddingModel = OpenAiEmbeddingModel.builder()
      .apiKey(FOUNDRY_API_KEY)
      .baseUrl(FOUNDRY_URI)
      .modelName(COHERE_EMBED_ENGLISH)
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    return cohereEmbeddingModel;
  }

  private static ChatModel claudeSonnetChatModel() {
    return AnthropicChatModel.builder()
      .apiKey(FOUNDRY_API_KEY)
      .baseUrl("https://ia-my-rag-project.services.ai.azure.com/anthropic/v1")
      .modelName("claude-sonnet-4-5")
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();
  }

  private static ChatModel gptChatModel() {
    return OpenAiChatModel.builder()
      .apiKey(FOUNDRY_API_KEY)
      .baseUrl("https://ia-my-rag-project.openai.azure.com/openai/v1/")
      .modelName("gpt-5-nano")
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();
  }

  private static ChatModel gptOfficialChatModel() {
    return OpenAiOfficialChatModel.builder()
      .apiKey(FOUNDRY_API_KEY)
      .baseUrl("https://ia-my-rag-project.openai.azure.com/openai/v1/")
      .modelName("gpt-5-nano")
      .build();
  }

  private static ChatModel routerChatModel() {
    return OpenAiChatModel.builder()
      .apiKey(FOUNDRY_API_KEY)
      .baseUrl("https://ia-my-rag-project.openai.azure.com/openai/v1/")
      .modelName("model-router")
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();
  }

  private static ChatModel deepseekChatModel() {
    OpenAiChatModel deep = OpenAiChatModel.builder()
      .apiKey(FOUNDRY_API_KEY)
//      .baseUrl("https://ia-my-rag-project.services.ai.azure.com/api/projects/my-rag-project")
//      .baseUrl("https://ia-my-rag-project.cognitiveservices.azure.com/openai/deployments/DeepSeek-V3/chat/completions?api-version=2024-05-01-preview")
      .baseUrl("https://ia-my-rag-project.openai.azure.com/openai/v1/")
      .modelName("toto")
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    return deep;
  }
}
