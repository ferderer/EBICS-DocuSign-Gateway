create table ebics_connections (
  id              bigint primary key,
  bank_name       nvarchar(255)  not null,
  host_id         nvarchar(50)   not null unique,
  partner_id      nvarchar(50)   not null,
  user_id         nvarchar(50)   not null,
  bank_url        nvarchar(500)  not null,
  version         nvarchar(10)   default 'H004',
  status          nvarchar(20)   default 'INACTIVE',
  last_connected  datetime2,
  created_at      datetime2 default getdate()
);
