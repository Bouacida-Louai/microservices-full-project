package org.sid.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class OrderResponseDTO {

        private Long orderId;
        private int quantity;
        private UserDTO user;
        private ProductDTO product;
   }

