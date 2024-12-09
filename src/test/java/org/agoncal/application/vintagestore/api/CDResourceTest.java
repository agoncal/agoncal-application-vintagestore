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
class CDResourceTest {

  @Test
  public void shouldGetAllCDs() {
    ArrayList cds =
      given()
        .header(ACCEPT, APPLICATION_JSON).
        when()
        .get("/cds").
        then()
        .statusCode(OK.getStatusCode())
        .extract().body().as(ArrayList.class);

    assertEquals(100, cds.size());
  }

  @Test
  public void shouldGetAllGenres() {
    ArrayList genres =
      given()
        .header(ACCEPT, APPLICATION_JSON).
        when()
        .get("/cds/genres").
        then()
        .statusCode(OK.getStatusCode())
        .extract().body().as(ArrayList.class);

    assertEquals(15, genres.size());
  }

  @Test
  public void shouldGetAllLabels() {
    ArrayList labels =
      given()
        .header(ACCEPT, APPLICATION_JSON).
        when()
        .get("/cds/labels").
        then()
        .statusCode(OK.getStatusCode())
        .extract().body().as(ArrayList.class);

    assertEquals(9, labels.size());
  }

  @Test
  public void shouldGetAllMusicians() {
    ArrayList musicians =
      given()
        .header(ACCEPT, APPLICATION_JSON).
        when()
        .get("/cds/musicians").
        then()
        .statusCode(OK.getStatusCode())
        .extract().body().as(ArrayList.class);

    assertEquals(17, musicians.size());
  }
}
