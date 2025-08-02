# Demo

## Show the VintageStore application

* In `VintageStoreChatAssistant` remove the `@SystemMessage`
* In `VintageStoreChatBot` only keep the WebSocket methods
* Show logs
* Browse CD and Books
* Show Terms and Conditions
* Login/Profile/Logout
* Chat: disconnect/connect/send a message
* Chat: CLEAR CONVERSATION

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
  
  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened");
    return "WebSocket chat connection opened";
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message);
    return message;
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed");
  }
}
```


## Adding Chat Bot

Remove most of the settings in the ChatAssistant and ChatBot.

* In `VintageStoreChatBot` add `assistant()` and `model()` methods
* Add the Assistant in the `@OnOpen` and `@OnTextMessage` methods
* Restart Quarkus (press 's' in the terminal)
* Prompt "Do you know anything about VintageStore ?"

```java
@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private VintageStoreChatAssistant assistant;
  
  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened");
    assistant = assistant(model());
    String answer = assistant.chat(WELCOME_PROMPT);
    return answer;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message);
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
* Show logs and check the system prompt
* Restart Quarkus (press 's' in the terminal)
* Prompt "Do you know anything about VintageStore ?"
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"

## Memory

* In `VintageStoreChatAssistant` add the memory
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat because the Assistant is initialized at the `@OnOpen`
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"
* DISCONNECT AND CONNECT WEBSOCKET, MEMORY IS LOST, NOT IN PERSISTENT STORAGE
* Prompt "My name is Antonio"

```java
  @OnOpen
  public String onOpen() throws Exception {
    assistant = assistant(model(), memory());
    // ...
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

## Memory in Persistent Storage

* Start Redis with `docker compose -f src/main/docker/redis.yml up -d`
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"
* DISCONNECT AND CONNECT WEBSOCKET, MEMORY IS LOST, NOT IN PERSISTENT STORAGE
* Prompt "My name is Antonio"
* Show the Redis Commander and copy the content to the logs.json

```java
  static ChatMemory memory() {
  ChatMemoryStore memoryStore = RedisChatMemoryStore.builder()
    .host("localhost")
    .port(6379)
    .build();

  ChatMemory chatMemory = MessageWindowChatMemory.builder()
    .maxMessages(20)
    .chatMemoryStore(memoryStore)
    .build();

  return chatMemory;
}
```
