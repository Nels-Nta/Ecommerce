package com.ntabana.backend.product;

import java.util.List;

public interface ProductService{
    Product findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
    List<Product> getAllProducts();
    List<Product> searchProducts(String productName, String Category);
    Product update(Product product);
}
