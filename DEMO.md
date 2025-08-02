# Demo

## Initialization

Remove most of the settings in the ChatAssistant and ChatBot.

* In `VintageStoreChatAssistant` remove the `@SystemMessage`
* In `VintageStoreChatBot` only keep the `model()` method 
* Prompt "Do you know anything about VintageStore ?"

```java
@RegisterAiService
@SessionScoped
public interface VintageStoreChatAssistant {

  String chat(String userMessage);
}
```

```java
@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private VintageStoreChatAssistant assistant;
  
  @OnOpen
  public String onOpen() throws Exception {
    assistant = assistant(model());
    String answer = assistant.chat(WELCOME_PROMPT);
    return answer;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    String answer;
    answer = assistant.chat(message);
    return answer;
  }

  static VintageStoreChatAssistant assistant(ChatModel model) {
    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
      .chatModel(model)
      .build();

    return assistant;
  }

  static ChatModel model() {
    ChatModel model = AnthropicChatModel.builder()
      .apiKey(ANTHROPIC_API_KEY)
      .modelName("claude-sonnet-4-20250514")
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(true)
      .logResponses(true)
      .build();

    return model;
  }

}  
```

## System Prompt

* In `VintageStoreChatAssistant` add the system prompt
* Restart Quarkus (press 'r' in the terminal) because bot in created in onMessage
* Prompt "Do you know anything about VintageStore ?"
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"

## Memory

* In `VintageStoreChatAssistant` add the memory
* Restart Quarkus (press 'r' in the terminal) because bot in created in onMessage
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"

```java
  @OnOpen
  public String onOpen() throws Exception {
    assistant = assistant(model(), memory());
    String answer = assistant.chat(WELCOME_PROMPT);
    return answer;
  }


  static ChatMemory memory() {
    ChatMemory chatMemory = MessageWindowChatMemory.builder()
      .maxMessages(20)
      .build();

    return chatMemory;
  }

  static VintageStoreChatAssistant assistant(ChatModel model, ChatMemory memory) {
    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
      .chatModel(model)
      .chatMemory(memory)
      .build();
  
    return assistant;
  }
```
