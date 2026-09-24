package com.awesomepizza.ordering.internal.realtime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PIZZA_MAKER_AUTHORITY = "ROLE_PIZZA_MAKER";

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter authenticationConverter;
    private final List<String> allowedOrigins;

    public WebSocketConfiguration(
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter authenticationConverter,
            @Value("#{'${awesome-pizza.security.cors.allowed-origins:http://localhost:4200}'.split(',')}")
            List<String> allowedOrigins) {
        this.jwtDecoder = jwtDecoder;
        this.authenticationConverter = authenticationConverter;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigins.toArray(String[]::new));
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(adminChannelInterceptor());
    }

    ChannelInterceptor adminChannelInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                        message, StompHeaderAccessor.class);
                if (accessor == null) {
                    return message;
                }
                if (accessor.getCommand() == StompCommand.CONNECT) {
                    accessor.setUser(authenticate(accessor));
                } else if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
                    authorizeSubscription(accessor);
                } else if (accessor.getCommand() == StompCommand.SEND) {
                    throw new AccessDeniedException("Invio di messaggi STOMP non consentito");
                }
                return message;
            }
        };
    }

    private Authentication authenticate(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationCredentialsNotFoundException("Bearer token STOMP mancante");
        }
        Jwt jwt = jwtDecoder.decode(authorization.substring(BEARER_PREFIX.length()));
        Authentication authentication = authenticationConverter.convert(jwt);
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Bearer token STOMP non valido");
        }
        return authentication;
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        if (!AdminOrderNotificationListener.ADMIN_ORDERS_TOPIC.equals(accessor.getDestination())) {
            throw new AccessDeniedException("Destinazione STOMP non consentita");
        }
        if (!(accessor.getUser() instanceof Authentication authentication)
                || authentication.getAuthorities().stream()
                .noneMatch(authority -> PIZZA_MAKER_AUTHORITY.equals(authority.getAuthority()))) {
            throw new AccessDeniedException("Ruolo PIZZA_MAKER richiesto");
        }
    }
}
