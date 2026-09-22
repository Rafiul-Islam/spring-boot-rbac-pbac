-- =====================================================
-- 1. CREATE SUPER_ADMIN USER
-- =====================================================
-- Password: Password1234* (BCrypt hash)
-- Generate BCrypt hash at: https://bcrypt-generator.com/
INSERT INTO users (name, email, password) VALUES
('Super Admin - 1', 'superadmin-1@gmail.com', '$2a$12$SiTzyNDsNuXYa/.MR76n0udDXirqzQIgRS6IShoHnYKPItltalYke');

-- =====================================================
-- 2. ASSIGN SUPER_ADMIN AND USER ROLES TO SUPER_ADMIN USER
-- =====================================================
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name IN ('SUPER_ADMIN', 'USER')
WHERE u.email = 'superadmin-1@gmail.com';

-- =====================================================
-- 2.5 ENSURE ALL EXISTING USERS HAVE USER ROLE
-- =====================================================
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'USER'
WHERE NOT EXISTS (
  SELECT 1 FROM user_roles ur
  WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- =====================================================
-- 3. ASSIGN ALL SUPER_ADMIN PERMISSIONS TO SUPER_ADMIN USER
-- =====================================================
INSERT IGNORE INTO user_permissions (user_id, permission_id)
SELECT u.id, rp.permission_id
FROM users u
JOIN roles_permissions rp ON TRUE
JOIN roles r ON rp.role_id = r.id
WHERE u.email = 'superadmin-1@gmail.com'
  AND r.name = 'SUPER_ADMIN';
