package com.harith.ecommerce.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Creating blueprint for product document 
@Document("product")
public class Product {
    @Id
    private String id;
    private String productName;
    private int price;
    private String description;
    private String productURL;
    private int stockQuanity;
    private String sellerId;

    // Default Constructor
    public Product() {

    }

    // Parameterized constructor
    public Product(String PN, int PR, String DE, String PURL, int SQ, String SI) {
        this.productName = PN;
        this.price = PR;
        this.description = DE;
        this.productURL = PURL;
        this.stockQuanity = SQ;
        this.sellerId = SI;
    }
public void setSellerId(String SI){this.sellerId = SI;}
    public String gettSellerId(){return this.sellerId;}
    public String getProductId() {
        return this.id;
    }

    public String getProductName() {
        return this.productName;
    }

    public int getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }

    public String getProductURL() {
        return this.productURL;
    }

    public int getStockQuanity() {
        return this.stockQuanity;
    }

    public void setProductName(String PN) {
        this.productName = PN;
    }

    public void setPrice(int PR) {
        this.price = PR;
    }

    public void setDescription(String DE) {
        this.description = DE;
    }

    public void setProductURL(String PURL) {
        this.productURL = PURL;
    }

    public void setStockQuanity(int SQ) {
        this.stockQuanity = SQ;
    }

}
