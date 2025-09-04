package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Moderate;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import jakarta.enterprise.context.SessionScoped;

@SessionScoped
public interface VintageStoreAssistant {

  String chat(@UserMessage String userMessage);
}
