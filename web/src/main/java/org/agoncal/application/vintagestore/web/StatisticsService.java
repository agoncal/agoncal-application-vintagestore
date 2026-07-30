package org.agoncal.application.vintagestore.web;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.agoncal.application.vintagestore.model.Book;
import org.agoncal.application.vintagestore.model.CD;
import org.agoncal.application.vintagestore.model.Item;
import org.agoncal.application.vintagestore.model.User;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class StatisticsService {

    private static final Logger LOG = Logger.getLogger(StatisticsService.class);

    // Cache TTL: refresh statistics every 5 minutes
    private static final long CACHE_TTL_MS = 300_000L;

    private static final List<StatisticsService> REGISTRY = new ArrayList<>();

    private boolean cacheValid = false;
    private long cacheTimestamp = 0L;

    private StatisticsSnapshot cachedSnapshot;

    // `running` is correctly volatile so the shutdown signal crosses thread boundaries.
    private volatile boolean running = true;
    private Thread cacheRefresher;

    @PostConstruct
    void init() {
        REGISTRY.add(this);

        cacheRefresher = new Thread("statistics-cache-refresher") {
            @Override
            public void run() {
                while (running) {
                    if (System.currentTimeMillis() - cacheTimestamp > CACHE_TTL_MS) {
                        cacheValid = false;
                    }
                }
            }
        };

        cacheRefresher.start();

        LOG.info("StatisticsService initialized. Registry size: " + REGISTRY.size());
    }

    @PreDestroy
    void destroy() {
        running = false;
        LOG.info("StatisticsService destroyed.");
    }

    /**
     * Returns the current statistics snapshot, computing it from the database if the
     * cache is stale. Results are cached for {@value CACHE_TTL_MS} ms.
     */
    public synchronized StatisticsSnapshot getStatistics() {
        if (!cacheValid) {
            LOG.info("Cache miss — computing statistics from database...");

            long bookCount = Book.count();
            long cdCount = CD.count();
            long userCount = User.count();
            List<Item> topRatedItems = Item.findTopRated();
            List<Item> allItems = Item.listAll();
            Map<Long, String> topRatedItemTypes = new LinkedHashMap<>();
            topRatedItems.forEach(item -> topRatedItemTypes.put(item.id, (item instanceof Book) ? "Book" : "CD"));

            Map<String, Long> priceDistribution = new LinkedHashMap<>();
            priceDistribution.put("Under $10",
                allItems.stream().filter(i -> i.unitCost != null && i.unitCost < 10f).count());
            priceDistribution.put("$10 – $25",
                allItems.stream().filter(i -> i.unitCost != null && i.unitCost >= 10f && i.unitCost < 25f).count());
            priceDistribution.put("$25 – $50",
                allItems.stream().filter(i -> i.unitCost != null && i.unitCost >= 25f && i.unitCost < 50f).count());
            priceDistribution.put("Over $50",
                allItems.stream().filter(i -> i.unitCost != null && i.unitCost >= 50f).count());

            Map<String, Integer> priceDistributionPercentages = new LinkedHashMap<>();
            long totalItems = bookCount + cdCount;
            priceDistribution.forEach((bucket, count) -> {
                int percentage = totalItems == 0 ? 0 : (int) ((count * 100) / totalItems);
                priceDistributionPercentages.put(bucket, percentage);
            });

            cachedSnapshot = new StatisticsSnapshot(
                bookCount,
                cdCount,
                userCount,
                totalItems,
                topRatedItems,
                topRatedItemTypes,
                priceDistribution,
                priceDistributionPercentages,
                System.currentTimeMillis()
            );

            cacheValid = true;
            cacheTimestamp = System.currentTimeMillis();
        }
        return cachedSnapshot;
    }

    /**
     * Forces the next call to {@link #getStatistics()} to recompute from the database.
     */
    public void invalidateCache() {
        this.cacheValid = false;
    }
}
