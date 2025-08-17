package org.agoncal.application.vintagestore.mcpcurrency;

import io.quarkiverse.mcp.server.TextContent;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.Tool.Annotations;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;

public class MCPServerCurrency {

  private static double DOLLARS_TO_EUROS = 0.85; // European Euro
  private static double DOLLARS_TO_JPY = 150.0; // Japanese Yen
  private static double DOLLARS_TO_GBP = 0.79; // British Pound Sterling
  private static double DOLLARS_TO_CNY = 7.25; // Chinese Yuan/Renminbi
  private static double DOLLARS_TO_CHF = 0.88; // Swiss Franc

  @Tool(description = "Converts US Dollar (USD) amounts to European Euro (EUR) currency. Takes a numeric dollar amount as input and returns the equivalent value in euros using the current conversion rate of 0.85 EUR per 1 USD. Useful for international pricing calculations and currency conversion needs.",
    annotations = @Annotations(title = "converts_usd_to_eur", readOnlyHint = true, destructiveHint = false, idempotentHint = true))
  ToolResponse toEuros(@ToolArg(description = "Amount in dollars") double dollars) {
    return ToolResponse.success(
      new TextContent(
        "Converted " + dollars + " dollars to euros: " + dollars * DOLLARS_TO_EUROS + " euros"
      ));
  }

  @Tool(description = "Converts US Dollar (USD) amounts to Japanese Yen (JPY) currency. Takes a numeric dollar amount as input and returns the equivalent value in Japanese Yen using the current conversion rate of 150 JPY per 1 USD. Essential for pricing calculations in the Japanese market and international commerce.",
    annotations = @Annotations(title = "converts_usd_to_jpy", readOnlyHint = true, destructiveHint = false, idempotentHint = true))
  ToolResponse toJPY(@ToolArg(description = "Amount in dollars") double dollars) {
    return ToolResponse.success(
      new TextContent(
        "Converted " + dollars + " dollars to Japanese Yen: " + dollars * DOLLARS_TO_JPY + " JPY"
      ));
  }

  @Tool(description = "Converts US Dollar (USD) amounts to British Pound Sterling (GBP) currency. Takes a numeric dollar amount as input and returns the equivalent value in British Pounds using the current conversion rate of 0.79 GBP per 1 USD. Ideal for UK market pricing and British financial transactions.",
    annotations = @Annotations(title = "converts_usd_to_gbp", readOnlyHint = true, destructiveHint = false, idempotentHint = true))
  ToolResponse toGBP(@ToolArg(description = "Amount in dollars") double dollars) {
    return ToolResponse.success(
      new TextContent(
        "Converted " + dollars + " dollars to British Pound Sterling: " + dollars * DOLLARS_TO_GBP + " GBP"
      ));
  }

  @Tool(description = "Converts US Dollar (USD) amounts to Chinese Yuan/Renminbi (CNY) currency. Takes a numeric dollar amount as input and returns the equivalent value in Chinese Yuan using the current conversion rate of 7.25 CNY per 1 USD. Perfect for Chinese market calculations and mainland China business operations.",
    annotations = @Annotations(title = "converts_usd_to_cny", readOnlyHint = true, destructiveHint = false, idempotentHint = true))
  ToolResponse toCNY(@ToolArg(description = "Amount in dollars") double dollars) {
    return ToolResponse.success(
      new TextContent(
        "Converted " + dollars + " dollars to Chinese Yuan/Renminbi: " + dollars * DOLLARS_TO_CNY + " CNY"
      ));
  }

  @Tool(description = "Converts US Dollar (USD) amounts to Swiss Franc (CHF) currency. Takes a numeric dollar amount as input and returns the equivalent value in Swiss Francs using the current conversion rate of 0.88 CHF per 1 USD. Valuable for Swiss market pricing and financial calculations in Switzerland.",
    annotations = @Annotations(title = "converts_usd_to_chf", readOnlyHint = true, destructiveHint = false, idempotentHint = true))
  ToolResponse toCHF(@ToolArg(description = "Amount in dollars") double dollars) {
    return ToolResponse.success(
      new TextContent(
        "Converted " + dollars + " dollars to Swiss Franc: " + dollars * DOLLARS_TO_CHF + " CHF"
      ));
  }
}
