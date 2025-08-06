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

  @Tool(name = "check_item_availability", value = "Checks if a book or CD is available in the vintage store inventory. Use this when customers ask about availability of specific titles, authors, or partial matches.")
  boolean isItemInStock(@V("The title, author, or partial title of the book or CD to check") String title) {
    if (title == null || title.trim().isEmpty()) {
      LOG.info("Title is null or empty, returning false");
      return false;
    }
    long nbOfAvailableItems = Item.findLikeTitle(title);
    LOG.info("Number of available items for " + title + " is " + nbOfAvailableItems);
    return nbOfAvailableItems > 0;
  }

  @Tool(name = "get_top_rated_items", value = "Retrieves all items with the highest rating (5 stars) from the vintage store catalog. Use this when customers ask for bestsellers, highest-rated, or premium recommendations.")
  List<Item> getTopRatedItems() {
    List<Item> topRatedItems = Item.findTopRated();
    LOG.info("Found " + topRatedItems.size() + " top-rated items");
    return topRatedItems;
  }

  @Tool(name = "search_catalog", value = "Searches the entire vintage store catalog by keyword, looking through titles, descriptions, and metadata. Use this for broad searches when customers describe what they're looking for rather than specific titles.")
  List<Item> searchItems(@V("Keyword or phrase to search for in titles, descriptions, or content") String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      LOG.info("Search keyword is null or empty, returning empty list");
      return Collections.emptyList();
    }
    List<Item> searchResults = Item.search(keyword);
    LOG.info("Found " + searchResults.size() + " items matching keyword: " + keyword);
    return searchResults;
  }
}
