package de.ferderer.ebicsdocusign.gateway.domain.ebics.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.ConnectionStatus;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsConnectionEntity;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsConnectionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestConnection {
    
    private final EbicsConnectionRepository repository;
    
    public record TestConnectionRequest(
        @NotNull Long connectionId
    ) {}
    
    public record TestConnectionResponse(
        Long connectionId,
        String bankName,
        String hostId,
        boolean success,
        String message,
        LocalDateTime testedAt
    ) {}
    
    @PostMapping("/api/ebics/test-connection")
    public TestConnectionResponse testConnection(@Valid @RequestBody TestConnectionRequest request) {
        var connection = repository.findById(request.connectionId())
            .orElseThrow(() -> new AppException(ErrorCode.E_NOT_FOUND, "connectionId", request.connectionId()));
        
        LocalDateTime testedAt = LocalDateTime.now();
        try {
            // TODO: Implement actual EBICS connection test
            boolean connectionSuccessful = performEbicsConnectionTest(connection);
            
            if (connectionSuccessful) {
                connection.setStatus(ConnectionStatus.ACTIVE);
                connection.setLastConnected(testedAt);
                repository.save(connection);
                
                return new TestConnectionResponse(connection.getId(), connection.getBankName(),
                    connection.getHostId(), true, "Connection successful", testedAt);
            }
            else {
                connection.setStatus(ConnectionStatus.ERROR);
                repository.save(connection);
                
                return new TestConnectionResponse(connection.getId(), connection.getBankName(),
                    connection.getHostId(), false, "Connection failed: Unable to connect to EBICS server", testedAt);
            }
        }
        catch (Exception e) {
            connection.setStatus(ConnectionStatus.ERROR);
            repository.save(connection);
            
            return new TestConnectionResponse(connection.getId(), connection.getBankName(),
                connection.getHostId(), false, "Connection failed: " + e.getMessage(), testedAt);
        }
    }
    
    private boolean performEbicsConnectionTest(EbicsConnectionEntity connection) {
        // Placeholder for actual EBICS connection test
        // For now, simulate success for ACTIVE connections, failure for others
        return !ConnectionStatus.ERROR.equals(connection.getStatus());
    }
}
