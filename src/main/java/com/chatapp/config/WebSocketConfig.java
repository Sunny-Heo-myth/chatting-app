package com.chatapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
It's like configuring a Kafka broker and topics
but for WebSocket communication instead of streaming data.
 */
@Configuration
@EnableWebSocketMessageBroker   // Enables Spring’s internal broker system
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {  // Hook to customize the broker and endpoints

    /*
    configuring a Kafka bootstrap server endpoint
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws") // where clients connect
                .setAllowedOriginPatterns("*")
                .withSockJS();  // Adds SockJS fallback — clients without native WebSocket can still connect
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
        This creates a lightweight in-memory broker (like a Kafka topic).
        Any message "sent to" /topic/sth is broadcast to all subscribers.
         */
        registry.enableSimpleBroker("/topic");

        /*
        Messages sent to /app/** are routed to @MessageMapping methods (like ChatController).
         */
        registry.setApplicationDestinationPrefixes("/app");
    }
}