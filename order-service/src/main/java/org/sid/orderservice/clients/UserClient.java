package org.sid.orderservice.clients;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.sid.orderservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallback")
    UserDTO getUserById(@PathVariable Long id);

    default UserDTO fallback(Long id, Throwable t) {  // ← add Throwable
        UserDTO fallback = new UserDTO();
        fallback.setId(id);
        fallback.setName("Unknown User");
        fallback.setEmail("N/A");
        return fallback;
    }
}


