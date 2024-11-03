package org.agoncal.application.vintagestore.chat;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;

@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private final VintageStoreAIService bot;

  public VintageStoreChatBot(VintageStoreAIService bot) {
    this.bot = bot;
  }

  @OnOpen
  public String onOpen() {
    return bot.chat("Hello, how can I help you?");
  }

  @OnTextMessage
  public String onMessage(String message) {
    return bot.chat(message);
  }
}
