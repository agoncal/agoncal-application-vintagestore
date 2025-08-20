package org.agoncal.application.vintagestore.summarize;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface SummarizerAssistant {

  @SystemMessage("""
      You are an assistant tasked with summarizing previous chatbot conversations.

      Create a clear, concise summary of the conversation history, emphasizing important user preferences, requests, decisions, and discussed topics or tasks.
      Ensure the summary is coherent and useful for continuing the conversation, retaining details needed to fulfill user requests.

      Limit the summary to {{desiredTokenLimit}} tokens and use natural, readable language.
      Do not list conversation turns; synthesize the information into a unified narrative.
    """)
  String summarize(@UserMessage String messages, @V("desiredTokenLimit") int desiredTokenLimit);
}
