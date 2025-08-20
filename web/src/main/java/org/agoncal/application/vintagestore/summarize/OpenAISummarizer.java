package org.agoncal.application.vintagestore.summarize;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.jboss.logging.Logger;

import java.util.List;

public class OpenAISummarizer implements Summarizer {

  private static final Logger LOG = Logger.getLogger(OpenAISummarizer.class);

  private final int desiredTokenLimit;
  private final SummarizerAssistant assistant;

  public OpenAISummarizer(OpenAiChatModel openAiChatModel, int desiredTokenLimit) {
    this.desiredTokenLimit = desiredTokenLimit;
    assistant = AiServices.builder(SummarizerAssistant.class)
      .chatModel(openAiChatModel)
      .build();
  }

  @Override
  public String summarize(List<ChatMessage> messages) {
    LOG.error("Summarizing " + messages.size() + " messages to fit in the token limit of " + desiredTokenLimit);

    StringBuilder promptBuilder = new StringBuilder("Summarize the following conversation: \n");
    for (ChatMessage msg : messages) {
      if (msg instanceof UserMessage) {
        promptBuilder.append("<user>\n").append(((UserMessage) msg).contents()).append("\n</user>\n");
      } else if (msg instanceof AiMessage) {
        promptBuilder.append("<assistant>\n").append(((AiMessage) msg).text()).append("\n</assistant>\n");
      } else if (msg instanceof SystemMessage) {
        promptBuilder.append("<system>\n").append(((SystemMessage) msg).text()).append("\n</system>\n");
      } else if (msg instanceof ToolExecutionResultMessage) {
        promptBuilder.append("<toolexecutionresult>\n").append(((ToolExecutionResultMessage) msg).text()).append("\n</toolexecutionresult>\n");
      }
    }

    String summary = assistant.summarize(promptBuilder.toString(), desiredTokenLimit);
    return summary.trim();
  }
}
