package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractDetails;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetContractDetails {
    
    private final ContractWorkflowRepository repository;
    
    @GetMapping("/api/workflows/contracts/{contractId}")
    public ContractDetails getContractDetails(@PathVariable Long contractId) {
        return repository.findContractDetails(contractId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("contractId", contractId));
    }
}
