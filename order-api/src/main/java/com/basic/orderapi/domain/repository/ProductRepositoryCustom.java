package com.basic.orderapi.domain.repository;

import com.basic.orderapi.domain.model.Product;

import java.util.List;

public interface ProductRepositoryCustom{
    List<Product> searchByName(String name);
}
