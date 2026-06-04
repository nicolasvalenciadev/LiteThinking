-- ============================================================
-- LiteThinking Database
-- seed.sql — Datos iniciales del sistema
-- ============================================================

-- ============================================================
-- ROLES
-- ============================================================
INSERT INTO roles (id, name, created_date, last_update, deleted) VALUES
    ('a1b2c3d4-0001-0001-0001-000000000001', 'ADMIN',    NOW(), NOW(), FALSE),
    ('a1b2c3d4-0001-0001-0001-000000000002', 'EXTERNAL', NOW(), NOW(), FALSE);

-- ============================================================
-- STATUS
-- ============================================================
INSERT INTO status (id, name, created_date, last_update, deleted) VALUES
    ('b2c3d4e5-0002-0002-0002-000000000001', 'PENDING',   NOW(), NOW(), FALSE),
    ('b2c3d4e5-0002-0002-0002-000000000002', 'CONFIRMED', NOW(), NOW(), FALSE),
    ('b2c3d4e5-0002-0002-0002-000000000003', 'DELIVERED', NOW(), NOW(), FALSE),
    ('b2c3d4e5-0002-0002-0002-000000000004', 'CANCELLED', NOW(), NOW(), FALSE);

-- ============================================================
-- USERS
-- admin     / Admin123!
-- external  / External123!
-- Passwords encriptadas con BCrypt (strength 10)
-- ============================================================
INSERT INTO users (id, username, password, role_id, client_id, created_date, last_update, deleted) VALUES
    (
        'c3d4e5f6-0003-0003-0003-000000000001',
        'admin',
        '$2b$10$WdbJ17DaivS8QNRVDhsJEeKieSjGlAy0fTU028eh2rdq4VAOdkbh.',
        'a1b2c3d4-0001-0001-0001-000000000001',
        NULL,
        NOW(), NOW(), FALSE
    ),
    (
        'c3d4e5f6-0003-0003-0003-000000000002',
        'external',
        '$2b$10$z1lLCNOwyT7vXlKzF240qOFe8OHB1jSumxedWbYD008wYpYB3niba',
        'a1b2c3d4-0001-0001-0001-000000000002',
        NULL,
        NOW(), NOW(), FALSE
    );

-- ============================================================
-- COMPANIES (datos de ejemplo)
-- ============================================================
INSERT INTO companies (id, name, nit, address, telephone, created_date, last_update, deleted) VALUES
    (
        'd4e5f6a7-0004-0004-0004-000000000001',
        'LiteThinking S.A.S',
        '900123456-1',
        'Calle 100 # 15-20, Bogotá',
        '+57 601 123 4567',
        NOW(), NOW(), FALSE
    ),
    (
        'd4e5f6a7-0004-0004-0004-000000000002',
        'Tech Solutions Colombia',
        '800987654-2',
        'Carrera 7 # 32-10, Medellín',
        '+57 604 987 6543',
        NOW(), NOW(), FALSE
    );

-- ============================================================
-- CATEGORIES
-- ============================================================
INSERT INTO categories (id, name, created_date, last_update, deleted) VALUES
    ('e5f6a7b8-0005-0005-0005-000000000001', 'Electrónica',   NOW(), NOW(), FALSE),
    ('e5f6a7b8-0005-0005-0005-000000000002', 'Software',      NOW(), NOW(), FALSE),
    ('e5f6a7b8-0005-0005-0005-000000000003', 'Hardware',      NOW(), NOW(), FALSE),
    ('e5f6a7b8-0005-0005-0005-000000000004', 'Servicios',     NOW(), NOW(), FALSE),
    ('e5f6a7b8-0005-0005-0005-000000000005', 'Accesorios',    NOW(), NOW(), FALSE);

