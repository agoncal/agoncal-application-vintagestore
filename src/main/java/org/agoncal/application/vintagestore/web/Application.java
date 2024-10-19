package org.agoncal.application.vintagestore.web;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.Path;
import org.agoncal.application.vintagestore.model.Book;
import org.agoncal.application.vintagestore.model.CD;

import java.util.List;

public class Application extends Controller {

  @CheckedTemplate
  static class Templates {
//    public static native TemplateInstance main();

    public static native TemplateInstance index();

    public static native TemplateInstance books(List<Book> books);

    public static native TemplateInstance cds(List<CD> cds);
  }

  @Path("/")
  public TemplateInstance index() {
    return Templates.index();
  }

  @Path("/view/books")
  public TemplateInstance books() {
    return Templates.books(Book.listAll());
  }

  @Path("/view/cds")
  public TemplateInstance cds() {
    return Templates.cds(CD.listAll());
  }
}
