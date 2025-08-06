package org.agoncal.application.vintagestore.tool;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;

@ApplicationScoped
public class LegalDocumentTools {

  @Tool(name = "get_acceptable_use_policy_update_date", value = "Gets the last update date of the Acceptable Use Policy document. Use when customers ask about the currency or version of usage policies.")
  LocalDate lastUpdateAcceptableUsePolicy() {
    return LocalDate.of(2021, 10, 1);
  }

  @Tool(name = "get_disclaimer_update_date", value = "Gets the last update date of the legal Disclaimer document. Use when customers inquire about liability limitations or disclaimer information.")
  LocalDate lastUpdateDisclaimer() {
    return LocalDate.of(2019, 5, 29);
  }

  @Tool(name = "get_eula_update_date", value = "Gets the last update date of the End User License Agreement (EULA). Use when customers ask about software licensing terms or EULA versions.")
  LocalDate lastUpdateEndOfUserLicenseAgreement() {
    return LocalDate.of(2012, 3, 9);
  }

  @Tool(name = "get_privacy_policy_update_date", value = "Gets the last update date of the Privacy Policy document. Use when customers have questions about data handling, privacy practices, or GDPR compliance.")
  LocalDate lastUpdatePrivacy() {
    return LocalDate.of(2013, 3, 9);
  }

  @Tool(name = "get_terms_conditions_update_date", value = "Gets the last update date of the Terms and Conditions document. Use when customers ask about service terms, purchase conditions, or legal agreements.")
  LocalDate lastUpdateTerms() {
    return LocalDate.of(2014, 6, 19);
  }
}
