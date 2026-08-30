\# Cloud ToDo API Architecture



\## 1. High-Level System Architecture



```text

\[ Client (Browser) ]

&#x20;       │

&#x20;       ▼

\[ AWS CloudFront (CDN) ] ───► \[ AWS S3 (Static Frontend: HTML/JS) ]

&#x20;       │

&#x20;       ▼

\[ AWS App Runner (Spring Boot API Container) ]

&#x20;  ├── Port: 8080

&#x20;  ├── Auth: X-API-Key Validation Filter

&#x20;  └── Logs: AWS CloudWatch

&#x20;       │

&#x20;       ▼

\[ Amazon RDS (PostgreSQL 17) ]

