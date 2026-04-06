package org.sid.orderservice.clients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.sid.orderservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallback")
    ProductDTO getProductById(@PathVariable Long id);

    default ProductDTO fallback(Long id, Throwable t) {  // ← add Throwable
        ProductDTO fallback = new ProductDTO();
        fallback.setId(id);
        fallback.setName("Unknown Product");
        fallback.setPrice(0.0);
        fallback.setStock(0);
        return fallback;
    }
}