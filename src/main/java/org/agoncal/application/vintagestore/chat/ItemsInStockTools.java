package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.agent.tool.Tool;

public class ItemsInStockTools {

  @Tool("Returns true if the item (CD or Book) in stock")
  boolean isItemInStock() {
    return true;
  }
}
