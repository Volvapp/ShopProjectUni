package org.example.shopproject.repository;

import org.example.shopproject.model.entity.Product;
import org.example.shopproject.model.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByName(String name);

}
