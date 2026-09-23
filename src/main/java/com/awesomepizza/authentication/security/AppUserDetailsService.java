package com.awesomepizza.authentication.security;

import com.awesomepizza.authentication.mapper.AppUserMapper;
import com.awesomepizza.authentication.model.AppUserModel;
import com.awesomepizza.authentication.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserModel user = appUserRepository.findByUsernameIgnoreCase(username)
                .map(appUserMapper::toModel)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .disabled(!user.isEnabled())
                .build();
    }
}



