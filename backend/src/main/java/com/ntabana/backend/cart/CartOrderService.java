package com.ntabana.backend.cart;

import java.util.List;

public interface CartOrderService {
    CartOrder save(CartOrder cartOrder);
    List<CartOrder> findAll();
}
