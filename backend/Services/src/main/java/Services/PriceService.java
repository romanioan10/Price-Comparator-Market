package Services;

import Domain.Discount;
import Domain.PriceAlert;
import Domain.PriceHistoryEntry;
import Domain.Product;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

import static Loader.CsvDataLoader.extractStoreShortName;
import static Utils.StringUtils.normalize;


@Service
public class PriceService
{

    /**
     * Optimize the basket by finding the cheapest product for each item in the basket.
     *
     * @param basket       List of product names in the basket.
     * @param allProducts  List of all available products.
     * @return A map where the key is the product name and the value is the cheapest Product object.
     */
    public Map<String, Product> optimizeBasket(List<String> basket, List<Product> allProducts) {
        Map<String, Product> result = new LinkedHashMap<>();

        for (String itemName : basket) {
            String normItem = normalize(itemName); // Normalize input name for case/diacritic-insensitive comparison

            List<Product> matches = allProducts.stream()
                    .filter(p -> normalize(p.getProductName()).contains(normItem)) // Match product names containing item
                    .collect(Collectors.toList());

            if (!matches.isEmpty()) {
                Product cheapest = matches.stream()
                        .min(Comparator.comparingDouble(Product::getPrice)) // Find cheapest match
                        .orElse(null);

                result.put(itemName, cheapest);
            } else {
                result.put(itemName, null); // If no match found, mark as null
            }
        }

        return result;
    }


    /**
     * Get the top active discounts from a list of all discounts.
     *
     * @param allDiscounts List of all available discounts.
     * @param limit        The maximum number of discounts to return.
     * @return A list of the top active discounts.
     */
    public List<Discount> getTopActiveDiscounts(List<Discount> allDiscounts, int limit) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return allDiscounts.stream()
                .filter(d -> {
                    try {
                        String rawFrom = d.getFromDate().trim();
                        String rawTo = d.getToDate().trim();

                        LocalDate from = LocalDate.parse(rawFrom, formatter);
                        LocalDate to = LocalDate.parse(rawTo, formatter);

                        return !today.isBefore(from) && !today.isAfter(to); // Check if discount is active
                    } catch (Exception e) {
                        System.err.println("Eroare la parsare date pentru discount " + d.getProductId());
                        return false;
                    }
                })
                .sorted(Comparator.comparingInt(Discount::getPercentageOfDiscount).reversed()) // Sort by discount % descending
                .limit(limit) // Return only top X
                .collect(Collectors.toList());
    }

    /**
     * Get the new discounts that are valid from yesterday to today.
     *
     * @param allDiscounts List of all available discounts.
     * @return A list of new discounts.
     */
    public List<Discount> getNewDiscounts(List<Discount> allDiscounts) {
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return allDiscounts.stream()
                .filter(d -> {
                    LocalDate from = LocalDate.parse(d.getFromDate(), formatter);
                    return !from.isBefore(yesterday); // Filter discounts that start today or yesterday
                })
                .sorted(Comparator.comparing(Discount::getFromDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get the filtered price history based on store, brand, and category.
     *
     * @param allProducts   List of all available products.
     * @param storeFilter   Filter for store name (can be null).
     * @param brandFilter   Filter for brand name (can be null).
     * @param categoryFilter Filter for product category (can be null).
     * @return A list of filtered PriceHistoryEntry objects.
     */
    public List<PriceHistoryEntry> getFilteredPriceHistory(List<Product> allProducts,
                                                           String storeFilter,
                                                           String brandFilter,
                                                           String categoryFilter) {
        return allProducts.stream()
                .filter(p -> storeFilter == null ||
                        normalize(p.getStoreName()).contains(normalize(extractStoreShortName(storeFilter)))) // Filter by store
                .filter(p -> brandFilter == null ||
                        normalize(p.getBrand()).contains(normalize(brandFilter))) // Filter by brand
                .filter(p -> categoryFilter == null ||
                        normalize(p.getProductCategory()).contains(normalize(categoryFilter))) // Filter by category
                .map(p -> new PriceHistoryEntry(
                        p.getProductId(),
                        p.getProductName(),
                        p.getStoreName(),
                        p.getDate(),
                        p.getPrice()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get the best value products based on price per unit.
     *
     * @param products List of all available products.
     * @return A list of the best value products.
     */
    public List<Product> getBestValueProducts(List<Product> products) {
        return products.stream()
                .filter(p -> p.getPrice() > 0 && p.getPackageQuantity() > 0) // Exclude invalid products
                .collect(Collectors.groupingBy(
                        p -> normalize(p.getProductName()), // Group by product name
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparingDouble(p -> p.getPrice() / p.getPackageQuantity())),
                                Optional::get
                        )
                ))
                .values().stream()
                .sorted(Comparator.comparing(p -> normalize(p.getProductName())))
                .collect(Collectors.toList());
    }

    /**
     * Get the triggered price alerts based on the current product prices.
     *
     * @param alerts   List of price alerts.
     * @param products List of all available products.
     * @return A list of triggered price alerts.
     */
    public List<PriceAlert> getTriggeredAlerts(List<PriceAlert> alerts, List<Product> products) {
        List<PriceAlert> triggered = new ArrayList<>();

        for (PriceAlert alert : alerts) {
            String targetId = alert.getProductId();
            double targetPrice = alert.getTargetPrice();

            products.stream()
                    .filter(p -> p.getProductId().equalsIgnoreCase(targetId)) // Match by product ID
                    .filter(p -> p.getPrice() <= targetPrice) // Check price threshold
                    .findFirst()
                    .ifPresent(p -> {
                        alert.setTriggered(true);
                        triggered.add(alert);
                    });
        }

        return triggered;
    }
}
