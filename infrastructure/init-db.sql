USE master;
GO

CREATE DATABASE ebics_gateway;
GO

USE ebics_gateway;
GO

CREATE LOGIN gateway_user WITH PASSWORD = 'mWdLu7mV89Z91';
GO

CREATE USER gateway_user FOR LOGIN gateway_user;
GO

ALTER ROLE db_owner ADD MEMBER gateway_user;
GO
