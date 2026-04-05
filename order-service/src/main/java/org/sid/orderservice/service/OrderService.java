package org.sid.orderservice.service;


import org.sid.orderservice.clients.ProductClient;
import org.sid.orderservice.clients.UserClient;
import org.sid.orderservice.dto.OrderResponseDTO;
import org.sid.orderservice.dto.ProductDTO;
import org.sid.orderservice.dto.UserDTO;
import org.sid.orderservice.entity.Order;
import org.sid.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {


    private final OrderRepository orderRepository;

    private final UserClient userClient;      // Feign

    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, UserClient userClient, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
        this.productClient = productClient;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public OrderResponseDTO createOrder(Order order) {

        Order saved=orderRepository.save(order);

        UserDTO user = userClient.getUserById(saved.getUserId());
        ProductDTO product = productClient.getProductById(saved.getProductId());

        // 3. build enriched response
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(saved.getId());
        response.setQuantity(saved.getQuantity());
        response.setUser(user);
        response.setProduct(product);

        return response;
    }
}