-- ============================================================
-- PRODUCTS
-- ============================================================
INSERT INTO products (id, code, name, description, company_id, created_date, last_update, deleted) VALUES
    (
        'f6a7b8c9-0006-0006-0006-000000000001',
        'PROD-001',
        'Laptop Empresarial',
        'Laptop de alto rendimiento para uso empresarial',
        'd4e5f6a7-0004-0004-0004-000000000001',
        NOW(), NOW(), FALSE
    ),
    (
        'f6a7b8c9-0006-0006-0006-000000000002',
        'PROD-002',
        'Licencia Software ERP',
        'Licencia anual para sistema ERP empresarial',
        'd4e5f6a7-0004-0004-0004-000000000001',
        NOW(), NOW(), FALSE
    ),
    (
        'f6a7b8c9-0006-0006-0006-000000000003',
        'PROD-003',
        'Mouse Inalámbrico',
        'Mouse ergonómico inalámbrico con receptor USB',
        'd4e5f6a7-0004-0004-0004-000000000002',
        NOW(), NOW(), FALSE
    );

-- ============================================================
-- PRODUCT PRICES (multi-currency: COP, USD, EUR)
-- ============================================================
INSERT INTO product_prices (id, product_id, currency, price, created_date, last_update, deleted) VALUES
    -- Laptop
    ('a7b8c9d0-0007-0007-0007-000000000001', 'f6a7b8c9-0006-0006-0006-000000000001', 'COP', 4500000.0000, NOW(), NOW(), FALSE),
    ('a7b8c9d0-0007-0007-0007-000000000002', 'f6a7b8c9-0006-0006-0006-000000000001', 'USD',    1100.0000, NOW(), NOW(), FALSE),
    ('a7b8c9d0-0007-0007-0007-000000000003', 'f6a7b8c9-0006-0006-0006-000000000001', 'EUR',    1020.0000, NOW(), NOW(), FALSE),
    -- Licencia ERP
    ('a7b8c9d0-0007-0007-0007-000000000004', 'f6a7b8c9-0006-0006-0006-000000000002', 'COP', 1200000.0000, NOW(), NOW(), FALSE),
    ('a7b8c9d0-0007-0007-0007-000000000005', 'f6a7b8c9-0006-0006-0006-000000000002', 'USD',     290.0000, NOW(), NOW(), FALSE),
    ('a7b8c9d0-0007-0007-0007-000000000006', 'f6a7b8c9-0006-0006-0006-000000000002', 'EUR',     270.0000, NOW(), NOW(), FALSE),
    -- Mouse
    ('a7b8c9d0-0007-0007-0007-000000000007', 'f6a7b8c9-0006-0006-0006-000000000003', 'COP',   85000.0000, NOW(), NOW(), FALSE),
    ('a7b8c9d0-0007-0007-0007-000000000008', 'f6a7b8c9-0006-0006-0006-000000000003', 'USD',      21.0000, NOW(), NOW(), FALSE),
    ('a7b8c9d0-0007-0007-0007-000000000009', 'f6a7b8c9-0006-0006-0006-000000000003', 'EUR',      19.0000, NOW(), NOW(), FALSE);

-- ============================================================
-- CATEGORY_PRODUCT
-- ============================================================
INSERT INTO category_product (id, id_category, id_product, created_date, last_update, deleted) VALUES
    ('b8c9d0e1-0008-0008-0008-000000000001', 'e5f6a7b8-0005-0005-0005-000000000001', 'f6a7b8c9-0006-0006-0006-000000000001', NOW(), NOW(), FALSE),
    ('b8c9d0e1-0008-0008-0008-000000000002', 'e5f6a7b8-0005-0005-0005-000000000003', 'f6a7b8c9-0006-0006-0006-000000000001', NOW(), NOW(), FALSE),
    ('b8c9d0e1-0008-0008-0008-000000000003', 'e5f6a7b8-0005-0005-0005-000000000002', 'f6a7b8c9-0006-0006-0006-000000000002', NOW(), NOW(), FALSE),
    ('b8c9d0e1-0008-0008-0008-000000000004', 'e5f6a7b8-0005-0005-0005-000000000004', 'f6a7b8c9-0006-0006-0006-000000000002', NOW(), NOW(), FALSE),
    ('b8c9d0e1-0008-0008-0008-000000000005', 'e5f6a7b8-0005-0005-0005-000000000005', 'f6a7b8c9-0006-0006-0006-000000000003', NOW(), NOW(), FALSE);
