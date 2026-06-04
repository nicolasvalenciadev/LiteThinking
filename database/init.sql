-- ============================================================
-- LiteThinking Database
-- init.sql — DDL completo con auditoría y soft delete
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- ROLES
-- ============================================================
CREATE TABLE roles (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(255) NOT NULL UNIQUE,
    created_date  TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_update   TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted       BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ============================================================
-- CLIENTS
-- ============================================================
CREATE TABLE clients (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(255) NOT NULL,
    lastname       VARCHAR(255) NOT NULL,
    email          VARCHAR(255),
    identification VARCHAR(20)  NOT NULL UNIQUE,
    telephone      VARCHAR(20),
    created_date   TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_update    TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted        BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username     VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role_id      UUID         NOT NULL,
    client_id    UUID,
    created_date TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_roles_id_users    FOREIGN KEY (role_id)   REFERENCES roles(id),
    CONSTRAINT fk_clientes_id_users FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- ============================================================
-- COMPANIES
-- ============================================================
CREATE TABLE companies (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    nit          VARCHAR(255) NOT NULL UNIQUE,
    address      VARCHAR(255),
    telephone    VARCHAR(20),
    created_date TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ============================================================
-- PRODUCTS
-- ============================================================
CREATE TABLE products (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code         VARCHAR(255) NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255),
    company_id   UUID         NOT NULL,
    created_date TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_companies_id_products FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- ============================================================
-- PRODUCT PRICES (multi-currency)
-- ============================================================
CREATE TABLE product_prices (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id   UUID           NOT NULL,
    currency     VARCHAR(10)    NOT NULL,
    price        DECIMAL(19, 4) NOT NULL,
    created_date TIMESTAMP      NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP      NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN        NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_product_prices_product_id_products FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uq_product_currency UNIQUE (product_id, currency)
);

-- ============================================================
-- CATEGORIES
-- ============================================================
CREATE TABLE categories (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL UNIQUE,
    created_date TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ============================================================
-- CATEGORY_PRODUCT (M:N productos ↔ categorías)
-- ============================================================
CREATE TABLE category_product (
    id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    id_category UUID      NOT NULL,
    id_product  UUID      NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN   NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_category_product_id_category_categories FOREIGN KEY (id_category) REFERENCES categories(id),
    CONSTRAINT fk_category_product_id_product_products    FOREIGN KEY (id_product)  REFERENCES products(id),
    CONSTRAINT uq_category_product UNIQUE (id_category, id_product)
);

-- ============================================================
-- STATUS (estados de orden)
-- ============================================================
CREATE TABLE status (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL UNIQUE,
    created_date TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ============================================================
-- ORDERS
-- ============================================================
CREATE TABLE orders (
    id           UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id    UUID           NOT NULL,
    status_id    UUID           NOT NULL,
    total        DECIMAL(19, 4) NOT NULL DEFAULT 0,
    created_date TIMESTAMP      NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP      NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN        NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_orders_client_id_clients FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_orders_status_id_status  FOREIGN KEY (status_id) REFERENCES status(id)
);

-- ============================================================
-- ORDER_PRODUCT (M:N órdenes ↔ productos)
-- ============================================================
CREATE TABLE order_product (
    id           UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id     UUID           NOT NULL,
    product_id   UUID           NOT NULL,
    quantity     INTEGER        NOT NULL DEFAULT 1,
    unit_price   DECIMAL(19, 4) NOT NULL,
    created_date TIMESTAMP      NOT NULL DEFAULT NOW(),
    last_update  TIMESTAMP      NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN        NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_order_product_order_id_orders     FOREIGN KEY (order_id)   REFERENCES orders(id),
    CONSTRAINT fk_order_product_product_id_products FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================
-- INDEXES — performance en consultas frecuentes
-- ============================================================
CREATE INDEX idx_users_username       ON users(username)         WHERE deleted = FALSE;
CREATE INDEX idx_users_role_id        ON users(role_id)          WHERE deleted = FALSE;
CREATE INDEX idx_users_client_id      ON users(client_id)        WHERE deleted = FALSE;
CREATE INDEX idx_products_company_id  ON products(company_id)    WHERE deleted = FALSE;
CREATE INDEX idx_products_code        ON products(code)          WHERE deleted = FALSE;
CREATE INDEX idx_product_prices_product_id ON product_prices(product_id) WHERE deleted = FALSE;
CREATE INDEX idx_orders_client_id     ON orders(client_id)       WHERE deleted = FALSE;
CREATE INDEX idx_orders_status_id     ON orders(status_id)       WHERE deleted = FALSE;
CREATE INDEX idx_order_product_order_id   ON order_product(order_id)   WHERE deleted = FALSE;
CREATE INDEX idx_order_product_product_id ON order_product(product_id) WHERE deleted = FALSE;
CREATE INDEX idx_category_product_category ON category_product(id_category) WHERE deleted = FALSE;
CREATE INDEX idx_category_product_product  ON category_product(id_product)  WHERE deleted = FALSE;

-- ============================================================
-- TRIGGER — auto update last_update en cada UPDATE
-- ============================================================
CREATE OR REPLACE FUNCTION update_last_update_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_update = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_roles_last_update            BEFORE UPDATE ON roles            FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_clients_last_update          BEFORE UPDATE ON clients          FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_users_last_update            BEFORE UPDATE ON users            FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_companies_last_update        BEFORE UPDATE ON companies        FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_products_last_update         BEFORE UPDATE ON products         FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_product_prices_last_update   BEFORE UPDATE ON product_prices   FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_categories_last_update       BEFORE UPDATE ON categories       FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_category_product_last_update BEFORE UPDATE ON category_product FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_status_last_update           BEFORE UPDATE ON status           FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_orders_last_update           BEFORE UPDATE ON orders           FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
CREATE TRIGGER trg_order_product_last_update    BEFORE UPDATE ON order_product    FOR EACH ROW EXECUTE FUNCTION update_last_update_column();
