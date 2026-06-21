#!/usr/bin/env bash
set -euo pipefail

ARCHIVE_PATH="${1:?Usage: server-deploy-backend.sh ARCHIVE_PATH REMOTE_DIR}"
APP_BASE_DIR="${2:-/opt/ai-learning-platform}"
APP_NAME="ai-learning-platform-backend"
SERVICE_NAME="ai-learning-platform"
DB_NAME="${MYSQL_DATABASE:-ai_learning_platform}"
DB_USER="${MYSQL_USERNAME:-root}"
APP_PORT="${APP_PORT:-8080}"
ENV_DIR="/etc/ai-learning-platform"
ENV_FILE="$ENV_DIR/backend.env"
RELEASE_DIR="$APP_BASE_DIR/releases/$(date +%Y%m%d%H%M%S)"
CURRENT_DIR="$APP_BASE_DIR/current"
LOG_DIR="/var/log/ai-learning-platform"

echo "==> Preparing directories"
mkdir -p "$APP_BASE_DIR/releases" "$LOG_DIR" "$ENV_DIR"
chmod 755 "$APP_BASE_DIR" "$APP_BASE_DIR/releases" "$LOG_DIR"

echo "==> Extracting release to $RELEASE_DIR"
mkdir -p "$RELEASE_DIR"
tar -xzf "$ARCHIVE_PATH" -C "$RELEASE_DIR"
ln -sfn "$RELEASE_DIR" "$CURRENT_DIR"

echo "==> Installing Maven if missing"
if ! command -v mvn >/dev/null 2>&1; then
  apt-get update
  apt-get install -y maven
fi

echo "==> Writing environment file: $ENV_FILE"
if [ ! -f "$ENV_FILE" ]; then
  JWT_SECRET="$(tr -dc 'A-Za-z0-9' </dev/urandom | head -c 48 || true)"
  AES_KEY="$(tr -dc 'A-Za-z0-9' </dev/urandom | head -c 32 || true)"
  DB_PASSWORD="${MYSQL_PASSWORD:-}"
else
  JWT_SECRET="$(grep '^APP_JWT_SECRET=' "$ENV_FILE" | cut -d= -f2- || true)"
  AES_KEY="$(grep '^APP_AES_KEY=' "$ENV_FILE" | cut -d= -f2- || true)"
  DB_NAME="$(grep '^MYSQL_DATABASE=' "$ENV_FILE" | cut -d= -f2- || true)"
  DB_USER="$(grep '^MYSQL_USERNAME=' "$ENV_FILE" | cut -d= -f2- || true)"
  DB_PASSWORD="$(grep '^MYSQL_PASSWORD=' "$ENV_FILE" | cut -d= -f2- || true)"
fi
DB_NAME="${DB_NAME:-ai_learning_platform}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
JWT_SECRET="${JWT_SECRET:-change-this-prod-secret-change-this-prod-secret}"
AES_KEY="${AES_KEY:-change-this-32-byte-prod-key-0001}"

if [ -z "$DB_PASSWORD" ]; then
  read -r -s -p "MySQL password for $DB_USER: " DB_PASSWORD
  echo
fi

cat > "$ENV_FILE" <<EOF
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=$APP_PORT
MYSQL_HOST=127.0.0.1
MYSQL_PORT=3306
MYSQL_DATABASE=$DB_NAME
MYSQL_USERNAME=$DB_USER
MYSQL_PASSWORD=$DB_PASSWORD
APP_JWT_SECRET=$JWT_SECRET
APP_AES_KEY=$AES_KEY
EOF
chmod 600 "$ENV_FILE"

echo "==> Creating database if missing"
MYSQL_PWD="$DB_PASSWORD" mysql -u "$DB_USER" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo "==> Initializing database if schema is empty"
TABLE_COUNT="$(MYSQL_PWD="$DB_PASSWORD" mysql -N -B -u "$DB_USER" "$DB_NAME" -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME';")"
if [ "$TABLE_COUNT" = "0" ]; then
  MYSQL_PWD="$DB_PASSWORD" mysql -u "$DB_USER" "$DB_NAME" < "$CURRENT_DIR/database/schema.sql"
else
  echo "==> Database already has $TABLE_COUNT tables, skipping schema import"
fi

echo "==> Applying idempotent seed and migrations"
MYSQL_PWD="$DB_PASSWORD" mysql -u "$DB_USER" "$DB_NAME" < "$CURRENT_DIR/database/seed.sql"
MYSQL_PWD="$DB_PASSWORD" mysql -u "$DB_USER" "$DB_NAME" < "$CURRENT_DIR/database/migrations/001_fusion_api_upgrade.sql"
if [ -f "$CURRENT_DIR/database/test-data.sql" ]; then
  MYSQL_PWD="$DB_PASSWORD" mysql -u "$DB_USER" "$DB_NAME" < "$CURRENT_DIR/database/test-data.sql"
fi
MYSQL_PWD="$DB_PASSWORD" mysql -u "$DB_USER" "$DB_NAME" < "$CURRENT_DIR/database/migrations/002_fusion_sample_seed.sql"

echo "==> Building jar"
cd "$CURRENT_DIR"
mvn -DskipTests package
JAR_PATH="$(find "$CURRENT_DIR/target" -maxdepth 1 -name '*.jar' ! -name '*original*' | head -n 1)"
if [ -z "$JAR_PATH" ]; then
  echo "No jar produced under $CURRENT_DIR/target" >&2
  exit 1
fi

echo "==> Writing systemd service"
cat > "/etc/systemd/system/$SERVICE_NAME.service" <<EOF
[Unit]
Description=AI Learning Platform Backend
After=network.target mysqld.service mysql.service

[Service]
Type=simple
WorkingDirectory=$CURRENT_DIR
EnvironmentFile=$ENV_FILE
ExecStart=/usr/bin/java -jar $JAR_PATH
Restart=always
RestartSec=5
StandardOutput=append:$LOG_DIR/backend.log
StandardError=append:$LOG_DIR/backend-error.log

[Install]
WantedBy=multi-user.target
EOF

echo "==> Starting service"
systemctl daemon-reload
systemctl enable "$SERVICE_NAME"
systemctl stop "$SERVICE_NAME" 2>/dev/null || true
sleep 2
pkill -f "ai-learning-platform-backend-.*\\.jar" 2>/dev/null || true
sleep 1
systemctl start "$SERVICE_NAME"
sleep 5
systemctl status "$SERVICE_NAME" --no-pager

JAVA_COUNT="$(pgrep -fc 'ai-learning-platform-backend-.*\.jar' || true)"
if [ "$JAVA_COUNT" != "1" ]; then
  echo "Expected exactly one backend Java process, found $JAVA_COUNT" >&2
  ps -ef | grep '[a]i-learning-platform-backend-.*\.jar' >&2 || true
  exit 1
fi

echo "==> Checking local Swagger endpoint"
curl -I "http://127.0.0.1:$APP_PORT/swagger-ui/index.html" || true

echo "==> Deploy finished"
echo "Swagger: http://$(curl -s ifconfig.me || hostname -I | awk '{print $1}'):$APP_PORT/swagger-ui/index.html"
