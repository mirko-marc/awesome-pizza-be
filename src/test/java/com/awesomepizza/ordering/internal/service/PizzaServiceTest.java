package com.awesomepizza.ordering.internal.service;

import com.awesomepizza.ordering.internal.entity.PizzaDB;
import com.awesomepizza.ordering.internal.mapper.PizzaMapper;
import com.awesomepizza.ordering.internal.model.PizzaModel;
import com.awesomepizza.ordering.internal.repository.PizzaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PizzaServiceTest {
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private PizzaMapper pizzaMapper;

    @Test
    void returnsOnlyTheActivePizzasProvidedByTheRepository() {
        PizzaDB margheritaEntity = PizzaDB.create();
        PizzaDB marinaraEntity = PizzaDB.create();
        PizzaModel margherita = PizzaModel.builder().id(1L).name("Margherita").build();
        PizzaModel marinara = PizzaModel.builder().id(2L).name("Marinara").build();
        when(pizzaRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(margheritaEntity, marinaraEntity));
        when(pizzaMapper.toModel(margheritaEntity)).thenReturn(margherita);
        when(pizzaMapper.toModel(marinaraEntity)).thenReturn(marinara);
        PizzaService service = new PizzaService(pizzaRepository, pizzaMapper);

        assertThat(service.getAvailablePizzas())
                .containsExactly(margherita, marinara);
    }
}
