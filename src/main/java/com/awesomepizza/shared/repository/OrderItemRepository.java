package com.awesomepizza.shared.repository;

import com.awesomepizza.shared.entity.OrderItemDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemDB, Long> {
    List<OrderItemDB> findAllByOrderIdOrderByIdAsc(Long orderId);
}




