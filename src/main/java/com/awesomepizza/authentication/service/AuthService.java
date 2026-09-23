package com.awesomepizza.authentication.service;

import com.awesomepizza.authentication.model.AccessTokenModel;
import com.awesomepizza.authentication.model.LoginModel;

public interface AuthService {
    AccessTokenModel login(LoginModel credentials);
}



