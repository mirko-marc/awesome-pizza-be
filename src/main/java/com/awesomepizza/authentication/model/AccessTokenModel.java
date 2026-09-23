package com.awesomepizza.authentication.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccessTokenModel {
    String token;
    String tokenType;
    long expiresIn;
}



