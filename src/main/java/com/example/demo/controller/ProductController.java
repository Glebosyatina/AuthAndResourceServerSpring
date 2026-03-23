package com.example.demo.controller;

import com.example.demo.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@EnableMethodSecurity
public class ProductController {

    // in-memory хранилище продуктов
    private static final List<Product> products = new ArrayList<>();

    //http ручки для работы с продуктами,
    //за работу с пользователями отвечает AuthorizationServerConfig

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(){

        if (products.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProducts(@PathVariable("id") Integer id){
        for (Product pr : products){
               if (Objects.equals(pr.getId(), id)){
                   return ResponseEntity.ok(pr);
               }
        }
        return ResponseEntity.notFound().build();
    }


    //@PreAuthorize автоматически будет требовать чтобы в jwt токене была информация о разрешении на запись
    @PostMapping("/products")
    @PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<?> addProduct(@RequestBody Product product){
        product.setId(products.size()+1);
        products.add(product);
        URI uri = URI.create("/api/products" + product.getId());
        return ResponseEntity.created(uri).body(product);
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody Product product){
        product.setId(id);

        if (!products.contains(product)){
            return null;
        }
        int idx = products.indexOf(product);
        products.set(idx, product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<?> removeProduct(@PathVariable Integer id){
        products.removeIf( (product) -> Objects.equals(product.getId(), id));
        return ResponseEntity.ok("deleted: " + id);
    }
}
