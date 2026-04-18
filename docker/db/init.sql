-- USUARIOS
CREATE TABLE IF NOT EXISTS users
(
    id
    SERIAL
    PRIMARY
    KEY,
    email
    VARCHAR
(
    255
) UNIQUE NOT NULL,
    password VARCHAR
(
    255
) NOT NULL
    );

-- ORDENES
CREATE TABLE IF NOT EXISTS orders
(
    id
    SERIAL
    PRIMARY
    KEY,
    user_id
    INT
    REFERENCES
    users
(
    id
),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- ITEMS DE ORDEN
CREATE TABLE IF NOT EXISTS order_items
(
    id
    SERIAL
    PRIMARY
    KEY,
    order_id
    INT
    REFERENCES
    orders
(
    id
),
    product_name VARCHAR
(
    255
),
    quantity INT
    );

-- USUARIO SEMILLA PARA AUTOMATIZACIÓN
INSERT INTO users (email, password)
VALUES ('testuser@mail.com', 'Password123') ON CONFLICT (email) DO NOTHING;