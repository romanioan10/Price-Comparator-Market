import Domain.Discount;
import Domain.PriceAlert;
import Domain.PriceHistoryEntry;
import Domain.Product;
import Services.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PriceServiceTest {

    private PriceService priceService;
    private List<Product> sampleProducts;
    private List<Discount> sampleDiscounts;

    @BeforeEach
    void setUp() {
        priceService = new PriceService();

        sampleProducts = Arrays.asList(
                new Product("P001", "lapte", "lactate", "BrandA", 1, "l", 5.5, "RON", "kaufland", "2025-05-01"),
                new Product("P002", "lapte", "lactate", "BrandB", 1, "l", 4.9, "RON", "lidl", "2025-05-01"),
                new Product("P003", "paine", "panificatie", "BrandC", 1, "buc", 2.0, "RON", "profi", "2025-05-01")
        );

        sampleDiscounts = Arrays.asList(
                new Discount("P001", "lapte", "BrandA", 1, "l", "lactate", "kaufland", "2025-05-01", "2025-05-20", 10),
                new Discount("P002", "lapte", "BrandB", 1, "l", "lactate", "lidl", "2025-04-20", "2025-04-30", 15)
        );
    }

    @Test
    void testOptimizeBasket() {
        List<String> basket = Arrays.asList("lapte", "paine");
        Map<String, Product> result = priceService.optimizeBasket(basket, sampleProducts);
        assertEquals("P002", result.get("lapte").getProductId()); // cel mai ieftin lapte
        assertEquals("P003", result.get("paine").getProductId());
    }

    @Test
    void testGetTopActiveDiscounts() {
        List<Discount> top = priceService.getTopActiveDiscounts(sampleDiscounts, 5);
        assertEquals(1, top.size());
        assertEquals("P001", top.get(0).getProductId());
    }

    @Test
    void testGetFilteredPriceHistory() {
        List<PriceHistoryEntry> filtered = priceService.getFilteredPriceHistory(
                sampleProducts, "kaufland", null, null
        );
        assertEquals(1, filtered.size());
        assertEquals("P001", filtered.get(0).getProductId());
    }

    @Test
    void testGetBestValueProducts() {
        List<Product> best = priceService.getBestValueProducts(sampleProducts);
        assertEquals(2, best.size()); // lapte si paine
        assertTrue(best.stream().anyMatch(p -> p.getProductId().equals("P002"))); // lapte ieftin
    }

    @Test
    void testGetTriggeredAlerts() {
        List<PriceAlert> alerts = Arrays.asList(
                new PriceAlert("U1", "P002", 5.0, false),
                new PriceAlert("U1", "P001", 4.0, false)
        );

        List<PriceAlert> triggered = priceService.getTriggeredAlerts(alerts, sampleProducts);
        assertEquals(1, triggered.size());
        assertTrue(triggered.get(0).isTriggered());
        assertEquals("P002", triggered.get(0).getProductId());
    }
}
