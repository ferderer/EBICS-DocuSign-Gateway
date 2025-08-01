package de.ferderer.ebicsdocusign.gateway.domain.ebics.api;

import de.ferderer.ebicsdocusign.gateway.domain.Endpoints;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsConnection;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsConnectionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoadConnections {

    private final EbicsConnectionRepository repository;

    @GetMapping(Endpoints.API_EBICS_CONNECTIONS)
    public List<EbicsConnection> getConnections() {
        return repository.findAllConnections();
    }
}
