package com.ntabana.backend.cart;

import com.ntabana.backend.product.Product;
import com.ntabana.backend.product.ProductService;
import com.ntabana.backend.services.UserService;
import com.ntabana.backend.user.User;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    UserService userService;
    @Autowired
    CartService cartService;
    @Autowired
    ProductService productService;

    @GetMapping({"/user/home","/"})
    public String getUserProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "userHome";
    }
    @GetMapping("/all-products")
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
    @PostMapping("user/remove-product")
    public String removeProduct(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("id") Long productId, RedirectAttributes redirectAttributes) {
        Cart cart = cartService.getCartChecked(userService.findByEmail(userDetails.getUsername()));
        if (cartService.removeProduct(cart.getId(), productId)) {
            redirectAttributes.addFlashAttribute("success", "Product removed successfully");
            return "redirect:/user/cart";
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found");
            return "redirect:/user/cart";
        }
    }


    @PostMapping("/user/add-product")
    public String addProduct(Product product, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());
        if (cartService.cartExists(user)) {
            Cart cart = cartService.getCartChecked(user);
            if (!cartService.addProduct(cart.getId(), product.getId())) {
                redirectAttributes.addFlashAttribute("message", "You have already added this product");
                return "redirect:/user/home";
            }
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProducts(List.of(product));
            cartService.saveCart(cart);
        }
        redirectAttributes.addFlashAttribute("message", "You have successfully added this product");
        return "redirect:/user/home";

    }

    @GetMapping("/user/cart")
    public String getCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Cart cart = cartService.getCartChecked(user);
        List<CartOrder> cartOrders;
        if (cart == null) {
            cartOrders = new ArrayList<>();
        } else {
            cartOrders = cart.getCartOrders();
        }
        model.addAttribute("carts", cartOrders);
        return "carts";
    }

    @GetMapping("/user/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Cart cart = cartService.getCartChecked(user);


            if (cart.getProducts().isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Your cart is empty. Please add some products before checking out.");
                return "redirect:/user/cart";
            }

            cart.setChecked(true);
            cartService.checkOutCart(cart);


            redirectAttributes.addFlashAttribute("message", "Checkout successful!");
            return "redirect:/user/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred during checkout. Please try again.");
            return "redirect:/user/cart";
        }
    }

    @PostMapping("/user/update-quantity")
    public String updateQuantity(Integer cartOrderId, int quantity, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());
        Cart cart = cartService.getCartChecked(user);
        if (cart == null) {
            redirectAttributes.addFlashAttribute("message", "Your cart is empty");
            return "redirect:/user/cart";
        }

        boolean updated = cartService.updateQuantity(cartOrderId, quantity);
        if (updated) {
            redirectAttributes.addFlashAttribute("message", "Quantity updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to update quantity");
        }

        return "redirect:/user/cart";
    }
    @GetMapping("/checkout")
    public String getCheckouts(Model model){
        try {
            model.addAttribute("carts", cartService.getAllCheckedCarts());
        } catch (Exception e){e.printStackTrace();}

        return "CheckoutManagement";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name="name", required=false) String name,
                         @RequestParam(name="category", required=false) String category,
                         Model model) {
        List<Product> searchResults = productService.searchProducts(name, category);
        model.addAttribute("results", searchResults);
        return "searchResults";
    }

    private List<String> performSearch(String name, String category) {
        List<String> results = new ArrayList<>();

        // Fetch all products from the database
        List<Product> allProducts = productService.getAllProducts();

        // Filter products based on the provided name and category
        for (Product product : allProducts) {
            boolean nameMatch = name == null || product.getName().toLowerCase().contains(name.toLowerCase());
            boolean categoryMatch = category == null || product.getCategory().toLowerCase().contains(category.toLowerCase());

            if (nameMatch && categoryMatch) {
                results.add(product.getName() + " - " + product.getCategory());
            }
        }

        return results;
    }

//    @PostMapping("/checkout/update-status/{cartId}")
//    public String updateCartStatus(@PathVariable Long cartId, RedirectAttributes redirectAttributes) {
//        boolean updated = cartService.updateCartStatus(cartId, "delivered");
//        if (updated) {
//            redirectAttributes.addFlashAttribute("message", "Cart status updated to delivered");
//        } else {
//            redirectAttributes.addFlashAttribute("error", "Failed to update cart status");
//        }
//        return "redirect:/checkout";
//    }
@PostMapping("/checkout/update-status")
public String updateCartStatus(
        @RequestParam("selectedCarts") List<Long> id,
        @RequestParam("status") CartStatus status,
        Model model) {
    try {
        List<Cart> selectedCarts = cartService.findCartsByIds(id);
        selectedCarts.forEach(cart -> cart.setStatus(status));
        cartService.saveAll(selectedCarts);

    } catch (Exception e) {
        e.printStackTrace();
    }
    return "redirect:/checkout";
}
}


