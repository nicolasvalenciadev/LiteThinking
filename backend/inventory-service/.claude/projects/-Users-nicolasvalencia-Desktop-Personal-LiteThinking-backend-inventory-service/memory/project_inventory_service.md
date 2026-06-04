---
name: project-inventory-service
description: Core context for the LiteThinking inventory-service microservice — architecture, build tooling, and key decisions
metadata:
  type: project
---

inventory-service is a Spring Boot 3.3.4 / Java 17 microservice (port 8084) that reads products/prices from PostgreSQL, fetches company data from company-service (port 8082 via WebClient), generates PDF reports with JasperReports 6.21.0, and sends them via JavaMail SMTP Gmail.

**Why:** Part of the LiteThinking platform; sits behind an API Gateway that injects X-User-Role / X-User-Id / X-Username headers. No JWT validation — gateway handles auth.

**Architecture:** Hexagonal (Ports & Adapters). Domain has zero Spring/JPA imports. All adapters in infrastructure layer. DTOs never expose domain models.

**Build constraint:** Maven must be run with `JAVA_HOME=/Users/nicolasvalencia/Library/Java/JavaVirtualMachines/corretto-17.0.18/Contents/Home` — the system Maven default is Java 26 (Homebrew), which breaks Lombok 1.18.36 (TypeTag error).

**Key decisions:**
- `jasperreports-pdf` does not exist as a standalone Maven artifact in 6.x — PDF export is in the core `jasperreports` jar.
- `InventoryService.getInventory()` wraps `fetchProducts()` result in `new ArrayList<>()` before sorting (avoid UnsupportedOperationException on immutable lists).
- `@WebMvcTest` controller tests import `SecurityConfig.class` explicitly so Spring Security permits all and doesn't return 401.
- `ddl-auto: validate` — tables already exist, never recreate.
- PDF saved to `System.getProperty("java.io.tmpdir")/inventory_report.pdf` before email attachment.

**How to apply:** Reference these decisions when modifying build config, adding tests, or extending the report/email flow.
