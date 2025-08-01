package de.ferderer.ebicsdocusign.gateway.domain.dashboard.api;

import de.ferderer.ebicsdocusign.gateway.domain.dashboard.model.SystemHealthResponse;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.ConnectionStatus;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsConnectionRepository;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CheckSystemHealth {
    
    private final DataSource dataSource;
    private final EbicsConnectionRepository ebicsRepository;
    
    @GetMapping("/api/dashboard/health")
    public SystemHealthResponse checkSystemHealth() {
        LocalDateTime checkedAt = LocalDateTime.now();
        
        // Check database connectivity
        boolean databaseHealthy = checkDatabaseHealth();
        
        // Check EBICS connections
        long activeConnections = ebicsRepository.countByStatus(ConnectionStatus.ACTIVE);
        long totalConnections = ebicsRepository.count();
        boolean ebicsHealthy = activeConnections > 0;
        
        // Overall system status
        String overallStatus = (databaseHealthy && ebicsHealthy) ? "HEALTHY" : 
                              databaseHealthy ? "DEGRADED" : "UNHEALTHY";
        
        return new SystemHealthResponse(
            overallStatus,
            databaseHealthy,
            ebicsHealthy,
            activeConnections,
            totalConnections,
            checkedAt
        );
    }
    
    private boolean checkDatabaseHealth() {
        try {
            dataSource.getConnection().close();
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }
}
