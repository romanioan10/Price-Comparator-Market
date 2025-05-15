package Loader;

import com.opencsv.bean.CsvToBeanBuilder;
import Domain.Product;
import Domain.Discount;
import Domain.PriceAlert;
import Domain.PriceHistoryEntry;

import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public class CsvDataLoader {

    public List<Product> loadProducts(String filePath) {
        String[] nameParts = extractStoreAndDate(filePath);
        String storeName = nameParts[0];
        String date = nameParts[1];

        try {
            List<Product> products = new CsvToBeanBuilder<Product>(new FileReader(filePath))
                    .withType(Product.class)
                    .withSeparator(';')
                    .build()
                    .parse();

            products.forEach(p -> {
                p.setStoreName(storeName);
                p.setDate(date);
            });

            return products;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Discount> loadDiscounts(String filePath) {
        String storeName = extractStoreNameOnly(filePath);

        try {
            List<Discount> discounts = new CsvToBeanBuilder<Discount>(new FileReader(filePath))
                    .withType(Discount.class)
                    .withSeparator(';')
                    .build()
                    .parse();

            discounts.forEach(d -> d.setStoreName(storeName));

            return discounts;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<PriceAlert> loadPriceAlerts(String filePath) {
        try {
            return new CsvToBeanBuilder<PriceAlert>(new FileReader(filePath))
                    .withType(PriceAlert.class)
                    .withSeparator(',')
                    .build()
                    .parse();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<PriceHistoryEntry> loadPriceHistoryFromProducts(List<Product> products) {
        return products.stream()
                .map(p -> new PriceHistoryEntry(
                        p.getProductId(),
                        p.getProductName(),
                        p.getStoreName(),
                        p.getDate(),
                        p.getPrice()
                ))
                .collect(Collectors.toList());
    }

    private String[] extractStoreAndDate(String filePath) {
        // Exemplu: lidl_2025-05-08.csv
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1).replace(".csv", "");
        String[] parts = fileName.split("_");
        return new String[]{parts[0], parts[1]};
    }

    private String extractStoreNameOnly(String filePath) {
        // Exemplu: kaufland_discounts_2025-05-01.csv
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1).replace(".csv", "");
        return fileName.split("_")[0];
    }
}
