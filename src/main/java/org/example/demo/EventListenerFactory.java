package org.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.OperationType;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class EventListenerFactory implements EventListenerProviderFactory {
    private Set<EventType> excludedEvents;
    private Set<OperationType> excludedAdminOperations;

    public EventListenerFactory(){
        log.info("no-arg constructor");
    }

    public EventListenerFactory(Set<EventType> excludedEvents, Set<OperationType> excludedAdminOperations) {
        this.excludedEvents = excludedEvents;
        this.excludedAdminOperations = excludedAdminOperations;
    }

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new EventListener(excludedEvents, excludedAdminOperations, keycloakSession);
    }

    @Override
    public void init(Config.Scope config) {

        String[] excludes = config.getArray("exclude-events");

        if (excludes != null) {
            excludedEvents = new HashSet<>();
            for (String e : excludes) {
                excludedEvents.add(EventType.valueOf(e));
            }
        }

        String[] excludesOperations = config.getArray("excludesOperations");

        if (excludesOperations != null) {
            excludedAdminOperations = new HashSet<>();
            for (String e : excludesOperations) {
                excludedAdminOperations.add(OperationType.valueOf(e));
            }
        }
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        keycloakSessionFactory
                .getSpis()
                .forEach(value ->
                        log.debug(value.getProviderFactoryClass().getName()));

    }

    @Override
    public void close() {
        log.info("Closing EventListenerFactory..");
    }

    @Override
    public String getId() {
        return "siem-event";
    }
}
