package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignTemplateInfo;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignTemplateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoadTemplates {
    
    private final DocuSignTemplateRepository repository;
    
    @GetMapping("/api/docusign/templates")
    public List<DocuSignTemplateInfo> getTemplates() {
        return repository.findAllTemplates();
    }
}
