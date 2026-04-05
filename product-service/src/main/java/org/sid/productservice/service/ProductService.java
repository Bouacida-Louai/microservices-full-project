package org.sid.productservice.service;

import org.sid.productservice.entities.Product;
import org.sid.productservice.exception.BadRequestException;
import org.sid.productservice.exception.ResourceNotFoundException;
import org.sid.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new BadRequestException("Product name cannot be empty");
        }
        if (product.getPrice() <= 0) {
            throw new BadRequestException("Product price must be greater than 0");
        }
        return productRepository.save(product);
    }
}
