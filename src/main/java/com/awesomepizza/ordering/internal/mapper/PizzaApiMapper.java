package com.awesomepizza.ordering.internal.mapper;

import com.awesomepizza.ordering.internal.dto.customer.PizzaResponseDTO;
import com.awesomepizza.ordering.internal.model.PizzaModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PizzaApiMapper {
    PizzaResponseDTO toDTO(PizzaModel model);

    List<PizzaResponseDTO> toDTOs(List<PizzaModel> models);
}



