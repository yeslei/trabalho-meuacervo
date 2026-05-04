#!/bin/bash
# setup.sh — sobe banco, compila, faz deploy e abre o navegador
# Uso: chmod +x setup.sh && ./setup.sh

set -e

TOMCAT_DIR="$HOME/tomcat11"
TOMCAT_URL="https://downloads.apache.org/tomcat/tomcat-11/v11.0.21/bin/apache-tomcat-11.0.21.tar.gz"
WAR_NAME="backend-1.0-SNAPSHOT.war"
APP_URL="http://localhost:8080/backend/login.jsp"

echo "==> 1/5  Subindo PostgreSQL via Docker..."
docker compose up -d
echo "      aguardando banco ficar pronto..."
until docker exec postgres-discos pg_isready -q; do sleep 1; done
echo "      banco OK"

echo "==> 2/5  Compilando projeto com Maven..."
mvn clean package -q
echo "      WAR gerado: target/$WAR_NAME"

echo "==> 3/5  Verificando Tomcat 11..."
if [ ! -d "$TOMCAT_DIR" ]; then
  echo "      Tomcat nao encontrado — baixando..."
  curl -L "$TOMCAT_URL" -o /tmp/tomcat11.tar.gz
  mkdir -p "$TOMCAT_DIR"
  tar -xzf /tmp/tomcat11.tar.gz -C "$TOMCAT_DIR" --strip-components=1
  chmod +x "$TOMCAT_DIR/bin/"*.sh
  echo "      Tomcat instalado em $TOMCAT_DIR"
fi

echo "==> 4/5  Parando Tomcat anterior (se houver)..."
"$TOMCAT_DIR/bin/shutdown.sh" 2>/dev/null || true
sleep 2
pkill -f "catalina" 2>/dev/null || true
sleep 1

echo "==> 5/5  Fazendo deploy e iniciando Tomcat..."
cp "target/$WAR_NAME" "$TOMCAT_DIR/webapps/backend.war"
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
"$TOMCAT_DIR/bin/startup.sh"

echo ""
echo "      aguardando aplicacao subir..."
for i in $(seq 1 30); do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL" 2>/dev/null || echo "000")
  if [ "$CODE" = "200" ]; then
    echo ""
    echo "======================================================"
    echo "  Aplicacao disponivel em: $APP_URL"
    echo "======================================================"
    xdg-open "$APP_URL" 2>/dev/null || true
    exit 0
  fi
  printf "."
  sleep 2
done

echo ""
echo "  Tomcat demorou mais que o esperado. Verifique os logs:"
echo "  tail -f $TOMCAT_DIR/logs/catalina.out"
