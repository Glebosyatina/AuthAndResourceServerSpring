package com.example.demo.controller;

import com.example.demo.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class ProductController {

    private static final List<Product> products = new ArrayList<>();

    @GetMapping("/products")
    public List<Product> getProducts(){
        return products;
    }

    @GetMapping("/products/{id}")
    public Product getProducts(@PathVariable("id") Integer id){
        for (Product pr : products){
               if (Objects.equals(pr.getId(), id)){
                   return pr;
               }
        }
        return null;
    }

    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product){
        product.setId(products.size()+1);
        products.add(product);
        return product;
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable Integer id, @RequestBody Product product){
        product.setId(id);

        if (!products.contains(product)){
            return null;
        }
        int idx = products.indexOf(product);
        products.set(idx, product);
        return product;
    }

    @DeleteMapping("/products/{id}")
    public void removeProduct(@PathVariable Integer id){
        products.removeIf( (product) -> Objects.equals(product.getId(), id));
    }
}
