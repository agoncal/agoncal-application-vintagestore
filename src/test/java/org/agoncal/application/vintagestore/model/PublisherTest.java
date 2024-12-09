package org.agoncal.application.vintagestore.model;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PublisherTest {

  @Test
  void shouldFindAll() {

    PanacheQuery<Publisher> publishers = Publisher.findAll();
    assertEquals(11, publishers.count());
  }
}
