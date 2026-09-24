package com.awesomepizza.ordering.internal.controller.administration;

import com.awesomepizza.ordering.internal.dto.administration.AdminRootResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Administration", description = "Protected pizza maker APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the authenticated administration entry point")
    public ResponseEntity<AdminRootResponseDTO> getAdmin(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        AdminRootResponseDTO response = AdminRootResponseDTO.builder()
                .username(authentication.getName())
                .roles(roles)
                .build();

        return ResponseEntity.ok(response);
    }
}



