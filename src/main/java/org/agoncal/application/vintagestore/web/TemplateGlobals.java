package org.agoncal.application.vintagestore.web;

import io.quarkus.arc.Arc;
import io.quarkus.qute.TemplateGlobal;
import org.agoncal.application.vintagestore.model.User;

/**
 * Global template variables that are available in all Qute templates
 *
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */
public class TemplateGlobals {

  @TemplateGlobal
  public static User user() {
    UserSession userSession = Arc.container().instance(UserSession.class).get();
    return userSession.getCurrentUser();
  }

  @TemplateGlobal
  public static boolean isLoggedIn() {
    UserSession userSession = Arc.container().instance(UserSession.class).get();
    return userSession.isLoggedIn();
  }

  @TemplateGlobal
  public static boolean isAdmin() {
    UserSession userSession = Arc.container().instance(UserSession.class).get();
    return userSession.isAdmin();
  }
}
