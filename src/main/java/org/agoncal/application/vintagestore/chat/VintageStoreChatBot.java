package org.agoncal.application.vintagestore.chat;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;

@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private final VintageStoreAIService aiService;

  public VintageStoreChatBot(VintageStoreAIService aiService) {
    this.aiService = aiService;
  }

  @OnOpen
  public String onOpen() {
    return aiService.chat("Hello, how can I help you?");
  }

  @OnTextMessage
  public String onMessage(String message) {
    return aiService.chat(message);
  }
}
