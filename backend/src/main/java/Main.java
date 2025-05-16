import Domain.PriceAlert;
import Domain.PriceHistoryEntry;
import Domain.Product;
import Domain.Discount;
import Services.Service;
import Loader.CsvDataLoader;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static Loader.CsvDataLoader.extractStoreShortName;
import static Utils.CsvPrintUtils.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        CsvDataLoader loader = new CsvDataLoader();

        List<Product> products = loader.loadAllProducts();
        System.out.println("=== PRODUSE ===");
        printProductsCsv(products);

        List<Discount> discounts = loader.loadAllDiscounts();
        System.out.println("\n=== REDUCERI ===");
        printDiscountsCsv(discounts);

        List<String> userBasket = List.of(
                "lapte zuzu",
                "pâine albă",
                "cafea macinata",
                "oua"
        );

        Service service = new Service();
        Map<String, Product> optimized = service.optimizeBasket(userBasket, products);

        System.out.println("\n=== COȘ OPTIMIZAT ===");
        optimized.forEach((productName, product) -> {
            if (product != null) {
                System.out.printf("- %s → %s (%.2f RON) din %s [%s]%n",
                        productName,
                        product.getProductName(),
                        product.getPrice(),
                        extractStoreShortName(product.getStoreName()),
                        product.getDate()
                );
            } else {
                System.out.printf("- %s → indisponibil%n", productName);
            }
        });

        System.out.println("\n=== REDUCERI ACTIVE ===");
        List<Discount> activeDiscounts = service.getTopActiveDiscounts(discounts, 10);
        printDiscountsCsv(activeDiscounts);

        System.out.println("\n=== REDUCERI NOI (ultimele 24h) ===");
        List<Discount> recentDiscounts = service.getNewDiscounts(discounts);
        printDiscountsCsv(recentDiscounts);

        System.out.println("\n=== ISTORIC PREȚURI - Brand: Zuzu ===");
        List<PriceHistoryEntry> history = service.getFilteredPriceHistory(products, "lidl", null, "oua");
        printPriceHistoryCsv(history);

        List<Product> bestValueProducts = service.getBestValueProducts(products);


        System.out.println("\n=== CELE MAI AVANTAJOASE PRODUSE (valoare per unitate) ===");
        bestValueProducts.forEach(p -> {
            double valuePerUnit = p.getPrice() / p.getPackageQuantity();
            System.out.printf("- %s (%s): %.2f RON per %s [%.2f RON / %.2f %s] din %s [%s]%n",
                    p.getProductName(), p.getBrand(),
                    valuePerUnit, p.getPackageUnit(),
                    p.getPrice(), p.getPackageQuantity(), p.getPackageUnit(),
                    p.getStoreName(), p.getDate()
            );
        });



        List<PriceAlert> alerts = loader.loadAllAlerts();
        List<PriceAlert> triggered = service.getTriggeredAlerts(alerts, products);

        System.out.println("\n=== ALERTE DE PREȚ DECLANȘATE ===");
        if (triggered.isEmpty()) {
            System.out.println("Nicio alertă activată.");
        } else {
            for (PriceAlert alert : triggered) {
                Product product = products.stream()
                        .filter(p -> p.getProductId().equalsIgnoreCase(alert.getProductId()))
                        .min(Comparator.comparing(Product::getPrice))
                        .orElse(null);

                if (product != null) {
                    System.out.printf("- %s: %.2f RON (sub %.2f RON) la %s [%s]%n",
                            product.getProductName(),
                            product.getPrice(),
                            alert.getTargetPrice(),
                            product.getStoreName(),
                            product.getDate()
                    );
                }
            }
        }
        loader.saveAlertsToCsv(alerts, "src/main/resources/data/priceAlerts/price_alerts.csv");
    }
}