package org.agoncal.application.vintagestore.controller;

import jakarta.ws.rs.Path;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkiverse.renarde.Controller;

public class Application extends Controller {

  @CheckedTemplate
  static class Templates {
    public static native TemplateInstance index();
  }

  @Path("/")
  public TemplateInstance index() {
    return Templates.index();
  }
}
