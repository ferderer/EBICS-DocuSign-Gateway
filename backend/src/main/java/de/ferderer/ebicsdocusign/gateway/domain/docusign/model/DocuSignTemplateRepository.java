package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocuSignTemplateRepository extends CrudRepository<DocuSignTemplateEntity, Long> {

    @Query("SELECT new de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignTemplateInfo(" +
           "t.id, t.templateId, t.templateName, t.description, CAST(t.status AS string), " +
           "t.documentName, t.pageCount, t.createdAt, t.lastUsed) " +
           "FROM DocuSignTemplate t WHERE t.status = 'ACTIVE' ORDER BY t.templateName")
    List<DocuSignTemplateInfo> findAllTemplates();
}
