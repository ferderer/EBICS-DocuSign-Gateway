package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowInfo;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoadContracts {
    
    private final ContractWorkflowRepository repository;
    
    @GetMapping("/api/workflows/contracts")
    public List<ContractWorkflowInfo> getContracts(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "50") int limit) {
        
        if ("ALL".equals(status)) {
            return repository.findAllContracts(limit);
        } else {
            return repository.findContractsByStatus(status, limit);
        }
    }
}
