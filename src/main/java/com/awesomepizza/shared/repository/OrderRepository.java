package com.awesomepizza.shared.repository;

import com.awesomepizza.shared.entity.OrderDB;
import com.awesomepizza.shared.enumeration.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderDB, Long>, JpaSpecificationExecutor<OrderDB> {

    @EntityGraph(attributePaths = {"items", "items.pizza"})
    Optional<OrderDB> findByOrderCode(UUID orderCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OrderDB> findFirstByOrderByIdAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderDB o where o.orderCode = :orderCode")
    Optional<OrderDB> findForUpdateByOrderCode(@Param("orderCode") UUID orderCode);

    Page<OrderDB> findAllByStatusOrderByCreatedAtAsc(OrderStatus status, Pageable pageable);

    boolean existsByStatus(OrderStatus status);
}




