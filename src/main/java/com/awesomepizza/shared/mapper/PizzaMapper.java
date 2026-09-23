package com.awesomepizza.shared.mapper;

import com.awesomepizza.shared.entity.PizzaDB;
import com.awesomepizza.shared.model.PizzaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PizzaMapper {

    PizzaModel toModel(PizzaDB entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PizzaDB toEntity(PizzaModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PizzaModel model, @MappingTarget PizzaDB entity);

    @ObjectFactory
    default PizzaDB newEntity() {
        return PizzaDB.create();
    }
}



