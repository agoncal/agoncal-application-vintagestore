package org.agoncal.application.vintagestore.web;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.agoncal.application.vintagestore.model.Book;
import org.agoncal.application.vintagestore.model.CD;

import java.util.List;

public class Application extends Controller {

  @CheckedTemplate
  static class Templates {
    public static native TemplateInstance index();

    public static native TemplateInstance chat();

    public static native TemplateInstance books(List<Book> books);

    public static native TemplateInstance cds(List<CD> cds);

    public static native TemplateInstance book(Book book);

    public static native TemplateInstance cd(CD cd);
  }

  @Path("/")
  public TemplateInstance index() {
    return Templates.index();
  }

  @Path("/view/chat")
  public TemplateInstance chat() {
    return Templates.chat();
  }

  @Path("/view/books")
  public TemplateInstance books() {
    return Templates.books(Book.listAll());
  }

  @Path("/view/cds")
  public TemplateInstance cds() {
    return Templates.cds(CD.listAll());
  }

  @Path("/view/book/{id}")
  public TemplateInstance book(@PathParam("id") Long id) {
    Book book = Book.findById(id);
    if (book == null) {
      notFound();
    }
    return Templates.book(book);
  }

  @Path("/view/cd/{id}")
  public TemplateInstance cd(@PathParam("id") Long id) {
    CD cd = CD.findById(id);
    if (cd == null) {
      notFound();
    }
    return Templates.cd(cd);
  }
}
