create table docusign_templates (
  id                bigint        primary key,
  template_id       nvarchar(100) not null unique,
  template_name     nvarchar(255) not null,
  description       nvarchar(500),
  status            nvarchar(20)  default 'active',
  document_name     nvarchar(255) not null,
  page_count        int,
  field_mappings    text,
  created_at        datetime2     default getdate(),
  last_used         datetime2
);
