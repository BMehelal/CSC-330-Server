package com.harith.ecommerce.Controller;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harith.ecommerce.Repository.ProductRepository;
import com.harith.ecommerce.Repository.UserRepository;
import com.harith.ecommerce.Resources.CheckoutRequest;
import com.harith.ecommerce.Errors.ProductError;
import com.harith.ecommerce.Errors.UserError;
import com.harith.ecommerce.Model.Product;
import com.harith.ecommerce.Model.User;

@RestController
@RequestMapping("api")
public class CheckoutController {
    private UserRepository userRepository;
    private ProductRepository productRepository;

    public CheckoutController(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("checkout")
    @Transactional
    public ResponseEntity<String> checkout(@RequestBody CheckoutRequest checkoutRequest) {
        String userID = checkoutRequest.getCustomerId();
        Map<String, Integer> cartItems = checkoutRequest.getCartItem();
        try {
         Optional<User> optionalUser = userRepository.findById(userID);
         Set<String> setOfPoductIDs = cartItems.keySet();
         List<Product> products = productRepository.findAllById(setOfPoductIDs);
            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(404).body(UserError.NO_USER_FOUND.getErrorMessage());
            }
            if (setOfPoductIDs.size() != products.size()) {
                return ResponseEntity.status(404).body(ProductError.NO_PRODUCT_FOUND.getErrorMessage());
            }

         User user = optionalUser.get();
            int totalPrice = 0;
            for (Map.Entry<String, Integer> item : cartItems.entrySet()) {
                String productID = item.getKey();
                int quanity = item.getValue();
                Product product = null;
                for (Product p : products) {
                    if (p.getProductId().equals(productID)) {
                        product = p;
                        break;
                    }
                }
                if (product == null) {
                    return ResponseEntity.status(404).body(ProductError.NO_PRODUCT_FOUND.getErrorMessage());
                }
                if (product.getStockQuanity() < quanity) {
                    return ResponseEntity.status(400).body(ProductError.NOT_ENOUGH_STOCK.getErrorMessage());
                }
                totalPrice += product.getPrice() * quanity;
                product.setStockQuanity(product.getStockQuanity() - quanity);
                String sellerID = product.getSellerId();
                if (!sellerID.equals("0")) {
                    Optional<User> optionalSeller = userRepository.findById(sellerID);
                    if (!optionalSeller.isPresent()) {
                        return ResponseEntity.status(404).body(UserError.NO_USER_FOUND.getErrorMessage());
                    }
                    User seller = optionalSeller.get();
                    if (user.getId().equals(seller.getId())) {
                        return ResponseEntity.status(400)
                                .body(ProductError.CANNOT_BUY_YOUR_OWN_PRODUCT.getErrorMessage());
                    }
                    int currentPrice = product.getPrice() * quanity;
                    seller.setAvailableMoney(seller.getAvailableMoney() + currentPrice);
                    userRepository.save(seller);
                }
                //end of the loop
            }
            
            if (totalPrice > user.getAvailableMoney()) {
                return ResponseEntity.status(400).body(ProductError.INSUFFICIENT_FUNDS.getErrorMessage());
            }
            user.setAvailableMoney(user.getAvailableMoney() - totalPrice);
            user.addPurchasedItem(cartItems);
            userRepository.save(user);
            productRepository.saveAll(products);
            return ResponseEntity.status(200).body(setOfPoductIDs.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Checkout Failed");
        }
    }

}
