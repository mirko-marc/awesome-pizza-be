package com.awesomepizza.administration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AdminRootResponse", description = "Authenticated administration entry point")
public class AdminRootResponseDTO {
    private String username;
    private List<String> roles;
}



