package com.ntabana.backend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ProductServImp implements ProductService {
    @Autowired
    private ProductRepo productRepo;



    @Override
    public Product save(Product product) {
        return productRepo.save(product);
    }

    @Override
    public void deleteById(Long id) {
         productRepo.deleteById(id);
    }

    @Override
    public Product findById(Long id) {
        return productRepo.findById(id).get();
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public List<Product> searchProducts(String name, String category) {
        List<Product> results = new ArrayList<>();

        // Fetch all products from the repository
        List<Product> allProducts = getAllProducts();

        // Filter products based on the provided name and category
        for (Product product : allProducts) {
            boolean nameMatch = name == null || product.getName().toLowerCase().contains(name.toLowerCase());
            boolean categoryMatch = category == null || product.getCategory().toLowerCase().contains(category.toLowerCase());

            if (nameMatch && categoryMatch) {
                results.add(product);
            }
        }

        return results;
    }

    @Override
    public Product update(Product product) {
        return null;
    }

}
