create table docusign_envelopes (
  id                bigint         primary key,
  envelope_id       nvarchar(100)  not null unique,
  template_id       bigint         not null,
  signer_email      nvarchar(255)  not null,
  signer_name       nvarchar(255)  not null,
  status            nvarchar(20)   default 'created',
  payment_id        bigint,
  created_at        datetime2      default getdate(),
  sent_at           datetime2,
  completed_at      datetime2,
  foreign key (template_id) references docusign_templates(id),
  foreign key (payment_id) references payments(id)
);
