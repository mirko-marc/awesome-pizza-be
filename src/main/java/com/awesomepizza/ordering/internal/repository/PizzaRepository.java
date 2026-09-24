package com.awesomepizza.ordering.internal.repository;

import com.awesomepizza.ordering.internal.entity.PizzaDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PizzaRepository extends JpaRepository<PizzaDB, Long> {
    List<PizzaDB> findAllByActiveTrueOrderByNameAsc();

    Optional<PizzaDB> findByNameIgnoreCase(String name);
}




