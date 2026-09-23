package com.awesomepizza.customer.mapper;

import com.awesomepizza.customer.dto.PizzaResponseDTO;
import com.awesomepizza.shared.model.PizzaModel;
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



