package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import java.time.LocalDateTime;

public interface EbicsConnection {
    Long getId();
    String getBankName();
    String getHostId();
    String getPartnerId();
    String getUserId();
    String getBankUrl();
    EbicsVersion getVersion();
    ConnectionStatus getStatus();
    LocalDateTime getLastConnected();
    LocalDateTime getCreatedAt();
}
