package org.agoncal.application.vintagestore.api;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@QuarkusTest
class BookResourceTest {

  @Test
  public void shouldGetAllBooks() {
    ArrayList books =
      given()
        .header(ACCEPT, APPLICATION_JSON).
        when()
        .get("/books").
        then()
        .statusCode(OK.getStatusCode())
        .extract().body().as(ArrayList.class);

    assertEquals(99, books.size());
  }

  @Test
  public void shouldGetAllCategories() {
    ArrayList categories =
      given()
        .header(ACCEPT, APPLICATION_JSON).
        when()
        .get("/books/categories").
        then()
        .statusCode(OK.getStatusCode())
        .extract().body().as(ArrayList.class);

    assertEquals(13, categories.size());
  }

  @Test
  public void shouldGetAllPublishers() {
    ArrayList publishers =
      given()
        .header(ACCEPT, APPLICATION_JSON).
        when()
        .get("/books/publishers").
        then()
        .statusCode(OK.getStatusCode())
        .extract().body().as(ArrayList.class);

    assertEquals(11, publishers.size());
  }

  @Test
  public void shouldGetAllAuthors() {
    ArrayList authors =
      given()
        .header(ACCEPT, APPLICATION_JSON).
        when()
        .get("/books/authors").
        then()
        .statusCode(OK.getStatusCode())
        .extract().body().as(ArrayList.class);

    assertEquals(16, authors.size());
  }
}
