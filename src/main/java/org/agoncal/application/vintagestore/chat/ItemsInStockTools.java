package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import jakarta.enterprise.context.ApplicationScoped;
import org.agoncal.application.vintagestore.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ItemsInStockTools {

  private static final Logger LOG = LoggerFactory.getLogger(ItemsInStockTools.class);

  @Tool("Given a title, returns true if the item (CD or Book) is available in stock")
  boolean isItemInStock(@V("CD or Book title") String title) {
    long nbOfAvailableItems = Item.findLikeTitle(title);
    LOG.info("Number of available items for {} is {}", title, nbOfAvailableItems);
    return nbOfAvailableItems > 0;
  }
}
