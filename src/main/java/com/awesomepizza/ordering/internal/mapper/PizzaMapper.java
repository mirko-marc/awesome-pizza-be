package com.awesomepizza.ordering.internal.mapper;

import com.awesomepizza.ordering.internal.entity.PizzaDB;
import com.awesomepizza.ordering.internal.model.PizzaModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PizzaMapper {

    PizzaModel toModel(PizzaDB entity);
}



