package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService
@SessionScoped
public interface VintageStoreAIService {

  @SystemMessage("You are chatbot that helps users with their queries")
  String chat(String message);
}
