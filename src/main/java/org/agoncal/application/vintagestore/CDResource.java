package org.agoncal.application.vintagestore;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.agoncal.application.vintagestore.model.Genre;
import org.agoncal.application.vintagestore.model.Label;
import org.agoncal.application.vintagestore.model.Musician;

import java.util.List;

@Path("/cds")
public class CDResource {

  @GET
  @Path("/genres")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Genre> getAllGenres() {
    return Genre.listAll();
  }

  @GET
  @Path("/labels")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Label> getAllLabels() {
    return Label.listAll();
  }

  @GET
  @Path("/musicians")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Musician> getAllMusicians() {
    return Musician.listAll();
  }
}
