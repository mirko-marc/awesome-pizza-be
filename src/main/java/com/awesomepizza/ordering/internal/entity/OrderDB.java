package com.awesomepizza.ordering.internal.entity;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.shared.entity.TechnicalDataDB;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDB extends TechnicalDataDB {

    @Id
    @SequenceGenerator(name = "orders_id_generator", sequenceName = "orders_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_id_generator")
    private Long id;

    @Column(name = "order_code", nullable = false, unique = true, updatable = false)
    private UUID orderCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Column(name = "preparation_started")
    private Instant preparationStarted;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<OrderItemDB> items = new ArrayList<>();

    public void addItem(OrderItemDB item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItemDB item) {
        items.remove(item);
        item.setOrder(null);
    }

    public static OrderDB create() {
        return new OrderDB();
    }
}



