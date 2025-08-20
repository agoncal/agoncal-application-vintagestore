package org.agoncal.application.vintagestore.summarize;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import static dev.langchain4j.internal.ValidationUtils.ensureGreaterThanZero;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.service.memory.ChatMemoryService;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SummarizingTokenWindowChatMemory implements ChatMemory {

  private static final Logger LOG = Logger.getLogger(SummarizingTokenWindowChatMemory.class);

  private final Object id;
  private final Integer maxTokens;
  private final TokenCountEstimator tokenCountEstimator;
  private final ChatMemoryStore store;
  private final Summarizer summarizer;

  private static final int NB_OF_MESSAGE_TO_KEEP = 2;

  private SummarizingTokenWindowChatMemory(Builder builder) {
    this.id = ensureNotNull(builder.id, "id");
    this.maxTokens = ensureGreaterThanZero(builder.maxTokens, "maxTokens");
    this.tokenCountEstimator = ensureNotNull(builder.tokenCountEstimator, "tokenizer");
    this.store = ensureNotNull(builder.store, "store");
    this.summarizer = ensureNotNull(builder.summarizer, "summarizer");
  }

  @Override
  public Object id() {
    return id;
  }

  @Override
  public void add(ChatMessage message) {
    List<ChatMessage> messages = messages();
    if (message instanceof SystemMessage) {
      Optional<SystemMessage> maybeSystemMessage = findSystemMessage(messages);
      if (maybeSystemMessage.isPresent()) {
        if (maybeSystemMessage.get().equals(message)) {
          return; // do not add the same system message
        } else {
          messages.remove(maybeSystemMessage.get()); // need to replace existing system message
        }
      }
    }
    messages.add(message);
    ensureCapacity(messages, maxTokens, tokenCountEstimator);
    store.updateMessages(id, messages);
  }

  @Override
  public List<ChatMessage> messages() {
    List<ChatMessage> messages = new ArrayList<>(store.getMessages(id));
    ensureCapacity(messages, maxTokens, tokenCountEstimator);
    return messages;
  }

  @Override
  public void clear() {
    store.deleteMessages(id);
  }

  private void ensureCapacity(List<ChatMessage> messages, int maxTokens, TokenCountEstimator estimator) {

    if (messages.isEmpty()) {
      return; // No messages
    }
    if (messages.size() <= NB_OF_MESSAGE_TO_KEEP) {
      return; // Always leave a few messages (last user/assistant + maybe tools) for context
    }

    if (estimator.estimateTokenCountInMessages(messages) <= maxTokens) {
      return; // We are within capacity, no need to summarize
    }

    // If we exceed tokens, let's summarize the older messages (except system msg & possibly the newest).
    // 1) Separate out the system message if present at index 0.
    // 2) Summarize everything from startIndex but leave the last user or assistant message "unsummarized" for context.
    // 3) Insert the summary as a single message.

    // If there is a system message, remove it so we only summarise the conversation. The System message will be added back as is
    Optional<SystemMessage> maybeSystem = findSystemMessage(messages);
    maybeSystem.ifPresent(messages::remove);

    // Now we can work with the non-system messages
    int startIndex = 0;
    int endIndex = messages.size() - NB_OF_MESSAGE_TO_KEEP; // Leave the a few messages for context

    // Get the messages to summarize (everything except system & last)
    List<ChatMessage> messagesToSummarize = new ArrayList<>(messages.subList(startIndex, endIndex));

    // Generate the summary
    String summary = summarizer.summarize(messagesToSummarize);

    // Replace the summarized messages with the summary
    messages.subList(startIndex, endIndex).clear();
    messages.add(startIndex, UserMessage.from("We've been communicating for a while. This is our conversation summary: " + summary));

    // Re-add system message if we had one
    maybeSystem.ifPresent(messages::addFirst);
  }

  private static Optional<SystemMessage> findSystemMessage(List<ChatMessage> messages) {
    return messages.stream()
      .filter(message -> message instanceof SystemMessage)
      .map(message -> (SystemMessage) message)
      .findAny();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Object id = ChatMemoryService.DEFAULT;
    private Integer maxTokens;
    private TokenCountEstimator tokenCountEstimator;
    private ChatMemoryStore store;
    private Summarizer summarizer;

    /**
     * @param id The ID of the {@link ChatMemory}.
     *           If not provided, a "default" will be used.
     * @return builder
     */
    public Builder id(Object id) {
      this.id = id;
      return this;
    }

    /**
     * @param maxTokens           The maximum number of tokens to retain.
     *                            Chat memory will retain as many of the most recent messages as can fit into {@code maxTokens}.
     *                            Messages are indivisible. If an old message doesn't fit, it is evicted completely.
     * @param tokenCountEstimator A {@link TokenCountEstimator} responsible for counting tokens in the messages.
     * @return builder
     */
    public Builder maxTokens(Integer maxTokens, TokenCountEstimator tokenCountEstimator) {
      this.maxTokens = maxTokens;
      this.tokenCountEstimator = tokenCountEstimator;
      return this;
    }

    /**
     * @param store The chat memory store responsible for storing the chat memory state.
     * @return builder
     */
    public Builder chatMemoryStore(ChatMemoryStore store) {
      this.store = store;
      return this;
    }

    public Builder summarizer(Summarizer summarizer) {
      this.summarizer = summarizer;
      return this;
    }

    public SummarizingTokenWindowChatMemory build() {
      return new SummarizingTokenWindowChatMemory(this);
    }
  }
}
