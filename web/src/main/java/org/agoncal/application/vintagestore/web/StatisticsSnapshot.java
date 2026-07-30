package org.agoncal.application.vintagestore.web;

import org.agoncal.application.vintagestore.model.Item;

import java.util.List;
import java.util.Map;

public record StatisticsSnapshot(
    long bookCount,
    long cdCount,
    long userCount,
    long totalItems,
    List<Item> topRatedItems,
    Map<Long, String> topRatedItemTypes,
    Map<String, Long> priceDistribution,
    Map<String, Integer> priceDistributionPercentages,
    long computedAtMs
) {}
