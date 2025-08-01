package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return v != null ? LocalDateTime.parse(v) : null;
    }
    
    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return v != null ? v.toString() : null;
    }
}