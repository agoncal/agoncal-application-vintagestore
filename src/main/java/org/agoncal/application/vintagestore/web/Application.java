package org.agoncal.application.vintagestore.web;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.agoncal.application.vintagestore.model.Book;
import org.agoncal.application.vintagestore.model.CD;
import org.agoncal.application.vintagestore.model.User;
import org.agoncal.application.vintagestore.model.UserRole;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

public class Application extends Controller {

  @Inject
  UserSession userSession;

  @CheckedTemplate(requireTypeSafeExpressions = false)
  static class Templates {
    public static native TemplateInstance index();

    public static native TemplateInstance chat();

    public static native TemplateInstance books(List<Book> books);

    public static native TemplateInstance cds(List<CD> cds);

    public static native TemplateInstance book(Book book);

    public static native TemplateInstance cd(CD cd);

    public static native TemplateInstance terms(String selectedDoc);

    public static native TemplateInstance users(List<User> users, long userCount, long adminCount);

    public static native TemplateInstance signin(String loginError, String passwordError, String login);

    public static native TemplateInstance profile(User profileUser);
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

  @Path("/view/terms")
  public TemplateInstance terms(@QueryParam("doc") String doc) {
    // Default to acceptable-use-policy if no doc parameter provided
    String selectedDoc = (doc != null && !doc.isEmpty()) ? doc : "acceptable-use-policy";
    return Templates.terms(selectedDoc);
  }

  @Path("/view/users")
  public TemplateInstance users() {
    List<User> users = User.listAll();
    long userCount = users.stream().filter(u -> u.role == UserRole.USER).count();
    long adminCount = users.stream().filter(u -> u.role == UserRole.ADMIN).count();
    return Templates.users(users, userCount, adminCount);
  }

  @GET
  @Path("/signin")
  public TemplateInstance signinPage() {
    return Templates.signin(null, null, null);
  }

  @POST
  @Path("/signin")
  public TemplateInstance signin(@RestForm String login, @RestForm String password) {
    String loginError = null;
    String passwordError = null;

    // Validate inputs
    if (login == null || login.trim().isEmpty()) {
      loginError = "Username is required";
    }
    if (password == null || password.trim().isEmpty()) {
      passwordError = "Password is required";
    }

    if (loginError != null || passwordError != null) {
      return Templates.signin(loginError, passwordError, login);
    }

    // Find user by login
    User user = User.find("login", login.trim()).firstResult();
    if (user == null) {
      loginError = "User not found";
      return Templates.signin(loginError, passwordError, login);
    }

    if (!user.password.equals(password)) {
      passwordError = "Invalid password";
      return Templates.signin(loginError, passwordError, login);
    }

    // Store user in session using UserSession
    userSession.setCurrentUser(user);

    // Redirect to home page after successful login
    return index();
  }

  @Path("/logout")
  public TemplateInstance logout() {
    userSession.logout();
    return index();
  }

  @Path("/profile")
  public TemplateInstance profile() {
    if (!userSession.isLoggedIn()) {
      return signinPage();
    }
    return Templates.profile(userSession.getCurrentUser());
  }
}
