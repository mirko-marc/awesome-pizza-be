package com.awesomepizza.ordering.internal.entity;

import com.awesomepizza.shared.entity.TechnicalDataDB;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pizza")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PizzaDB extends TechnicalDataDB {

    @Id
    @SequenceGenerator(name = "pizza_id_generator", sequenceName = "pizza_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pizza_id_generator")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    public static PizzaDB create() {
        return new PizzaDB();
    }
}



