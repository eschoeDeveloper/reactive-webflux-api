package io.github.eschoe.reactivemockapi.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class H2ConsoleConfig {

    private Server server;

    @EventListener(ContextRefreshedEvent.class)
    public void start() throws SQLException {
        this.server = Server.createWebServer("-webPort", "8091", "-tcpAllowOthers").start();
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() {
        this.server.stop();
    }

}
