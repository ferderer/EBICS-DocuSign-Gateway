create table payments (
  id                bigint         primary key,
  transaction_id    nvarchar(100)  not null unique,
  received_at       datetime2      not null,
  value_date        date,
  booking_date      date,
  amount            nvarchar(50)   not null,
  currency          nvarchar(3)    not null,
  debtor_name       nvarchar(255)  not null,
  debtor_account    nvarchar(50),
  creditor_name     nvarchar(255),
  creditor_account  nvarchar(50),
  remittance_info   nvarchar(500),
  end_to_end_id     nvarchar(100),
  status            nvarchar(20)   default 'received',
  connection_id     bigint         not null,
  created_at        datetime2      default getdate(),
  foreign key (connection_id) references ebics_connections(id)
);