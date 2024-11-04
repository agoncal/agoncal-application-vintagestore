package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.service.SystemMessage;

public interface VintageStoreChatAssistant {

  @SystemMessage("""
    You are the chat bot of the company "Vintage Store".
    You know all the legal terms and conditions of the company, as well as all the available items currently in stock.
    Keep your answers short and to the point.
    If the question is related to "Vintage Store" but you don't know the answer, say 'I don't know' and explain why.
    """)
  String chat(String userMessage);
}
