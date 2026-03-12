CREATE SEQUENCE IF NOT EXISTS category_id_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE categories
(
    id         BIGINT                   NOT NULL,
    name       VARCHAR(120)             NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version    BIGINT                   NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

ALTER TABLE products
    ADD category_id BIGINT;

ALTER TABLE categories
    ADD CONSTRAINT uc_categories_name UNIQUE (name);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE products
    DROP COLUMN category;