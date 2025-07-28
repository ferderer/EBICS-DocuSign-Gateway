# EBICS DocuSign Gateway - API Endpoints Reference

## EBICS Management
| Endpoint | Method | Controller | Description |
|----------|--------|------------|-------------|
| `/api/ebics/connections` | GET | LoadConnections | List configured EBICS bank connections |
| `/api/ebics/test-connection` | POST | TestConnection | Test connectivity to EBICS bank server |
| `/api/ebics/certificates` | POST | UploadCertificates | Upload bank certificates and client keys |
| `/api/ebics/download-statements` | POST | DownloadStatements | Download bank statements via EBICS |
| `/api/ebics/process-payments` | POST | ProcessPayments | Submit payment orders to bank |
| `/api/ebics/payments/{paymentId}/status` | GET | GetPaymentStatus | Check status of submitted payment |

## DocuSign Integration
| Endpoint | Method | Controller | Description |
|----------|--------|------------|-------------|
| `/api/docusign/templates` | GET | LoadTemplates | List available DocuSign templates |
| `/api/docusign/templates/upload` | POST | UploadTemplate | Upload new contract template |
| `/api/docusign/envelopes` | POST | CreateEnvelope | Create signing envelope from template |
| `/api/docusign/envelopes/{envelopeId}/status` | GET | GetEnvelopeStatus | Check envelope signing status |
| `/api/docusign/envelopes/{envelopeId}/signing-url` | GET | GetSigningUrl | Get embedded signing URL |
| `/api/docusign/webhook` | POST | DocuSignWebhook | Handle DocuSign status callbacks |

## Contract Workflow Management
| Endpoint | Method | Controller | Description |
|----------|--------|------------|-------------|
| `/api/workflows/contracts` | GET | LoadContracts | List all contract workflows |
| `/api/workflows/contracts/create` | POST | CreateContract | Manually create new contract workflow |
| `/api/workflows/contracts/{contractId}` | GET | GetContractDetails | Get detailed contract information |
| `/api/workflows/contracts/{contractId}/status` | PUT | UpdateContractStatus | Update contract workflow status |
| `/api/workflows/contracts/{contractId}/retry` | POST | RetryContract | Retry failed contract processing |
| `/api/workflows/contracts/{contractId}/cancel` | DELETE | CancelContract | Cancel pending contract workflow |

## Template Management
| Endpoint | Method | Controller | Description |
|----------|--------|------------|-------------|
| `/api/templates` | GET | LoadTemplates | List contract templates |
| `/api/templates` | POST | CreateTemplate | Create new template configuration |
| `/api/templates/{templateId}` | PUT | UpdateTemplate | Update template settings |
| `/api/templates/{templateId}` | DELETE | DeleteTemplate | Delete template |
| `/api/templates/{templateId}/preview` | GET | PreviewTemplate | Preview template with sample data |
| `/api/templates/{templateId}/mappings` | GET/PUT | ManageFieldMapping | Manage payment-to-template field mappings |

## Payment Processing
| Endpoint | Method | Controller | Description |
|----------|--------|------------|-------------|
| `/api/payments/incoming` | GET | LoadIncomingPayments | List recent incoming payments |
| `/api/payments/{paymentId}` | GET | GetPaymentDetails | Get detailed payment information |
| `/api/payments/{paymentId}/process` | POST | ProcessPaymentManual | Manually trigger contract workflow |
| `/api/payments/history` | GET | LoadPaymentHistory | Get payment processing history |
| `/api/payments/search` | GET | SearchPayments | Search payments by criteria |

## Dashboard & Monitoring
| Endpoint | Method | Controller | Description |
|----------|--------|------------|-------------|
| `/api/dashboard/overview` | GET | LoadDashboardOverview | Get dashboard summary statistics |
| `/api/dashboard/recent-activity` | GET | LoadRecentActivity | Get recent system activity feed |
| `/api/dashboard/metrics` | GET | LoadMetrics | Get performance and usage metrics |
| `/api/dashboard/health` | GET | CheckSystemHealth | System health and connectivity status |

## Audit & Compliance
| Endpoint | Method | Controller | Description |
|----------|--------|------------|-------------|
| `/api/audit/logs` | GET | LoadAuditLogs | Get system audit trail |
| `/api/audit/export` | GET | ExportAuditData | Export audit data for compliance |
| `/api/audit/signatures/{documentId}/verify` | POST | VerifySignatures | Verify digital signature validity |
| `/api/audit/compliance-reports` | GET | GenerateComplianceReport | Generate compliance reports |

## System Configuration
| Endpoint | Method | Controller | Description |
|----------|--------|------------|-------------|
| `/api/config/system` | GET | LoadSystemConfig | Get system configuration |
| `/api/config/business-rules` | PUT | UpdateBusinessRules | Update payment processing rules |
| `/api/config/notifications` | GET/PUT | ManageNotifications | Manage notification settings |
| `/api/config/backup` | POST | BackupSystem | Trigger system backup |

## WebSocket Endpoints
| Endpoint | Type | Broadcaster | Description |
|----------|------|-------------|-------------|
| `/ws/payments` | WebSocket | PaymentUpdatesBroadcaster | Real-time payment status updates |
| `/ws/contracts` | WebSocket | ContractUpdatesBroadcaster | Real-time contract workflow updates |
| `/ws/notifications` | WebSocket | SystemNotificationsBroadcaster | System alerts and notifications |

## Authentication
All REST endpoints require JWT authentication header: `Authorization: Bearer <token>`
WebSocket connections authenticate via query parameter: `?token=<jwt_token>`