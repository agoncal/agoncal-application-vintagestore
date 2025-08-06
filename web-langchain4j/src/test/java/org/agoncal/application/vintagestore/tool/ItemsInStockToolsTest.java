package org.agoncal.application.vintagestore.tool;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.agoncal.application.vintagestore.model.Item;
import java.util.List;

@QuarkusTest
class ItemsInStockToolsTest {

  @Inject
  ItemsInStockTools tool;

  // Test for specific ranking values
  @Test
  void shouldFindHighRankedItems() {
    List<Item> topRatedItems = tool.getTopRatedItems();
    assertNotNull(topRatedItems);
    if (!topRatedItems.isEmpty()) {
      for (Item item : topRatedItems) {
        assertEquals(5, item.rank);
      }
    }
  }

  @Test
  void shouldReturnTopRatedItems() {
    List<Item> topRatedItems = tool.getTopRatedItems();
    assertNotNull(topRatedItems);
    for (Item item : topRatedItems) {
      assertEquals(5, item.rank);
    }
  }

  @Test
  void shouldReturnEmptyListWhenNoTopRatedItems() {
    List<Item> topRatedItems = tool.getTopRatedItems();
    assertNotNull(topRatedItems);
  }

  @Test
  void shouldSearchItemsByKeyword() {
    List<Item> searchResults = tool.searchItems("Java");
    assertNotNull(searchResults);
    assertFalse(searchResults.isEmpty());
  }

  @Test
  void shouldSearchItemsCaseInsensitive() {
    List<Item> searchResults1 = tool.searchItems("java");
    List<Item> searchResults2 = tool.searchItems("JAVA");
    List<Item> searchResults3 = tool.searchItems("Java");

    assertNotNull(searchResults1);
    assertNotNull(searchResults2);
    assertNotNull(searchResults3);
    assertEquals(searchResults1.size(), searchResults2.size());
    assertEquals(searchResults2.size(), searchResults3.size());
  }

  @Test
  void shouldReturnEmptyListForUnknownKeyword() {
    List<Item> searchResults = tool.searchItems("ZZZZZZZZZZ");
    assertNotNull(searchResults);
    assertEquals(0, searchResults.size());
  }

  @Test
  void shouldHandleNullSearchKeyword() {
    List<Item> searchResults = tool.searchItems(null);
    assertNotNull(searchResults);
  }

  @Test
  void shouldHandleEmptySearchKeyword() {
    List<Item> searchResults = tool.searchItems("");
    assertNotNull(searchResults);
  }
}
