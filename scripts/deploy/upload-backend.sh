#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

SERVER_HOST="${SERVER_HOST:-106.54.168.35}"
SERVER_USER="${SERVER_USER:-root}"
REMOTE_DIR="${REMOTE_DIR:-/opt/ai-learning-platform}"
ARCHIVE_NAME="ai-learning-platform-backend-$(date +%Y%m%d%H%M%S).tar.gz"
ARCHIVE_PATH="/tmp/$ARCHIVE_NAME"
SSH_ARGS=(-o StrictHostKeyChecking=accept-new)

if [ -z "${SSH_PASSWORD:-}" ] && command -v sshpass >/dev/null 2>&1; then
  read -r -s -p "SSH password for $SERVER_USER@$SERVER_HOST: " SSH_PASSWORD
  echo
fi

run_ssh() {
  if command -v sshpass >/dev/null 2>&1 && [ -n "${SSH_PASSWORD:-}" ]; then
    sshpass -p "$SSH_PASSWORD" ssh "${SSH_ARGS[@]}" "$@"
  else
    ssh "${SSH_ARGS[@]}" "$@"
  fi
}

run_scp() {
  if command -v sshpass >/dev/null 2>&1 && [ -n "${SSH_PASSWORD:-}" ]; then
    sshpass -p "$SSH_PASSWORD" scp "${SSH_ARGS[@]}" "$@"
  else
    scp "${SSH_ARGS[@]}" "$@"
  fi
}

echo "==> Project: $PROJECT_DIR"
echo "==> Server:  $SERVER_USER@$SERVER_HOST"
echo "==> Remote:  $REMOTE_DIR"

cd "$PROJECT_DIR"

echo "==> Creating archive: $ARCHIVE_PATH"
tar \
  --exclude='.git' \
  --exclude='target' \
  --exclude='.DS_Store' \
  --exclude='.vite' \
  --exclude='logs' \
  --exclude='*.log' \
  -czf "$ARCHIVE_PATH" \
  pom.xml src database docs scripts

echo "==> Uploading archive and deploy script"
run_scp "$ARCHIVE_PATH" "$SERVER_USER@$SERVER_HOST:/tmp/$ARCHIVE_NAME"
run_scp "$SCRIPT_DIR/server-deploy-backend.sh" "$SERVER_USER@$SERVER_HOST:/tmp/server-deploy-backend.sh"

echo "==> Running remote deploy"
run_ssh "$SERVER_USER@$SERVER_HOST" "bash /tmp/server-deploy-backend.sh '/tmp/$ARCHIVE_NAME' '$REMOTE_DIR'"

echo "==> Done"
echo "Swagger: http://$SERVER_HOST:8080/swagger-ui/index.html"
