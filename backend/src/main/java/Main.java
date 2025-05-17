import Domain.PriceAlert;
import Domain.PriceHistoryEntry;
import Domain.Product;
import Domain.Discount;
import Services.PriceService;
import Loader.CsvDataLoader;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static Loader.CsvDataLoader.extractStoreShortName;
import static Utils.CsvPrintUtils.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        CsvDataLoader loader = new CsvDataLoader();
        PriceService priceService = new PriceService();

        List<Product> products = loader.loadAllProducts();
        List<Discount> discounts = loader.loadAllDiscounts();
        List<PriceAlert> alerts = loader.loadAllAlerts();

        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Afișează toate produsele");
            System.out.println("2. Optimizează coșul");
            System.out.println("3. Afișează reducerile active");
            System.out.println("4. Afișează reducerile noi (ultimele 24h)");
            System.out.println("5. Istoric prețuri (filtrabil)");
            System.out.println("6. Cele mai avantajoase produse (valoare per unitate)");
            System.out.println("7. Alerte de preț declanșate");
            System.out.println("8. Afiseaza reducerile active (filtrabil)");
            System.out.println("0. Ieșire");
            System.out.print("Alege o opțiune: ");

            String opt = scanner.nextLine().trim();

            if (opt.equals("1")) {
                System.out.println("\n=== PRODUSE ===");
                printProductsCsv(products);
            } else if (opt.equals("2")) {
                System.out.println("Introduceți produsele (separate prin virgulă):");
                String input = scanner.nextLine().trim();
                List<String> basket = Arrays.stream(input.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

                Map<String, Product> optimized = priceService.optimizeBasket(basket, products);
                System.out.println("\n=== COȘ OPTIMIZAT ===");
                optimized.forEach((name, p) -> {
                    if (p != null) {
                        System.out.printf("- %s → %s (%.2f RON) din %s [%s]%n",
                                name, p.getProductName(), p.getPrice(),
                                extractStoreShortName(p.getStoreName()), p.getDate());
                    } else {
                        System.out.printf("- %s → indisponibil%n", name);
                    }
                });
            } else if (opt.equals("3")) {
                System.out.println("\n=== REDUCERI ACTIVE ===");
                printDiscountsCsv(discounts);
            } else if (opt.equals("4")) {
                System.out.println("\n=== REDUCERI NOI (ultimele 24h) ===");
                List<Discount> recent = priceService.getNewDiscounts(discounts);
                printDiscountsCsv(recent);
            } else if (opt.equals("5")) {
                System.out.print("Magazin: ");
                String store = scanner.nextLine().trim();
                System.out.print("Brand: ");
                String brand = scanner.nextLine().trim();
                System.out.print("Categorie: ");
                String category = scanner.nextLine().trim();

                List<PriceHistoryEntry> history = priceService.getFilteredPriceHistory(
                        products,
                        store.isEmpty() ? null : store,
                        brand.isEmpty() ? null : brand,
                        category.isEmpty() ? null : category
                );
                System.out.println("\n=== ISTORIC PREȚURI ===");
                printPriceHistoryCsv(history);
            } else if (opt.equals("6")) {
                System.out.println("\n=== PRODUSE AVANTAJOASE ===");
                List<Product> best = priceService.getBestValueProducts(products);
                best.forEach(p -> {
                    double value = p.getPrice() / p.getPackageQuantity();
                    System.out.printf("- %s (%s): %.2f RON / %s din %s [%s]%n",
                            p.getProductName(), p.getBrand(),
                            value, p.getPackageUnit(),
                            extractStoreShortName(p.getStoreName()), p.getDate());
                });
            } else if (opt.equals("7")) {
                System.out.println("\n=== ALERTE DE PREȚ ===");
                List<PriceAlert> triggered = priceService.getTriggeredAlerts(alerts, products);
                if (triggered.isEmpty()) {
                    System.out.println("Nicio alertă activată.");
                } else {
                    for (PriceAlert alert : triggered) {
                        Product p = products.stream()
                                .filter(prod -> prod.getProductId().equalsIgnoreCase(alert.getProductId()))
                                .min(Comparator.comparing(Product::getPrice))
                                .orElse(null);
                        if (p != null) {
                            System.out.printf("- %s: %.2f RON (sub %.2f RON) la %s [%s]%n",
                                    p.getProductName(), p.getPrice(),
                                    alert.getTargetPrice(),
                                    extractStoreShortName(p.getStoreName()), p.getDate());
                        }
                    }
                }
                loader.saveAlertsToCsv(alerts, "src/main/resources/data/priceAlerts/price_alerts.csv");
            }
            else if(opt.equals("8"))
            {
                System.out.print("Introduceti numarul de reduceri pe care doriti sa il afisati: ");
                int nrProducts = Integer.parseInt(scanner.nextLine().trim());
                System.out.println("\n=== REDUCERI ACTIVE (filtrabil) ===");
                List<Discount> active = priceService.getTopActiveDiscounts(discounts, nrProducts);
                printDiscountsCsv(active);
            }
            else if (opt.equals("0")) {
                System.out.println("Ieșire...");
                break;
            } else {
                System.out.println("Opțiune invalidă. Reîncearcă.");
            }
        }

        scanner.close();
    }
}
