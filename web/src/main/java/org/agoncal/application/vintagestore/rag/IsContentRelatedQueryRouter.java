package org.agoncal.application.vintagestore.rag;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.QueryRouter;
import org.jboss.logging.Logger;

import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class IsContentRelatedQueryRouter implements QueryRouter {

  private static final Logger LOG = Logger.getLogger(IsContentRelatedQueryRouter.class);
  private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage("""
    You are the official query router assistant for Vintage Store. Your role is to make sure that the user query is related to the business of the Vintage Store or not. Answer only 'yes' or 'no'.

    Vintage Store is a specialized e-commerce platform dedicated to vintage and collectible items, particularly focusing on:

    Product Categories:
    - Books: A curated collection of vintage and rare books across various categories, publishers, and authors
    - CDs: Vintage music albums from different genres, labels, and musicians

    Key Features:
    - AI-Powered Shopping Experience: Advanced chat assistance for personalized product recommendations and customer support
    - Comprehensive Catalog: Detailed product information including metadata like publication dates, ISBN numbers, artist details, and more
    - User Authentication: Secure sign-in system with user profiles and role-based access
    - Expert Curation: Each item is carefully selected for its vintage appeal and collectible value
    """);
  private static final PromptTemplate USER_MESSAGE_TEMPLATE = PromptTemplate.from("""
    Is the following query related to the business of Vintage Store? Answer only 'yes' or 'no'

    Query: {{it}}
    """
  );

  protected final ChatModel chatModel;
  protected final ContentRetriever contentRetriever;

  public IsContentRelatedQueryRouter(ChatModel chatModel, ContentRetriever contentRetriever) {
    this.chatModel = ensureNotNull(chatModel, "chatModel");
    this.contentRetriever = ensureNotNull(contentRetriever, "contentRetriever");
  }

  public static IsContentRelatedQueryRouter.ContentRelatedQueryRouterBuilder builder() {
    return new IsContentRelatedQueryRouter.ContentRelatedQueryRouterBuilder();
  }

  @Override
  public Collection<ContentRetriever> route(Query query) {

    UserMessage userMessage = USER_MESSAGE_TEMPLATE.apply(query.text()).toUserMessage();

    AiMessage aiMessage = chatModel.chat(SYSTEM_MESSAGE, userMessage).aiMessage();
    LOG.info("LLM decided: " + aiMessage.text());

    if (aiMessage.text().toLowerCase().contains("no")) {
      return emptyList();
    }

    return singletonList(contentRetriever);
  }

  public static class ContentRelatedQueryRouterBuilder {
    private ChatModel chatModel;
    private ContentRetriever contentRetriever;

    ContentRelatedQueryRouterBuilder() {
    }

    public IsContentRelatedQueryRouter.ContentRelatedQueryRouterBuilder chatModel(ChatModel chatModel) {
      this.chatModel = chatModel;
      return this;
    }

    public IsContentRelatedQueryRouter.ContentRelatedQueryRouterBuilder contentRetriever(ContentRetriever contentRetriever) {
      this.contentRetriever = contentRetriever;
      return this;
    }

    public IsContentRelatedQueryRouter build() {
      return new IsContentRelatedQueryRouter(this.chatModel, this.contentRetriever);
    }
  }
}
