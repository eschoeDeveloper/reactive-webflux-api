package io.github.eschoe.reactivemockapi.config;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class H2ConsoleConfig {

    @Value("${spring.h2.port}")
    private int port;

    private Server server;

    @EventListener(ContextRefreshedEvent.class)
    public void start() throws SQLException {
        this.server = Server.createWebServer("-webPort", String.valueOf(port), "-tcpAllowOthers").start();
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() {
        this.server.stop();
    }

}
