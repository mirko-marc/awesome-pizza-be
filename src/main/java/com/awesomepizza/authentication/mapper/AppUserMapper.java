package com.awesomepizza.authentication.mapper;

import com.awesomepizza.authentication.entity.AppUserDB;
import com.awesomepizza.authentication.model.AppUserModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AppUserMapper {
    AppUserModel toModel(AppUserDB entity);
}



