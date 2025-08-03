package org.agoncal.application.vintagestore.chat;

import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.time.LocalDate;

@QuarkusTest
class LegalDocumentToolsTest {

  @Inject
  LegalDocumentTools tool;

  @Test
  void shouldCheckLastUpdateAcceptableUsePolicy() {
    assertEquals(LocalDate.of(2021, 10, 1), tool.lastUpdateAcceptableUsePolicy());
  }

  @Test
  void shouldCheckLastUpdateDisclaimer() {
    assertEquals(LocalDate.of(2019, 5, 29), tool.lastUpdateDisclaimer());
  }

  @Test
  void shouldCheckLastUpdateEndOfUserLicenseAgreement() {
    assertEquals(LocalDate.of(2012, 3, 9), tool.lastUpdateEndOfUserLicenseAgreement());
  }

  @Test
  void shouldCheckLastUpdatePrivacy() {
    assertEquals(LocalDate.of(2013, 3, 9), tool.lastUpdatePrivacy());
  }

  @Test
  void shouldCheckLastUpdateTerms() {
    assertEquals(LocalDate.of(2014, 6, 19), tool.lastUpdateTerms());
  }
}
