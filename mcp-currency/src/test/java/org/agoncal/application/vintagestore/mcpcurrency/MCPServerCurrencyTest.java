package org.agoncal.application.vintagestore.mcpcurrency;

import io.quarkiverse.mcp.server.test.McpAssured;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class MCPServerCurrencyTest {

  @Test
  public void shouldConvertDollars() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("toEuros", Map.of("dollars", 42), r -> {
        assertEquals("Converted 42.0 dollars to euros: 35.699999999999996 euros", r.content().getFirst().asText().text());
      })
      .toolsCall("toJPY", Map.of("dollars", 42), r -> {
        assertEquals("Converted 42.0 dollars to Japanese Yen: 6300.0 JPY", r.content().getFirst().asText().text());
      })
      .toolsCall("toGBP", Map.of("dollars", 42), r -> {
        assertEquals("Converted 42.0 dollars to British Pound Sterling: 33.18 GBP", r.content().getFirst().asText().text());
      })
      .toolsCall("toCNY", Map.of("dollars", 42), r -> {
        assertEquals("Converted 42.0 dollars to Chinese Yuan/Renminbi: 304.5 CNY", r.content().getFirst().asText().text());
      })
      .toolsCall("toCHF", Map.of("dollars", 42), r -> {
        assertEquals("Converted 42.0 dollars to Swiss Franc: 36.96 CHF", r.content().getFirst().asText().text());
      })
      .thenAssertResults();
  }
}
