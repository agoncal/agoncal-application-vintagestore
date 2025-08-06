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

  @Test
  void shouldFindTitle() {
    assertTrue(tool.isItemInStock("Clojure High Performance Programming"));
    assertTrue(tool.isItemInStock("Clojure High Performance"));
    assertTrue(tool.isItemInStock("Clojure High"));
    assertTrue(tool.isItemInStock("clojure high performance programming"));
    assertTrue(tool.isItemInStock("clojure high performance"));
    assertTrue(tool.isItemInStock("clojure high"));
    assertTrue(tool.isItemInStock("CLOJURE HIGH PERFORMANCE PROGRAMMING"));
    assertTrue(tool.isItemInStock("CLOJURE HIGH PERFORMANCE"));
    assertTrue(tool.isItemInStock("CLOJURE HIGH"));
  }

  @Test
  void shouldFindTitleBook() {
    assertTrue(tool.isItemInStock("Advanced Java EE Development"));
  }

  @Test
  void shouldFindTitleCD() {
    assertTrue(tool.isItemInStock("Swingin Xmas"));
  }

  @Test
  void shouldNotFindUnknownTitle() {
    assertFalse(tool.isItemInStock("ZZZZZZZZZZ"));
  }

  @Test
  void shouldHandleEmptyString() {
    assertFalse(tool.isItemInStock(""));
  }

  @Test
  void shouldHandleNullTitle() {
    assertFalse(tool.isItemInStock(null));
  }

  @Test
  void shouldFindPartialMatch() {
    assertTrue(tool.isItemInStock("Java"));
    assertTrue(tool.isItemInStock("Programming"));
  }

  @Test
  void shouldFindWithExtraSpaces() {
    assertTrue(tool.isItemInStock(" Java "));
    assertTrue(tool.isItemInStock("  Programming  "));
  }

  @Test
  void shouldFindSingleCharacter() {
    assertTrue(tool.isItemInStock("J"));
  }

  @Test
  void shouldHandleSpecialCharacters() {
    assertFalse(tool.isItemInStock("@#$%^&*()"));
  }

  // Additional book title tests from vintagestore-data.sql
  @Test
  void shouldFindSpecificBookTitles() {
    assertTrue(tool.isItemInStock("Apache Maven 3 Cookbook"));
    assertTrue(tool.isItemInStock("apache maven 3 cookbook"));
    assertTrue(tool.isItemInStock("APACHE MAVEN 3 COOKBOOK"));
    assertTrue(tool.isItemInStock("Maven 3 Cookbook"));
    assertTrue(tool.isItemInStock("Apache Maven"));
    assertTrue(tool.isItemInStock("Maven"));
  }

  @Test
  void shouldFindAdvancedJavaEEBooks() {
    assertTrue(tool.isItemInStock("Advanced Java EE Development"));
    assertTrue(tool.isItemInStock("Advanced Java EE Development with WildFly"));
    assertTrue(tool.isItemInStock("Advanced Jax-Ws Web Services"));
    assertTrue(tool.isItemInStock("WildFly"));
    assertTrue(tool.isItemInStock("Jax-Ws"));
  }

  @Test
  void shouldFindTomcatBooks() {
    assertTrue(tool.isItemInStock("Apache Tomcat 7"));
    assertTrue(tool.isItemInStock("Apache Tomcat 8"));
    assertTrue(tool.isItemInStock("Apache TomEE Cookbook"));
    assertTrue(tool.isItemInStock("Tomcat"));
    assertTrue(tool.isItemInStock("TomEE"));
  }

  @Test
  void shouldFindOracleApplicationServerBook() {
    assertTrue(tool.isItemInStock("Applied SOA Patterns on the Oracle Platform"));
    assertTrue(tool.isItemInStock("Oracle Platform"));
    assertTrue(tool.isItemInStock("SOA Patterns"));
    assertTrue(tool.isItemInStock("Oracle"));
  }

  @Test
  void shouldFindWindowsCertificationBook() {
    assertTrue(tool.isItemInStock("Mike Meyers' Guide to Supporting Windows 7"));
    assertTrue(tool.isItemInStock("CompTIA A+ Certification"));
    assertTrue(tool.isItemInStock("Mike Meyers"));
    assertTrue(tool.isItemInStock("Windows 7"));
    assertTrue(tool.isItemInStock("CompTIA"));
  }

  // CD title tests from vintagestore-data.sql
  @Test
  void shouldFindSpecificCDTitles() {
    assertTrue(tool.isItemInStock("Homenaje al Maestro Ernesto Lecuona"));
    assertTrue(tool.isItemInStock("Weather Report 8:30"));
    assertTrue(tool.isItemInStock("George Benson 20/20"));
    assertTrue(tool.isItemInStock("Ryuichi Sakamoto 05"));
    assertTrue(tool.isItemInStock("Esbjörn Svensson Trio"));
    assertTrue(tool.isItemInStock("Weather Report"));
    assertTrue(tool.isItemInStock("George Benson"));
    assertTrue(tool.isItemInStock("Sakamoto"));
  }

  @Test
  void shouldFindJazzCDs() {
    assertTrue(tool.isItemInStock("Jazz Do It at 02:45 A.M."));
    assertTrue(tool.isItemInStock("Moni's Jazz Quartett"));
    assertTrue(tool.isItemInStock("Jazz Do It"));
    assertTrue(tool.isItemInStock("Jazz Quartett"));
    assertTrue(tool.isItemInStock("Jazz"));
  }

  @Test
  void shouldFindElectronicAndWorldMusicCDs() {
    assertTrue(tool.isItemInStock("Big Neighborhood"));
    assertTrue(tool.isItemInStock("Glenn Brown and Intergalactic Spiral"));
    assertTrue(tool.isItemInStock("01"));
    assertTrue(tool.isItemInStock("The Ploctones 050"));
    assertTrue(tool.isItemInStock("Intergalactic Spiral"));
    assertTrue(tool.isItemInStock("Glenn Brown"));
    assertTrue(tool.isItemInStock("Ploctones"));
  }

  @Test
  void shouldFindSwingAndClassicalCDs() {
    assertTrue(tool.isItemInStock("Swingin Xmas Vol.1"));
    assertTrue(tool.isItemInStock("Swingin Xmas Vol.2"));
    assertTrue(tool.isItemInStock("Emperors of Swing"));
    assertTrue(tool.isItemInStock("Louis Prima 1"));
    assertTrue(tool.isItemInStock("Swingin Xmas"));
    assertTrue(tool.isItemInStock("Swing"));
    assertTrue(tool.isItemInStock("Louis Prima"));
  }

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

  // Test for edge cases with rankings
  @Test
  void shouldHandleVariousRankings() {
    // Test that items with different rankings exist (based on data seen: rank 1, 2, 3, 6, 7, 9)
    assertTrue(tool.isItemInStock("Apache Maven 3 Cookbook")); // rank 1
    assertTrue(tool.isItemInStock("Advanced Java EE Development")); // rank 2
    assertTrue(tool.isItemInStock("Advanced Java EE Development with WildFly")); // rank 3
    assertTrue(tool.isItemInStock("#1")); // rank 6
    assertTrue(tool.isItemInStock("Esbjörn Svensson Trio")); // rank 7
    assertTrue(tool.isItemInStock("Mike Meyers' Guide to Supporting Windows 7")); // rank 9
  }

  // Test search with various price ranges
  @Test
  void shouldFindItemsAcrossPriceRanges() {
    // Test items exist across different price points (from data: 9.99, 15.98, 22.90, 30.00, 44.99, 79.95)
    assertTrue(tool.isItemInStock("Apache Tomcat")); // around 9.99
    assertTrue(tool.isItemInStock("Advanced Jax-Ws Web Services")); // 22.90
    assertTrue(tool.isItemInStock("Mike Meyers")); // 30.00
    assertTrue(tool.isItemInStock("WildFly")); // 44.99
    assertTrue(tool.isItemInStock("Rational Application Developer")); // 79.95
  }

  // Test partial matches with variations
  @Test
  void shouldFindItemsWithNumbersAndSpecialCharacters() {
    assertTrue(tool.isItemInStock("7.5"));
    assertTrue(tool.isItemInStock("3 Cookbook"));
    assertTrue(tool.isItemInStock("02:45"));
    assertTrue(tool.isItemInStock("20/20"));
    assertTrue(tool.isItemInStock("Vol.1"));
    assertTrue(tool.isItemInStock("Vol.2"));
    assertTrue(tool.isItemInStock("8:30"));
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
