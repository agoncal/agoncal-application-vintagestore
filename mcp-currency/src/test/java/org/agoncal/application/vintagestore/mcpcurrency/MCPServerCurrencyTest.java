package org.agoncal.application.vintagestore.mcpcurrency;

import io.quarkiverse.mcp.server.test.McpAssured;
import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.Map;

@QuarkusTest
public class MCPServerCurrencyTest {

  @Test
  public void testAllEuroConversions() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("toEurosFromDollars", Map.of("dollars", 100), r -> {
        assertEquals("Converted 100.0 dollars to euros: 85.0 EUR", r.content().getFirst().asText().text());
      })
      .toolsCall("toEurosFromGbp", Map.of("gbp", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 GBP to euros:") && r.content().getFirst().asText().text().contains("107.594") && r.content().getFirst().asText().text().contains("EUR"));
      })
      .toolsCall("toEurosFromJpy", Map.of("jpy", 10000), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 10000.0 JPY to euros:") && r.content().getFirst().asText().text().contains("56.6666") && r.content().getFirst().asText().text().contains("EUR"));
      })
      .toolsCall("toEurosFromCny", Map.of("cny", 500), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 500.0 CNY to euros:") && r.content().getFirst().asText().text().contains("58.620") && r.content().getFirst().asText().text().contains("EUR"));
      })
      .toolsCall("toEurosFromChf", Map.of("chf", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 CHF to euros:") && r.content().getFirst().asText().text().contains("96.590") && r.content().getFirst().asText().text().contains("EUR"));
      })
      .thenAssertResults();
  }

  @Test
  public void testAllGbpConversions() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("toGbpFromDollars", Map.of("dollars", 100), r -> {
        assertEquals("Converted 100.0 dollars to GBP: 79.0 GBP", r.content().getFirst().asText().text());
      })
      .toolsCall("toGbpFromEuros", Map.of("euros", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 EUR to GBP:") && r.content().getFirst().asText().text().contains("GBP"));
      })
      .toolsCall("toGbpFromJpy", Map.of("jpy", 10000), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 10000.0 JPY to GBP:") && r.content().getFirst().asText().text().contains("GBP"));
      })
      .toolsCall("toGbpFromCny", Map.of("cny", 500), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 500.0 CNY to GBP:") && r.content().getFirst().asText().text().contains("GBP"));
      })
      .toolsCall("toGbpFromChf", Map.of("chf", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 CHF to GBP:") && r.content().getFirst().asText().text().contains("GBP"));
      })
      .thenAssertResults();
  }

  @Test
  public void testAllJpyConversions() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("toJpyFromDollars", Map.of("dollars", 100), r -> {
        assertEquals("Converted 100.0 dollars to JPY: 15000.0 JPY", r.content().getFirst().asText().text());
      })
      .toolsCall("toJpyFromEuros", Map.of("euros", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 EUR to JPY:") && r.content().getFirst().asText().text().contains("17647.05") && r.content().getFirst().asText().text().contains("JPY"));
      })
      .toolsCall("toJpyFromGbp", Map.of("gbp", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 GBP to JPY:") && r.content().getFirst().asText().text().contains("18987.") && r.content().getFirst().asText().text().contains("JPY"));
      })
      .toolsCall("toJpyFromCny", Map.of("cny", 500), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 500.0 CNY to JPY:") && r.content().getFirst().asText().text().contains("10344.8") && r.content().getFirst().asText().text().contains("JPY"));
      })
      .toolsCall("toJpyFromChf", Map.of("chf", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 CHF to JPY:") && r.content().getFirst().asText().text().contains("17045.") && r.content().getFirst().asText().text().contains("JPY"));
      })
      .thenAssertResults();
  }

  @Test
  public void testAllCnyConversions() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("toCnyFromDollars", Map.of("dollars", 100), r -> {
        assertEquals("Converted 100.0 dollars to CNY: 725.0 CNY", r.content().getFirst().asText().text());
      })
      .toolsCall("toCnyFromEuros", Map.of("euros", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 EUR to CNY:") && r.content().getFirst().asText().text().contains("CNY"));
      })
      .toolsCall("toCnyFromGbp", Map.of("gbp", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 GBP to CNY:") && r.content().getFirst().asText().text().contains("CNY"));
      })
      .toolsCall("toCnyFromJpy", Map.of("jpy", 10000), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 10000.0 JPY to CNY:") && r.content().getFirst().asText().text().contains("CNY"));
      })
      .toolsCall("toCnyFromChf", Map.of("chf", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 CHF to CNY:") && r.content().getFirst().asText().text().contains("CNY"));
      })
      .thenAssertResults();
  }

  @Test
  public void testAllChfConversions() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("toChfFromDollars", Map.of("dollars", 100), r -> {
        assertEquals("Converted 100.0 dollars to CHF: 88.0 CHF", r.content().getFirst().asText().text());
      })
      .toolsCall("toChfFromEuros", Map.of("euros", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 EUR to CHF:") && r.content().getFirst().asText().text().contains("CHF"));
      })
      .toolsCall("toChfFromGbp", Map.of("gbp", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 GBP to CHF:") && r.content().getFirst().asText().text().contains("CHF"));
      })
      .toolsCall("toChfFromJpy", Map.of("jpy", 10000), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 10000.0 JPY to CHF:") && r.content().getFirst().asText().text().contains("CHF"));
      })
      .toolsCall("toChfFromCny", Map.of("cny", 500), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 500.0 CNY to CHF:") && r.content().getFirst().asText().text().contains("CHF"));
      })
      .thenAssertResults();
  }

  @Test
  public void testAllDollarConversions() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("toDollarsFromEuros", Map.of("euros", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 EUR to dollars:") && r.content().getFirst().asText().text().contains("117.647") && r.content().getFirst().asText().text().contains("USD"));
      })
      .toolsCall("toDollarsFromGbp", Map.of("gbp", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 GBP to dollars:") && r.content().getFirst().asText().text().contains("126.582") && r.content().getFirst().asText().text().contains("USD"));
      })
      .toolsCall("toDollarsFromJpy", Map.of("jpy", 10000), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 10000.0 JPY to dollars:") && r.content().getFirst().asText().text().contains("66.6666") && r.content().getFirst().asText().text().contains("USD"));
      })
      .toolsCall("toDollarsFromCny", Map.of("cny", 500), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 500.0 CNY to dollars:") && r.content().getFirst().asText().text().contains("68.965") && r.content().getFirst().asText().text().contains("USD"));
      })
      .toolsCall("toDollarsFromChf", Map.of("chf", 100), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Converted 100.0 CHF to dollars:") && r.content().getFirst().asText().text().contains("113.636") && r.content().getFirst().asText().text().contains("USD"));
      })
      .thenAssertResults();
  }

  @Test
  public void testAdvancedTools() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("getHistoricalRates", Map.of("currencyPair", "USD/EUR", "days", 30), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Historical exchange rate data"));
        assertTrue(r.content().getFirst().asText().text().contains("USD/EUR"));
      })
      .toolsCall("calculateTransferFees", Map.of("amount", 1000.0, "sourceCurrency", "USD", "targetCurrency", "EUR"), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Transfer fee breakdown"));
        assertTrue(r.content().getFirst().asText().text().contains("1000.0"));
      })
      .toolsCall("analyzeVolatility", Map.of("currencyPair", "EUR/USD"), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Volatility analysis"));
        assertTrue(r.content().getFirst().asText().text().contains("EUR/USD"));
      })
      .toolsCall("optimizePaymentRouting", Map.of("amount", 5000.0, "sourceCountry", "US", "destinationCountry", "UK"), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Optimized payment routing"));
        assertTrue(r.content().getFirst().asText().text().contains("5000.0"));
      })
      .thenAssertResults();
  }

  @Test
  public void testBudgetAndCompliance() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("forecastMulticurrencyBudget", Map.of("budgetAmount", 10000.0, "currencies", "EUR,GBP,JPY", "months", 12), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Multi-currency budget forecast"));
        assertTrue(r.content().getFirst().asText().text().contains("10000.0"));
      })
      .toolsCall("validateBankAccountDetails", Map.of("accountNumber", "DE89370400440532013000", "swiftCode", "COBADEFFXXX", "countryCode", "DE"), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Bank account validation results"));
        assertTrue(r.content().getFirst().asText().text().contains("VALID"));
      })
      .toolsCall("generateComplianceReport", Map.of("transactionId", "TXN123456", "jurisdiction", "US", "reportType", "AML"), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Compliance report generated"));
        assertTrue(r.content().getFirst().asText().text().contains("PASSED"));
      })
      .thenAssertResults();
  }

  @Test
  public void testCryptoAndAnalysis() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("retrieveCryptoRates", Map.of("cryptoSymbol", "BTC", "fiatCurrency", "USD"), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Cryptocurrency exchange rate"));
        assertTrue(r.content().getFirst().asText().text().contains("BTC"));
      })
      .toolsCall("performCorrelationAnalysis", Map.of("pair1", "EUR/USD", "pair2", "GBP/USD", "days", 90), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Currency correlation analysis"));
        assertTrue(r.content().getFirst().asText().text().contains("Pearson correlation"));
      })
      .toolsCall("monitorMarketLiquidity", Map.of("currencyPair", "EUR/USD", "tradeSize", 1000000.0), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Market liquidity analysis"));
        assertTrue(r.content().getFirst().asText().text().contains("EUR/USD"));
      })
      .thenAssertResults();
  }

  @Test
  public void testDerivativesAndRiskAnalysis() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("executeForwardCurveAnalysis", Map.of("currencyPair", "USD/EUR", "maturityMonths", 6), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Forward curve analysis"));
        assertTrue(r.content().getFirst().asText().text().contains("6-month"));
      })
      .toolsCall("performPPPAnalysis", Map.of("baseCurrency", "USD", "quoteCurrency", "EUR"), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Purchasing Power Parity analysis"));
        assertTrue(r.content().getFirst().asText().text().contains("USD/EUR"));
      })
      .toolsCall("assessCurrencyRiskExposure", Map.of("baseCurrency", "USD", "exposureAmount", 100000.0, "timeHorizon", 12), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Currency risk exposure assessment"));
        assertTrue(r.content().getFirst().asText().text().contains("100000.0"));
      })
      .thenAssertResults();
  }

  @Test
  public void testFxUtilityTools() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("calculateFxSpread", Map.of("pair", "EUR/USD", "bid", 1.0800, "ask", 1.0850), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("FX spread for EUR/USD"));
        assertTrue(r.content().getFirst().asText().text().contains("1.08") && r.content().getFirst().asText().text().contains("1.085"));
      })
      .toolsCall("calculateCrossRate", Map.of("baseCurrency", "EUR", "quoteCurrency", "GBP", "basePerUsd", 0.85, "quotePerUsd", 0.79), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Cross rate for EUR/GBP"));
      })
      .toolsCall("estimateForwardFxRate", Map.of("spotRate", 1.0850, "domesticRate", 0.05, "foreignRate", 0.03, "months", 12), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Estimated 12-month forward rate"));
      })
      .thenAssertResults();
  }

  @Test
  public void testArbitrageAndPortfolio() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("detectTriangularArbitrage", Map.of("rateAB", 1.2, "rateBC", 0.9, "rateCA", 0.92), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Triangular arbitrage check"));
      })
      .toolsCall("calculateFxPipValue", Map.of("currencyPair", "EUR/USD", "lotSize", 100000.0, "pipSize", 0.0001, "quoteToUsd", 1.0), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Pip value for EUR/USD"));
      })
      .toolsCall("calculateHedgedExposure", Map.of("exposureAmount", 500000.0, "hedgeRatioPercent", 75.0), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Exposure split"));
        assertTrue(r.content().getFirst().asText().text().contains("375000.0") && r.content().getFirst().asText().text().contains("125000.0"));
      })
      .thenAssertResults();
  }

  @Test
  public void testPortfolioRebalancingAndInvoicing() {
    McpAssured.McpStreamableTestClient client = McpAssured.newConnectedStreamableClient();

    client.when()
      .toolsCall("rebalanceCurrencyPortfolio", Map.of("totalUsd", 1000000.0, "eurPct", 25.0, "gbpPct", 25.0, "jpyPct", 25.0, "chfPct", 25.0), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Portfolio rebalance result"));
        assertTrue(r.content().getFirst().asText().text().contains("250000.0"));
      })
      .toolsCall("calculateCurrencyInvoiceTotal", Map.of("invoiceAmount", 10000.0, "exchangeRate", 1.0850, "feePercent", 2.5), r -> {
        assertTrue(r.content().getFirst().asText().text().contains("Currency invoice total"));
        assertTrue(r.content().getFirst().asText().text().contains("Gross"));
      })
      .thenAssertResults();
  }
}
