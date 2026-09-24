package com.awesomepizza.ordering.internal.realtime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebSocketConfigurationTest {
    private JwtDecoder jwtDecoder;
    private JwtAuthenticationConverter authenticationConverter;
    private ChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        jwtDecoder = mock(JwtDecoder.class);
        authenticationConverter = mock(JwtAuthenticationConverter.class);
        interceptor = new WebSocketConfiguration(
                jwtDecoder, authenticationConverter, List.of("http://localhost:4200"))
                .adminChannelInterceptor();
    }

    @Test
    void authenticatesTheStompConnectFrameWithTheBearerToken() {
        Jwt jwt = jwt();
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "pizzaiolo", null, List.of(new SimpleGrantedAuthority("ROLE_PIZZA_MAKER")));
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);
        when(authenticationConverter.convert(jwt)).thenReturn(authentication);
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer valid-token");

        interceptor.preSend(message(accessor), mock(MessageChannel.class));

        assertThat(accessor.getUser()).isSameAs(authentication);
    }

    @Test
    void rejectsAConnectFrameWithoutBearerToken() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);

        assertThatThrownBy(() -> interceptor.preSend(message(accessor), mock(MessageChannel.class)))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    void allowsPizzaMakersToSubscribeToTheAdminTopic() {
        StompHeaderAccessor accessor = subscription("ROLE_PIZZA_MAKER",
                AdminOrderNotificationListener.ADMIN_ORDERS_TOPIC);

        Message<?> result = interceptor.preSend(message(accessor), mock(MessageChannel.class));

        assertThat(result).isNotNull();
    }

    @Test
    void rejectsUsersWithoutThePizzaMakerRole() {
        StompHeaderAccessor accessor = subscription("ROLE_CUSTOMER",
                AdminOrderNotificationListener.ADMIN_ORDERS_TOPIC);

        assertThatThrownBy(() -> interceptor.preSend(message(accessor), mock(MessageChannel.class)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void rejectsSubscriptionsToOtherTopics() {
        StompHeaderAccessor accessor = subscription("ROLE_PIZZA_MAKER", "/topic/orders");

        assertThatThrownBy(() -> interceptor.preSend(message(accessor), mock(MessageChannel.class)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void rejectsMessagesSentByClients() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setDestination(AdminOrderNotificationListener.ADMIN_ORDERS_TOPIC);
        accessor.setUser(UsernamePasswordAuthenticationToken.authenticated(
                "pizzaiolo", null,
                List.of(new SimpleGrantedAuthority("ROLE_PIZZA_MAKER"))));

        assertThatThrownBy(() -> interceptor.preSend(message(accessor), mock(MessageChannel.class)))
                .isInstanceOf(AccessDeniedException.class);
    }

    private StompHeaderAccessor subscription(String authority, String destination) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setUser(UsernamePasswordAuthenticationToken.authenticated(
                "user", null, List.of(new SimpleGrantedAuthority(authority))));
        return accessor;
    }

    private Message<byte[]> message(StompHeaderAccessor accessor) {
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private Jwt jwt() {
        return new Jwt("valid-token", NOW.minusSeconds(60), NOW.plusSeconds(300),
                java.util.Map.of("alg", "HS256"), java.util.Map.of("sub", "pizzaiolo"));
    }

    private static final Instant NOW = Instant.parse("2026-09-24T10:30:00Z");
}
