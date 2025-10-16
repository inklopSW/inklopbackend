package com.inklop.inklop.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conectar el cliente (ej: frontend con SockJS)
        registry.addEndpoint("/ws") // ej: ws://localhost:8080/ws
                .setAllowedOriginPatterns("*") 
                .withSockJS(); 
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefijo para los métodos @MessageMapping (entrada)
        registry.setApplicationDestinationPrefixes("/app");

        // Prefijo para los tópicos de salida (suscripciones)
        registry.enableSimpleBroker("/topic");
    }
}
