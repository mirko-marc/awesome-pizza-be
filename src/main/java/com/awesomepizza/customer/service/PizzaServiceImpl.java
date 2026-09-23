package com.awesomepizza.customer.service;

import com.awesomepizza.shared.mapper.PizzaMapper;
import com.awesomepizza.shared.model.PizzaModel;
import com.awesomepizza.shared.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PizzaServiceImpl implements PizzaService {
    private final PizzaRepository pizzaRepository;
    private final PizzaMapper pizzaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PizzaModel> getAvailablePizzas() {
        return pizzaRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(pizzaMapper::toModel)
                .toList();
    }
}



