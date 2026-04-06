package org.sid.orderservice.service;

import org.sid.orderservice.clients.ProductClient;
import org.sid.orderservice.clients.UserClient;
import org.sid.orderservice.dto.OrderResponseDTO;
import org.sid.orderservice.dto.ProductDTO;
import org.sid.orderservice.dto.UserDTO;
import org.sid.orderservice.entity.Order;
import org.sid.orderservice.exception.BadRequestException;
import org.sid.orderservice.exception.ResourceNotFoundException;
import org.sid.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id));
    }

    public OrderResponseDTO createOrder(Order order) {
        if (order.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        // no try/catch → fallback handles failures automatically ✅
        UserDTO user = userClient.getUserById(order.getUserId());
        ProductDTO product = productClient.getProductById(order.getProductId());

        Order saved = orderRepository.save(order);

        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(saved.getId());
        response.setQuantity(saved.getQuantity());
        response.setUser(user);
        response.setProduct(product);
        return response;
    }
}