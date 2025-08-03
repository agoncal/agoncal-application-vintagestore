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
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

public class Application extends Controller {

  private static final Logger LOG = Logger.getLogger(Application.class);

  @Inject
  UserSession userSession;

  @CheckedTemplate(requireTypeSafeExpressions = false)
  static class Templates {
    public static native TemplateInstance index();

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
    LOG.info("Entering index()");
    return Templates.index();
  }

  @Path("/view/books")
  public TemplateInstance books() {
    LOG.info("Entering books()");
    return Templates.books(Book.listAll());
  }

  @Path("/view/cds")
  public TemplateInstance cds() {
    LOG.info("Entering cds()");
    return Templates.cds(CD.listAll());
  }

  @Path("/view/book/{id}")
  public TemplateInstance book(@PathParam("id") Long id) {
    LOG.info("Entering book() with id: " + id);
    Book book = Book.findById(id);
    if (book == null) {
      LOG.warn("Book not found with ID: " + id);
      notFound();
    }
    return Templates.book(book);
  }

  @Path("/view/cd/{id}")
  public TemplateInstance cd(@PathParam("id") Long id) {
    LOG.info("Entering cd() with id: " + id);
    CD cd = CD.findById(id);
    if (cd == null) {
      LOG.warn("CD not found with ID: " + id);
      notFound();
    }
    return Templates.cd(cd);
  }

  @Path("/view/terms")
  public TemplateInstance terms(@QueryParam("doc") String doc) {
    LOG.info("Entering terms() with doc: " + doc);
    // Default to acceptable-use-policy if no doc parameter provided
    String selectedDoc = (doc != null && !doc.isEmpty()) ? doc : "acceptable-use-policy";
    return Templates.terms(selectedDoc);
  }

  @Path("/view/users")
  public TemplateInstance users() {
    LOG.info("Entering users()");
    List<User> users = User.listAll();
    long userCount = users.stream().filter(u -> u.role == UserRole.USER).count();
    long adminCount = users.stream().filter(u -> u.role == UserRole.ADMIN).count();
    return Templates.users(users, userCount, adminCount);
  }

  @GET
  @Path("/signin")
  public TemplateInstance signinPage() {
    LOG.info("Entering signinPage()");
    return Templates.signin(null, null, null);
  }

  @POST
  @Path("/signin")
  public TemplateInstance signin(@RestForm String login, @RestForm String password) {
    LOG.info("Entering signin() for user: " + (login != null ? login.trim() : "null"));
    String loginError = null;
    String passwordError = null;

    // Validate inputs
    if (login == null || login.trim().isEmpty()) {
      loginError = "Username is required";
      LOG.warn("Signin failed: Username is required");
    }
    if (password == null || password.trim().isEmpty()) {
      passwordError = "Password is required";
      LOG.warn("Signin failed: Password is required");
    }

    if (loginError != null || passwordError != null) {
      return Templates.signin(loginError, passwordError, login);
    }

    // Find user by login
    User user = User.find("login", login.trim()).firstResult();
    if (user == null) {
      loginError = "User not found";
      LOG.warn("Signin failed: User not found - " + login.trim());
      return Templates.signin(loginError, passwordError, login);
    }

    if (!user.password.equals(password)) {
      passwordError = "Invalid password";
      LOG.warn("Signin failed: Invalid password for user - " + login.trim());
      return Templates.signin(loginError, passwordError, login);
    }

    // Store user in session using UserSession
    userSession.setCurrentUser(user);
    LOG.info("Successful signin for user: " + user.login + " (" + user.role + ")");

    // Redirect to home page after successful login
    return index();
  }

  @Path("/logout")
  public TemplateInstance logout() {
    LOG.info("Entering logout()");
    User currentUser = userSession.getCurrentUser();
    if (currentUser != null) {
      LOG.info("User logout: " + currentUser.login);
    }
    userSession.logout();
    return index();
  }

  @Path("/profile")
  public TemplateInstance profile() {
    LOG.info("Entering profile()");
    if (!userSession.isLoggedIn()) {
      LOG.info("Profile access attempt without authentication - redirecting to signin");
      return signinPage();
    }
    User currentUser = userSession.getCurrentUser();
    LOG.info("Viewing profile for user: " + currentUser.login);
    return Templates.profile(currentUser);
  }
}
