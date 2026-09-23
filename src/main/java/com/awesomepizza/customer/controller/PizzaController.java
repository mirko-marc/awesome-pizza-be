package com.awesomepizza.customer.controller;

import com.awesomepizza.customer.dto.PizzaResponseDTO;
import com.awesomepizza.customer.mapper.PizzaApiMapper;
import com.awesomepizza.shared.model.PizzaModel;
import com.awesomepizza.customer.service.PizzaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pizzas")
@RequiredArgsConstructor
@Tag(name = "Pizzas", description = "Pizza menu operations")
public class PizzaController {
    private final PizzaService pizzaService;
    private final PizzaApiMapper pizzaApiMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List available pizzas")
    @ApiResponse(responseCode = "200", description = "Available pizza menu")
    public ResponseEntity<List<PizzaResponseDTO>> getAvailablePizzas() {
        List<PizzaModel> pizzas = pizzaService.getAvailablePizzas();
        List<PizzaResponseDTO> response = pizzaApiMapper.toDTOs(pizzas);

        return ResponseEntity.ok(response);
    }
}



