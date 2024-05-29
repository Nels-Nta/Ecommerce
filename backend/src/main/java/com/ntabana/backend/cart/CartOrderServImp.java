package com.ntabana.backend.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CartOrderServImp implements CartOrderService {
    @Autowired
    private CartOrderRepo cartOrderRepo;
    @Override
    public CartOrder save(CartOrder cartOrder) {
        return cartOrderRepo.save(cartOrder);
    }

    @Override
    public List<CartOrder> findAll() {
        return cartOrderRepo.findAll();
    }
}
