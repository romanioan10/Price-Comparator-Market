package controller;

import Domain.Discount;
import Domain.PriceAlert;
import Domain.PriceHistoryEntry;
import Domain.Product;
import Loader.CsvDataLoader;
import Services.PriceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final PriceService priceService;
    private final CsvDataLoader loader;

    public ProductController(PriceService priceService, CsvDataLoader loader) {
        this.priceService = priceService;
        this.loader = loader;
    }

    @GetMapping("/ping")
    public String ping() {
        return "REST OK";
    }

    @GetMapping("/all")
    public List<Product> getAllProducts()
    {
        return loader.loadAllProducts();
    }

    @GetMapping("/optimize")
    public Map<String, Product> optimizeBasket(@RequestParam List<String> items) {
        List<Product> all = loader.loadAllProducts();
        return priceService.optimizeBasket(items, all);
    }

    @GetMapping("/top-discounts")
    public List<Discount> getTopDiscounts(@RequestParam(defaultValue = "10") int limit) {
        List<Discount> all = loader.loadAllDiscounts();
        System.out.println("== Reduceri încărcate ==" + all.size());
        return priceService.getTopActiveDiscounts(all, limit);
    }



    @GetMapping("/new-discounts")
    public List<Discount> getNewDiscounts() {
        List<Discount> all = loader.loadAllDiscounts();
        return priceService.getNewDiscounts(all);
    }

    @GetMapping("/price-history")
    public List<PriceHistoryEntry> getPriceHistory(
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category
    ) {
        List<Product> all = loader.loadAllProducts();
        return priceService.getFilteredPriceHistory(all, store, brand, category);
    }

    @GetMapping("/best-value")
    public List<Product> getBestValueProducts() {
        List<Product> all = loader.loadAllProducts();
        return priceService.getBestValueProducts(all);
    }

    @GetMapping("/triggered-alerts")
    public List<PriceAlert> getTriggeredAlerts() {
        List<PriceAlert> alerts = loader.loadAllAlerts();
        List<Product> all = loader.loadAllProducts();
        return priceService.getTriggeredAlerts(alerts, all);
    }

}
