package org.agoncal.application.vintagestore.model;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import static org.agoncal.application.vintagestore.model.Language.DEUTSCH;
import static org.agoncal.application.vintagestore.model.Language.ENGLISH;
import static org.agoncal.application.vintagestore.model.Language.FINISH;
import static org.agoncal.application.vintagestore.model.Language.FRENCH;
import static org.agoncal.application.vintagestore.model.Language.GERMAN;
import static org.agoncal.application.vintagestore.model.Language.ITALIAN;
import static org.agoncal.application.vintagestore.model.Language.PORTUGUESE;
import static org.agoncal.application.vintagestore.model.Language.RUSSIAN;
import static org.agoncal.application.vintagestore.model.Language.SPANISH;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */
@Converter
public class LanguageConverter implements AttributeConverter<Language, String> {

  // ======================================
  // =          Business methods          =
  // ======================================

  @Override
  public String convertToDatabaseColumn(Language language) {
    switch (language) {
      case DEUTSCH:
        return "DE";
      case ENGLISH:
        return "EN";
      case FINISH:
        return "FI";
      case FRENCH:
        return "FR";
      case GERMAN:
        return "GM";
      case ITALIAN:
        return "IT";
      case PORTUGUESE:
        return "PT";
      case RUSSIAN:
        return "RU";
      case SPANISH:
        return "ES";
      case CZECH:
        return "CZ";
      case JAPANESE:
        return "JP";
      default:
        throw new IllegalArgumentException("Unknown" + language);
    }
  }

  @Override
  public Language convertToEntityAttribute(String dbData) {
    switch (dbData) {
      case "DE":
        return DEUTSCH;
      case "EN":
        return ENGLISH;
      case "FI":
        return FINISH;
      case "FR":
        return FRENCH;
      case "GM":
        return GERMAN;
      case "IT":
        return ITALIAN;
      case "PT":
        return PORTUGUESE;
      case "RU":
        return RUSSIAN;
      case "ES":
        return SPANISH;
      case "CZ":
        return Language.CZECH;
      case "JP":
        return Language.JAPANESE;
      default:
        throw new IllegalArgumentException("Unknown" + dbData);
    }
  }
}
