package com.ntabana.backend.cart;

import com.ntabana.backend.product.Product;
import com.ntabana.backend.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer Cart_quantity ;
    @ManyToOne
    private User user;
    @ManyToMany
    private List<Product> products;
    private boolean checked;
    @ManyToMany
    private List<CartOrder> cartOrders;
    @Enumerated(EnumType.STRING)
    private CartStatus status;
}
