package org.agoncal.application.contosostore.summarize;

import dev.langchain4j.data.message.ChatMessage;

import java.util.List;

public interface Summarizer {
  String summarize(List<ChatMessage> messages);
}
