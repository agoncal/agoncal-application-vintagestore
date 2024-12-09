package org.agoncal.application.vintagestore.chat;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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
}
