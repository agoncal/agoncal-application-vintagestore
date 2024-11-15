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
    // Hello Antonio! How can I assist you today with information about "Vintage Store"?

    System.out.println(assistant.chat("Who are you?"));
    // I am the chat bot for "Vintage Store." I'm here to assist you with information about our products, services, and legal terms. If you have any questions or need help, feel free to ask!

    System.out.println(assistant.chat("Where is the company located?"));
    // The Vintage Store is located in Paris, France. If you have any more questions, feel free to ask!

    System.out.println(assistant.chat("What is the capital of France?"));
    // The capital of France is Paris. Please note that I am a chat bot primarily designed to answer questions related to "Vintage Store." If you have any questions about the store, feel free to ask!

    System.out.println(assistant.chat("What's my name?"));
    // You mentioned earlier that your name is Antonio. If you have any other questions or need assistance, feel free to ask!
  }
}
