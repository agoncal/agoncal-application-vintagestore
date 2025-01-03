package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;

@ApplicationScoped
public class LegalDocumentTools {

  @Tool("Returns the last time the ACCEPTABLE USE POLICY document was updated")
  LocalDate lastUpdateAcceptableUsePolicy() {
    return LocalDate.of(2021, 10, 1);
  }

  @Tool("Returns the last time the DISCLAIMER document was updated")
  LocalDate lastUpdateDisclaimer() {
    return LocalDate.of(2019, 5, 29);
  }

  @Tool("Returns the last time the END USER LICENSE AGREEMENT document was updated")
  LocalDate lastUpdateEndOfUserLicenseAgreement() {
    return LocalDate.of(2012, 3, 9);
  }

  @Tool("Returns the last time the PRIVACY document was updated")
  LocalDate lastUpdatePrivacy() {
    return LocalDate.of(2013, 3, 9);
  }

  @Tool("Returns the last time the TERMS AND CONDITIONS document was updated")
  LocalDate lastUpdateTerms() {
    return LocalDate.of(2014, 6, 19);
  }
}
