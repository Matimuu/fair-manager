CREATE SEQUENCE IF NOT EXISTS product_id_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE products
(
    id          BIGINT                   NOT NULL,
    sku         UUID                     NOT NULL,
    label       VARCHAR(240)             NOT NULL,
    description TEXT,
    category    VARCHAR(120)             NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    version     BIGINT                   NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

ALTER TABLE products
    ADD CONSTRAINT uc_products_sku UNIQUE (sku);

ALTER TABLE products
    ADD CONSTRAINT uc_products_label UNIQUE (label);