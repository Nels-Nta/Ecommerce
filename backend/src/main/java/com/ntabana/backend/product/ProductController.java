package com.ntabana.backend.product;

//import ch.qos.logback.core.model.Model;
import com.ntabana.backend.services.UserService;
import com.ntabana.backend.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IModel;

import java.util.ArrayList;
import java.util.List;

@Controller

@CrossOrigin
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping("/admin/product-new")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "AddProduct";
    }


    @PostMapping("/admin/add")
    public String addProduct(@ModelAttribute @RequestBody Product product, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        product.setUser(userService.findByEmail(userDetails.getUsername()));
        productService.save(product);
        return "redirect:/admin/all";
    }
    @GetMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/admin/home";
    }
    @RequestMapping("/{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        return productService.findById(id);
    }
    @RequestMapping("/admin/all")
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
       return "products";
    }
    @GetMapping("/product/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "UpdateProduct";
    }

    @PostMapping("/product/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product, Model model) {
        Product existingProduct = productService.findById(id);
        if (existingProduct!= null) {
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setQuantity(product.getQuantity());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setImage(product.getImage());
            productService.save(existingProduct);
        }
        return "redirect:/admin/all";
    }
}
