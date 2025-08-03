package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.agoncal.application.vintagestore.model.User;
import org.agoncal.application.vintagestore.web.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserLoggedInTools {

  private static final Logger LOG = LoggerFactory.getLogger(UserLoggedInTools.class);

  @Inject
  UserSession userSession;

  @Tool("Retrieve the logged-in user's information")
  User loggedInUserInformation() {
    LOG.info("Retrieving logged-in user information");

    if (!userSession.isLoggedIn()) {
      LOG.warn("No user is currently logged in");
      return null;
    }

    User user = userSession.getCurrentUser();
    LOG.info("Logged-in user: {}", user);
    return user;
  }
}
