-- 1. Crear extensión para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Insertar roles
INSERT INTO role(id, name) VALUES (uuid_generate_v4(), 'USER') ON CONFLICT DO NOTHING;
INSERT INTO role(id, name) VALUES (uuid_generate_v4(), 'ADMIN') ON CONFLICT DO NOTHING;

-- 3. Insertar usuario ADMIN si no existe
INSERT INTO user_data (
    id, username, first_name, last_name, dui, email, hashed_password, active
)
SELECT uuid_generate_v4(), 'adminuser', 'Admin', 'User', '12345678-9',
       'admin@example.com', '$2y$10$aKvjm7YNB.1ZWM6xSyRRsOzul9IA2FcjlOpbCQ3UPVWxRYPL1jM4S', true
    WHERE NOT EXISTS (
    SELECT 1 FROM user_data WHERE username = 'adminuser'
);

-- 4. Insertar relación entre usuario y rol si no existe
INSERT INTO user_x_role (user_id, role_id)
SELECT u.id, r.id
FROM user_data u, role r
WHERE u.username = 'adminuser' AND r.name = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM user_x_role WHERE user_id = u.id AND role_id = r.id
);

-- 5. Insertar cuenta por defecto asociada al usuario ADMIN
INSERT INTO account (
    id, number, type, balance, currency, created_at, user_id
)
SELECT uuid_generate_v4(), '0001-0001-0001', 'CORRIENTE', 1000.00, 'USD', NOW(), u.id
FROM user_data u
WHERE u.username = 'adminuser'
  AND NOT EXISTS (
    SELECT 1 FROM account a WHERE a.user_id = u.id
);
