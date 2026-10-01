package murach.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {

    private final List<LineItem> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void addItem(Product product) {

        for (LineItem item : items) {

            if (item.getProduct().getCode()
                    .equals(product.getCode())) {

                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }

        items.add(new LineItem(product));
    }

    public void updateItem(String productCode, int quantity) {

        for (LineItem item : items) {

            if (item.getProduct().getCode()
                    .equals(productCode)) {

                if (quantity <= 0) {
                    items.remove(item);
                } else {
                    item.setQuantity(quantity);
                }

                return;
            }
        }
    }

    public void removeItem(String productCode) {

        for (LineItem item : items) {

            if (item.getProduct().getCode()
                    .equals(productCode)) {

                items.remove(item);
                return;
            }
        }
    }

    public double getTotal() {

        double total = 0;

        for (LineItem item : items) {
            total += item.getTotal();
        }

        return total;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}