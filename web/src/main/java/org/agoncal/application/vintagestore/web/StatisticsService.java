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

    // BUG 1 (memory leak): static registry is never pruned.
    // In Quarkus dev mode, hot-reloads create new ApplicationScoped instances; each adds
    // itself here but @PreDestroy never removes it, so old instances accumulate indefinitely.
    private static final List<StatisticsService> REGISTRY = new ArrayList<>();

    // BUG 2 (stale cache): neither field is declared volatile.
    // Writes from the background refresher thread are not guaranteed to be visible
    // to HTTP request threads — the JVM memory model allows each CPU core to hold
    // its own cached copy of these values.
    private boolean cacheValid = false;
    private long cacheTimestamp = 0L;

    private StatisticsSnapshot cachedSnapshot;

    // `running` is correctly volatile so the shutdown signal crosses thread boundaries.
    private volatile boolean running = true;
    private Thread cacheRefresher;

    @PostConstruct
    void init() {
        REGISTRY.add(this); // BUG 1: never removed in @PreDestroy

        cacheRefresher = new Thread("statistics-cache-refresher") {
            @Override
            public void run() {
                // BUG 3 (busy-wait / CPU burn): the loop has no Thread.sleep().
                // When the cache is warm and within TTL, this thread spins at ~100% CPU
                // doing nothing useful — it just checks the timestamp millions of times
                // per second until cacheValid is false again.
                while (running) {
                    if (System.currentTimeMillis() - cacheTimestamp > CACHE_TTL_MS) {
                        cacheValid = false; // BUG 2: non-volatile write
                    }
                }
            }
        };

        // BUG 4 (blocks JVM shutdown): the thread is not marked as a daemon.
        // When the application tries to stop, the JVM waits for all non-daemon threads to
        // finish before exiting. This thread only finishes when `running` becomes false,
        // but @PreDestroy does not call cacheRefresher.interrupt(), so if the thread is
        // inside the tight loop when shutdown begins, shutdown may hang.
        cacheRefresher.start();

        LOG.info("StatisticsService initialized. Registry size: " + REGISTRY.size());
    }

    @PreDestroy
    void destroy() {
        running = false;
        // BUG 1: REGISTRY.remove(this) is missing — old instance leaks
        // BUG 4: no cacheRefresher.interrupt() — thread spins until it naturally checks `running`
        LOG.info("StatisticsService destroyed.");
    }

    /**
     * Returns the current statistics snapshot, computing it from the database if the
     * cache is stale. Results are cached for {@value CACHE_TTL_MS} ms.
     *
     * BUG 5 (latency / lock contention): the method is synchronized to prevent multiple
     * threads from recomputing at the same time — reasonable intent, but the lock is held
     * for the entire duration of multiple sequential database queries. Under moderate
     * concurrency every page load that hits a stale cache queues up here, and even
     * cache-hit calls block while another thread is inside the DB queries.
     */
    public synchronized StatisticsSnapshot getStatistics() {
        if (!cacheValid) { // BUG 2: may read a stale `true` even after the refresher invalidated it
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

            cacheValid = true;        // BUG 2: non-volatile write
            cacheTimestamp = System.currentTimeMillis(); // BUG 2: non-volatile write
        }
        return cachedSnapshot;
    }

    /**
     * Forces the next call to {@link #getStatistics()} to recompute from the database.
     */
    public void invalidateCache() {
        this.cacheValid = false; // BUG 2: non-volatile write; background thread may not see it
    }
}
