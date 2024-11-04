package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import org.agoncal.application.vintagestore.model.Item;
import org.jboss.logging.Logger;

public class ItemsInStockTools {

  private static final Logger LOG = Logger.getLogger(VintageStoreChatBot.class);

  @Tool("Given a title, returns true if the item (CD or Book) is available in stock")
  boolean isItemInStock(@V("CD or Book title") String title) {
    long nbOfAvailableItems = Item.count("lower(title) like lower(concat('%', ?1,'%'))", title);
    LOG.info("Number of available items for " + title + " is " + nbOfAvailableItems);
    return nbOfAvailableItems > 0;
  }
}
