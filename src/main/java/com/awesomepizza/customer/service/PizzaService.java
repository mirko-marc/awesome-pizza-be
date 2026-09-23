package com.awesomepizza.customer.service;

import com.awesomepizza.shared.model.PizzaModel;

import java.util.List;

public interface PizzaService {
    List<PizzaModel> getAvailablePizzas();
}



