#!/usr/bin/env bash
set -euo pipefail

SERVER_HOST="${SERVER_HOST:-106.54.168.35}"
SERVER_USER="${SERVER_USER:-root}"
SSH_ARGS=(-o StrictHostKeyChecking=accept-new)

if [ -z "${SSH_PASSWORD:-}" ] && command -v sshpass >/dev/null 2>&1; then
  read -r -s -p "SSH password for $SERVER_USER@$SERVER_HOST: " SSH_PASSWORD
  echo
fi

REMOTE_SCRIPT="$(cat <<'REMOTE'
set -e

echo "==> Java process"
ps -ef | grep '[j]ava' || true

PID="$(pgrep -f 'ai-learning-platform-backend.*jar' | head -n 1 || true)"
echo "PID=$PID"

if [ -n "$PID" ]; then
  echo "==> App env"
  tr '\0' '\n' < "/proc/$PID/environ" \
    | grep -E 'SPRING|MYSQL|SERVER|APP_' \
    | sed 's/MYSQL_PASSWORD=.*/MYSQL_PASSWORD=******/; s/APP_JWT_SECRET=.*/APP_JWT_SECRET=******/; s/APP_AES_KEY=.*/APP_AES_KEY=******/' || true
fi

ENV_FILE="/etc/ai-learning-platform/backend.env"
if [ ! -f "$ENV_FILE" ]; then
  echo "Missing env file: $ENV_FILE" >&2
  exit 1
fi

MYSQL_HOST="$(grep '^MYSQL_HOST=' "$ENV_FILE" | cut -d= -f2-)"
MYSQL_PORT="$(grep '^MYSQL_PORT=' "$ENV_FILE" | cut -d= -f2-)"
MYSQL_DATABASE="$(grep '^MYSQL_DATABASE=' "$ENV_FILE" | cut -d= -f2-)"
MYSQL_USERNAME="$(grep '^MYSQL_USERNAME=' "$ENV_FILE" | cut -d= -f2-)"
MYSQL_PASSWORD="$(grep '^MYSQL_PASSWORD=' "$ENV_FILE" | cut -d= -f2-)"

echo "==> Database check"
MYSQL_PWD="$MYSQL_PASSWORD" mysql \
  -h"$MYSQL_HOST" \
  -P"$MYSQL_PORT" \
  -u"$MYSQL_USERNAME" \
  "$MYSQL_DATABASE" <<'SQL'
SELECT DATABASE() AS db;
SELECT
  id,
  username,
  HEX(username) AS username_hex,
  real_name,
  account_status,
  HEX(account_status) AS status_hex,
  deleted_at,
  LEFT(password_hash, 30) AS hash_prefix
FROM users
WHERE username = 'admin';

SELECT COUNT(*) AS matched
FROM users
WHERE username = 'admin'
  AND account_status = 'active'
  AND deleted_at IS NULL;

SELECT u.id, u.username, GROUP_CONCAT(r.code) AS roles
FROM users u
LEFT JOIN user_roles ur ON ur.user_id = u.id
LEFT JOIN roles r ON r.id = ur.role_id
WHERE u.username = 'admin'
GROUP BY u.id, u.username;
SQL

echo "==> Try bootstrap-admin"
curl -i -X POST http://127.0.0.1:8080/api/v1/users/bootstrap-admin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","realName":"系统管理员","password":"123456","mustChangePassword":false}' || true

echo
echo "==> Try login"
curl -i -X POST http://127.0.0.1:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' || true

echo
echo "==> Recent logs"
journalctl -u ai-learning-platform-backend -n 80 --no-pager || true
REMOTE
)"

if command -v sshpass >/dev/null 2>&1 && [ -n "${SSH_PASSWORD:-}" ]; then
  sshpass -p "$SSH_PASSWORD" ssh "${SSH_ARGS[@]}" "$SERVER_USER@$SERVER_HOST" "bash -s" <<<"$REMOTE_SCRIPT"
else
  ssh "${SSH_ARGS[@]}" "$SERVER_USER@$SERVER_HOST" "bash -s" <<<"$REMOTE_SCRIPT"
fi
