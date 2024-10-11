package org.agoncal.application.vintagestore;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/books")
public class BookResource {

  @GET
  @Path("/categories")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Category> getAllCategories() {
    return Category.listAll();
  }
}
