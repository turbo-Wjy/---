USE ai_learning_platform;

-- =========================================================
-- 开发/演示环境系统管理员账号
-- 用户名：admin
-- 密码：1234
-- 说明：本脚本用于首个管理员账号自举；正式环境请改用部署侧密钥或重置密码流程。
-- =========================================================

SET @admin_password_hash = '$2y$10$Dz/ddDx5rjJD67yRVG23eOEP7CfQMaOdQxjwaUz.padlgomJ4Syle';

INSERT INTO users (username, password_hash, real_name, account_status, must_change_password)
VALUES ('admin', @admin_password_hash, '系统管理员', 'active', 0)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  real_name = VALUES(real_name),
  account_status = VALUES(account_status),
  must_change_password = VALUES(must_change_password),
  updated_at = CURRENT_TIMESTAMP;

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.code = 'admin'
WHERE u.username = 'admin';
