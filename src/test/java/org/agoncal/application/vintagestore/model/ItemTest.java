package org.agoncal.application.vintagestore.model;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ItemTest {

  @Test
  void shouldFindAll() {
    PanacheQuery<Item> items = Item.findAll();
    assertEquals(199, items.count());
  }

  @Test
  void shouldFindTitle() {
    assertEquals(1, Item.findLikeTitle("Clojure High Performance Programming"));
    assertEquals(1, Item.findLikeTitle("Clojure High Performance"));
    assertEquals(1, Item.findLikeTitle("Clojure High"));
    assertEquals(1, Item.findLikeTitle("clojure high performance programming"));
    assertEquals(1, Item.findLikeTitle("clojure high performance"));
    assertEquals(1, Item.findLikeTitle("clojure high"));
    assertEquals(1, Item.findLikeTitle("CLOJURE HIGH PERFORMANCE PROGRAMMING"));
    assertEquals(1, Item.findLikeTitle("CLOJURE HIGH PERFORMANCE"));
    assertEquals(1, Item.findLikeTitle("CLOJURE HIGH"));
  }

  @Test
  void shouldFindTitleBook() {
    assertEquals(3, Item.findLikeTitle("Advanced Java EE Development"));
  }

  @Test
  void shouldFindTitleCD() {
    assertEquals(2, Item.findLikeTitle("Swingin Xmas"));
  }

  @Test
  void shouldNotFindUnknownTitle() {
    assertEquals(0, Item.findLikeTitle("ZZZZZZZZZZ"));
  }
}
