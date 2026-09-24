package com.awesomepizza.ordering.internal.service;

import com.awesomepizza.ordering.internal.mapper.PizzaMapper;
import com.awesomepizza.ordering.internal.model.PizzaModel;
import com.awesomepizza.ordering.internal.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PizzaService {
    private final PizzaRepository pizzaRepository;
    private final PizzaMapper pizzaMapper;

    @Transactional(readOnly = true)
    public List<PizzaModel> getAvailablePizzas() {
        return pizzaRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(pizzaMapper::toModel)
                .toList();
    }
}
