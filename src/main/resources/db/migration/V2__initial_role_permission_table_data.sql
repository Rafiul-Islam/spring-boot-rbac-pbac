-- =====================================================
-- 1. POPULATE PERMISSIONS
-- =====================================================
INSERT INTO permissions (name) VALUES
                                   ('USER_CREATE'),
                                   ('USER_READ_All'),
                                   ('USER_READ_SINGLE'),
                                   ('USER_UPDATE'),
                                   ('USER_DELETE'),
                                   ('PRODUCT_CREATE'),
                                   ('PRODUCT_READ_All'),
                                   ('PRODUCT_READ_SINGLE'),
                                   ('PRODUCT_UPDATE'),
                                   ('PRODUCT_DELETE');

-- =====================================================
-- 2. POPULATE ROLES
-- =====================================================
INSERT INTO roles (name) VALUES
                             ('SUPER_ADMIN'),
                             ('ADMIN'),
                             ('EDITOR'),
                             ('MODERATOR'),
                             ('USER');

-- =====================================================
-- 3. POPULATE ROLES_PERMISSIONS
-- =====================================================

-- SUPER_ADMIN: Gets ALL permissions
INSERT IGNORE INTO roles_permissions (role_id, permission_id, isDefaultForRole)
SELECT r.id, p.id, TRUE
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'SUPER_ADMIN';

-- ADMIN: User CRUD (no delete) + Full Product CRUD
INSERT IGNORE INTO roles_permissions (role_id, permission_id, isDefaultForRole)
SELECT r.id, p.id, TRUE
FROM roles r
         JOIN permissions p ON p.name IN (
                                          'USER_CREATE', 'USER_READ_All', 'USER_READ_SINGLE', 'USER_UPDATE',
                                          'PRODUCT_CREATE', 'PRODUCT_READ_All', 'PRODUCT_READ_SINGLE', 'PRODUCT_UPDATE', 'PRODUCT_DELETE'
    )
WHERE r.name = 'ADMIN';

-- EDITOR: Full Product CRUD + Read single user profile
INSERT IGNORE INTO roles_permissions (role_id, permission_id, isDefaultForRole)
SELECT r.id, p.id, TRUE
FROM roles r
         JOIN permissions p ON p.name IN (
                                          'PRODUCT_CREATE', 'PRODUCT_READ_All', 'PRODUCT_READ_SINGLE', 'PRODUCT_UPDATE', 'PRODUCT_DELETE',
                                          'USER_READ_SINGLE'
    )
WHERE r.name = 'EDITOR';

-- MODERATOR: Read/Update products + Read single user profile
INSERT IGNORE INTO roles_permissions (role_id, permission_id, isDefaultForRole)
SELECT r.id, p.id, TRUE
FROM roles r
         JOIN permissions p ON p.name IN (
                                          'PRODUCT_READ_All', 'PRODUCT_READ_SINGLE', 'PRODUCT_UPDATE',
                                          'USER_READ_SINGLE'
    )
WHERE r.name = 'MODERATOR';

-- USER: Read products + Read single user profile
INSERT IGNORE INTO roles_permissions (role_id, permission_id, isDefaultForRole)
SELECT r.id, p.id, TRUE
FROM roles r
         JOIN permissions p ON p.name IN (
                                          'PRODUCT_READ_All', 'PRODUCT_READ_SINGLE',
                                          'USER_READ_SINGLE'
    )
WHERE r.name = 'USER';

-- =====================================================
-- 7. POPULATE ADDRESSES
-- =====================================================
INSERT INTO addresses (street, city, state, zip, user_id)
SELECT '100 Corporate Blvd', 'Tech City', 'CA', '94016', id FROM users WHERE email = 'superadmin@example.com';

INSERT INTO addresses (street, city, state, zip, user_id)
SELECT '200 HQ Way', 'San Francisco', 'CA', '94105', id FROM users WHERE email = 'admin@example.com';

INSERT INTO addresses (street, city, state, zip, user_id)
SELECT '456 Elm Street', 'Austin', 'TX', '73301', id FROM users WHERE email = 'alice@example.com';

INSERT INTO addresses (street, city, state, zip, user_id)
SELECT '789 Pine Street', 'Seattle', 'WA', '98101', id FROM users WHERE email = 'user-1@gmail.com';

INSERT INTO addresses (street, city, state, zip, user_id)
SELECT '321 Oak Avenue', 'Denver', 'CO', '80202', id FROM users WHERE email = 'user-2@gmail.com';

-- =====================================================
-- 8. POPULATE PRODUCTS
-- =====================================================
INSERT INTO products (name, description, price, quantity) VALUES
                                                              ('Wireless Mechanical Keyboard', 'RGB backlit mechanical keyboard with tactile switches.', 129.99, 50),
                                                              ('Ergonomic Mouse', 'Wireless ergonomic mouse with customizable side buttons.', 79.50, 120),
                                                              ('UltraWide Gaming Monitor', '34-inch curved QHD monitor with 144Hz refresh rate.', 499.00, 15),
                                                              ('USB-C Docking Station', '11-in-1 multi-port hub supporting dual 4K monitors.', 89.95, 200),
                                                              ('Noise Cancelling Headphones', 'Over-ear Bluetooth headphones with active noise isolation.', 249.99, 35);