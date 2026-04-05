package org.sid.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private double price;
    private int stock;

}