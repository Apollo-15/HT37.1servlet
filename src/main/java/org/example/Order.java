package org.example;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Order {
    private int orderId;
    private double price;
    private final List<Product> products;
    private final LocalDateTime date = LocalDateTime.now();

    public Order(int orderId, double price) {
        this.products = new ArrayList<>();
        this.orderId = orderId;
        this.price = price;
    }

    public Order() {
        this.products = new ArrayList<>();
    }

    public void addProductsToOrder(Product product) {
        products.add(product);
        price += product.getPrice();
    }
}
