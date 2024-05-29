package com.ntabana.backend.cart;

import com.ntabana.backend.product.Product;
import com.ntabana.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private CartOrderRepo cartOrderRepo;
    @Override
    public Cart getCart(User user) {
        return cartRepo.findByUser(user).get();
    }

    @Override
    public Cart getCartChecked(User user) {
        return cartRepo.findByUserAndCheckedIsFalse(user).orElse(null);
    }

    @Override
    public boolean updateQuantity(Integer cartOrderId, Integer quantity) {
        Optional<CartOrder> cartOrderOptional = cartOrderRepo.findById(cartOrderId);
        if (cartOrderOptional.isPresent()) {
            CartOrder cartOrder = cartOrderOptional.get();
            cartOrder.setQuantity(quantity);
            cartOrderRepo.save(cartOrder);
            return true;
        }
        return false;
    }

    @Override
    public List<Cart> getAllCheckedCarts() {
        return cartRepo.findAllByCheckedIsTrue();
    }



    @Override
    public void saveAll(List<Cart> carts) {
        cartRepo.saveAll(carts);
    }

    @Override
    public List<Cart> findCartsByIds(List<Long> cartIds) {
        return cartRepo.findAllById(cartIds);
    }

    @Override
    public boolean checkOutCart(Cart cart) {
        cartRepo.save(cart);
        return cartRepo.existsById(cart.getId());
    }

    @Override
    public boolean removeProduct(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId).orElse(null);
        if (cart == null) {
            return false;
        }
        List<CartOrder> cartOrders = cart.getCartOrders();
        Optional<CartOrder> cartOrderOptional = cartOrders.stream().filter(cartOrder -> cartOrder.getProduct().getId().equals(productId)).findFirst();
        if (cartOrderOptional.isEmpty()) {
            return false;
        }
        cartOrders.remove(cartOrderOptional.get());
        cart.setCartOrders(cartOrders);
        cartRepo.save(cart);
        return true;

    }

    @Override
    public boolean addProduct(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId).orElse(null);
        if (cart != null) {
            List<CartOrder> cartOrders = cart.getCartOrders();
            CartOrder cartOrder = cartOrders.stream()
                    .filter(order -> order.getProduct().getId().equals(productId)).findFirst().orElse(null);
//            List<Product> products = cart.getProducts();
//            Product product = products.stream()
//                    .filter(p -> p.getId().equals(productId))
//                    .findFirst()
//                    .orElse(null);
            if (cartOrder == null) {
                cartOrder = new CartOrder();
                Product product = new Product();
                product.setId(productId);
                cartOrder.setProduct(product);
                cartOrder.setQuantity(1);
                cartOrderRepo.save(cartOrder);
                cartOrders.add(cartOrder);
                cart.setCartOrders(cartOrders);
                cartRepo.save(cart);
                return true;
            }
//            if (product == null) {
//                // Product does not exist in the cart, add it
//                product = new Product(); // Replace with your code to fetch the product
//                product.setId(productId);
//                products.add(product);
//                cartRepo.save(cart);
//                return true;
//            }
        }
        return false;
    }


    @Override
    public boolean cartExists(User user) {
        return cartRepo.existsByCheckedIsFalseAndUser(user);
    }

    @Override
    public boolean saveCart(Cart cart) {
        cartRepo.save(cart);
        return cartRepo.existsById(cart.getId());
    }



}
