package org.agoncal.application.vintagestore.model;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GenreTest {

  @Test
  void shouldFindAll() {

    PanacheQuery<Genre> genres = Genre.findAll();
    assertEquals(15, genres.count());
  }
}
