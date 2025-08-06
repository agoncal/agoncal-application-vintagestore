package org.agoncal.application.vintagestore.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import io.quarkus.arc.Arc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.agoncal.application.vintagestore.model.Item;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Collections;

@ApplicationScoped
public class ItemsInStockTools {

  private static final Logger LOG = Logger.getLogger(ItemsInStockTools.class);

  @Tool(name = "get_top_rated_items", value = "Retrieves all items with the highest rating (5 stars) from the vintage store catalog. Use this when customers ask for bestsellers, highest-rated, or premium recommendations.")
  List<Item> getTopRatedItems() {
    LOG.info("getTopRatedItems()");

    try {
      UserTransaction userTransaction = Arc.container().instance(UserTransaction.class).get();
      userTransaction.begin();

      List<Item> topRatedItems = Item.findTopRated();
      LOG.info("Found " + topRatedItems.size() + " top-rated items");

      userTransaction.commit();
      return topRatedItems;
    } catch (Exception e) {
      LOG.error("Error retrieving top-rated items", e);
      try {
        UserTransaction userTransaction = Arc.container().instance(UserTransaction.class).get();
        userTransaction.rollback();
      } catch (Exception rollbackException) {
        LOG.error("Error rolling back transaction", rollbackException);
      }
      return Collections.emptyList();
    }
  }

  @Tool(name = "search_catalog", value = "Searches the entire vintage store catalog by keyword, looking through titles, descriptions, and metadata. Use this for broad searches when customers describe what they're looking for rather than specific titles.")
  List<Item> searchItems(@V("Keyword or phrase to search for in titles, descriptions, or content") String keyword) {
    LOG.info("searchItems(" + keyword + ")");
    if (keyword == null || keyword.trim().isEmpty()) {
      LOG.info("Search keyword is null or empty, returning empty list");
      return Collections.emptyList();
    }

    try {
      UserTransaction userTransaction = Arc.container().instance(UserTransaction.class).get();
      userTransaction.begin();

      List<Item> searchResults = Item.search(keyword);
      LOG.info("Found " + searchResults.size() + " items matching keyword: " + keyword);

      userTransaction.commit();
      return searchResults;
    } catch (Exception e) {
      LOG.error("Error searching items with keyword: " + keyword, e);
      try {
        UserTransaction userTransaction = Arc.container().instance(UserTransaction.class).get();
        userTransaction.rollback();
      } catch (Exception rollbackException) {
        LOG.error("Error rolling back transaction", rollbackException);
      }
      return Collections.emptyList();
    }
  }
}
