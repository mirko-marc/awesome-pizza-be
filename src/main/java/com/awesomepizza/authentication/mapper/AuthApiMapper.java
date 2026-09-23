package com.awesomepizza.authentication.mapper;

import com.awesomepizza.authentication.dto.LoginRequestDTO;
import com.awesomepizza.authentication.dto.LoginResponseDTO;
import com.awesomepizza.authentication.model.AccessTokenModel;
import com.awesomepizza.authentication.model.LoginModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AuthApiMapper {
    LoginModel toModel(LoginRequestDTO request);

    @Mapping(target = "accessToken", source = "token")
    LoginResponseDTO toDTO(AccessTokenModel model);
}



