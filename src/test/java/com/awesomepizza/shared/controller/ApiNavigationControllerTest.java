package com.awesomepizza.shared.controller;

import com.awesomepizza.shared.exception.GlobalExceptionHandler;
import com.awesomepizza.customer.controller.PizzaController;
import com.awesomepizza.customer.mapper.PizzaApiMapperImpl;
import com.awesomepizza.shared.model.PizzaModel;
import com.awesomepizza.customer.service.PizzaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PizzaController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import({ApiController.class, PizzaApiMapperImpl.class, GlobalExceptionHandler.class})
class ApiNavigationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PizzaService pizzaService;

    @Test
    void exposesAvailablePizzas() throws Exception {
        PizzaModel margherita = PizzaModel.builder()
                .id(1L)
                .name("Margherita")
                .description("Pomodoro, mozzarella e basilico")
                .price(new BigDecimal("8.50"))
                .active(true)
                .build();
        when(pizzaService.getAvailablePizzas()).thenReturn(List.of(margherita));

        mockMvc.perform(get("/api/v1/pizzas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Margherita"));
    }
}


