package org.agoncal.application.vintagestore.chat;

import static org.agoncal.application.vintagestore.chat.VintageStoreChatBot.memory;
import static org.agoncal.application.vintagestore.chat.VintageStoreChatBot.embeddingStore;
import static org.agoncal.application.vintagestore.chat.VintageStoreChatBot.model;
import static org.agoncal.application.vintagestore.chat.VintageStoreChatBot.assistant;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class VintageStoreChatAssistantTest {

  @Test
  @Disabled
  void shouldChat() throws Exception {
    VintageStoreChatAssistant assistant = assistant(embeddingStore(), model(), memory());

    System.out.println(assistant.chat("Hello, my name is Antonio?"));
    // Hello Antonio! How can I assist you today with information about "Vintage Store"?

    System.out.println(assistant.chat("Who are you?"));
    // I am the chat bot for "Vintage Store." I'm here to assist you with information about our products, services, and legal terms. If you have any questions or need help, feel free to ask!

    System.out.println(assistant.chat("Where is your company located?"));
    // The Vintage Store is located in Paris, France. If you have any more questions, feel free to ask!

    System.out.println(assistant.chat("What is the capital of France?"));
    // The capital of France is Paris. Please note that I am a chat bot primarily designed to answer questions related to "Vintage Store." If you have any questions about the store, feel free to ask!

    System.out.println(assistant.chat("What's my name?"));
    // You mentioned earlier that your name is Antonio. If you have any other questions or need assistance, feel free to ask!

    System.out.println(assistant.chat("When was your ACCEPTABLE USE POLICY document updated?"));
    // ...

    System.out.println(assistant.chat("And when was your BACON BURGER RECIPE document updated?"));
    // ...

    System.out.println(assistant.chat("Do you have any available book on Clojure?"));
    // ...

    System.out.println(assistant.chat("And any books on Burgers?"));
    // ...

  }
}
