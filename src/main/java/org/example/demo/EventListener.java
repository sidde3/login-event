package org.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.util.Set;

@Slf4j
public class EventListener implements EventListenerProvider {

    private Set<EventType> excludedEvents;
    private Set<OperationType> excludedAdminOperations;
    private KeycloakSession keycloakSession;

    public EventListener(Set<EventType> eventType, Set<OperationType> operationType, KeycloakSession keycloakSession){
        this.excludedEvents = eventType;
        this.excludedAdminOperations = operationType;
        this.keycloakSession = keycloakSession;
    }

    @Override
    public void onEvent(Event event) {
        // Ignore excluded events
        if (excludedEvents != null && excludedEvents.contains(event.getType())) {
            return;
        } else {
            log.info(toString(event));
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        // Ignore excluded operations
        if (excludedAdminOperations != null && excludedAdminOperations.contains(adminEvent.getOperationType())) {
            return;
        } else {
            log.info(toString(adminEvent));
        }
    }

    @Override
    public void close() {
        log.trace("closing event listener...");
    }

    private String toString(Event event) {
        RealmModel realmModel = keycloakSession.getContext().getRealm();

        StringBuilder sb = new StringBuilder();

        sb.append("|type="+event.getType());
        sb.append("|realmId="+realmModel.getName());
        sb.append("|clientId="+event.getClientId());
        sb.append("|ipAddress="+event.getIpAddress());
        sb.append("|username=" +keycloakSession.users().getUserById(realmModel,event.getUserId()).getUsername());

        if (event.getError() != null) {
            log.debug("|error="+event.getError());
        }
        /*
        if (event.getDetails() != null) {
            for (Map.Entry<String, String> e : event.getDetails().entrySet()) {
                sb.append("| "+sb.append(e.getKey()));
                if (e.getValue() == null || e.getValue().indexOf(' ') == -1) {
                    sb.append("=");
                    sb.append(e.getValue());
                } else {
                    sb.append("='");
                    sb.append(e.getValue());
                    sb.append("'");
                }
            }
        }
         */
        return sb.toString();
    }

    private String toString(AdminEvent adminEvent) {
        RealmModel realmModel = keycloakSession.getContext().getRealm();
        StringBuilder sb = new StringBuilder();

        sb.append("|operationType="+adminEvent.getOperationType());
        sb.append("|realmId="+realmModel.getName());
        sb.append("|clientId="+adminEvent.getAuthDetails().getClientId());
        sb.append("|username=" +keycloakSession.users().getUserById(realmModel,adminEvent.getAuthDetails().getUserId()).getUsername());
        sb.append("|ipAddress="+adminEvent.getAuthDetails().getIpAddress());
        sb.append("|resourcePath="+adminEvent.getResourcePath());

        if (adminEvent.getError() != null) {
            sb.append("|error="+adminEvent.getError());
        }
        return sb.toString();
    }
}
