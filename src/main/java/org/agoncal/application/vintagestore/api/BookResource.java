package org.agoncal.application.vintagestore.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.agoncal.application.vintagestore.model.Author;
import org.agoncal.application.vintagestore.model.Category;
import org.agoncal.application.vintagestore.model.Publisher;
import org.agoncal.application.vintagestore.model.Book;

import java.util.List;

@Path("/books")
public class BookResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Book> getAllBooks() {
    return Book.listAll();
  }

  @GET
  @Path("/categories")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Category> getAllCategories() {
    return Category.listAll();
  }

  @GET
  @Path("/publishers")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Publisher> getAllPublishers() {
    return Publisher.listAll();
  }

  @GET
  @Path("/authors")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Author> getAllAuthors() {
    return Author.listAll();
  }
}
