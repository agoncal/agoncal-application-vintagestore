package org.agoncal.application.vintagestore.web;

import jakarta.enterprise.context.ApplicationScoped;
import org.agoncal.application.vintagestore.model.User;

/**
 * Session-scoped bean to manage user authentication state
 *
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */
@ApplicationScoped
public class UserSession {

  private User currentUser;

  public User getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }

  public boolean isLoggedIn() {
    return currentUser != null;
  }

  public boolean isAdmin() {
    return currentUser != null && currentUser.role.ordinal() == 1;
  }

  public void logout() {
    this.currentUser = null;
  }
}
