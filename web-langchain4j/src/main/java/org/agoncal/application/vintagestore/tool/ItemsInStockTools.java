package org.agoncal.application.vintagestore.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import jakarta.enterprise.context.ApplicationScoped;
import org.agoncal.application.vintagestore.model.Item;
import org.jboss.logging.Logger;
import java.util.List;
import java.util.Collections;

@ApplicationScoped
public class ItemsInStockTools {

  private static final Logger LOG = Logger.getLogger(ItemsInStockTools.class);

  @Tool("Given a title, returns true if the item (CD or Book) is available in stock")
  boolean isItemInStock(@V("CD or Book title") String title) {
    if (title == null || title.trim().isEmpty()) {
      LOG.info("Title is null or empty, returning false");
      return false;
    }
    long nbOfAvailableItems = Item.findLikeTitle(title);
    LOG.info("Number of available items for " + title + " is " + nbOfAvailableItems);
    return nbOfAvailableItems > 0;
  }

  @Tool("Returns a list of top-rated items (rank = 5)")
  List<Item> getTopRatedItems() {
    List<Item> topRatedItems = Item.findTopRated();
    LOG.info("Found " + topRatedItems.size() + " top-rated items");
    return topRatedItems;
  }

  @Tool("Search for items by keyword in title or description")
  List<Item> searchItems(@V("Search keyword") String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      LOG.info("Search keyword is null or empty, returning empty list");
      return Collections.emptyList();
    }
    List<Item> searchResults = Item.search(keyword);
    LOG.info("Found " + searchResults.size() + " items matching keyword: " + keyword);
    return searchResults;
  }
}
