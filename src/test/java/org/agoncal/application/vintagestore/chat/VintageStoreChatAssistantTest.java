package org.agoncal.application.vintagestore.chat;

import static org.agoncal.application.vintagestore.chat.VintageStoreChatBot.embeddingStore;
import static org.agoncal.application.vintagestore.chat.VintageStoreChatBot.model;
import static org.agoncal.application.vintagestore.chat.VintageStoreChatBot.assistant;
import org.junit.jupiter.api.Test;

class VintageStoreChatAssistantTest {

  @Test
  void shouldChat() throws Exception {
    VintageStoreChatAssistant assistant = assistant(embeddingStore(), model());
    System.out.println(assistant.chat("Hello, my name is Antonio?"));
  }
}
