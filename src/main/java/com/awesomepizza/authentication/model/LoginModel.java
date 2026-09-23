package com.awesomepizza.authentication.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginModel {
    String username;
    String password;
}



