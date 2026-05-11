#!/bin/bash
# setup.sh — sobe banco, compila, faz deploy e abre o navegador
set -e

TOMCAT_DIR="$HOME/tomcat11"
# Usando o Archive da Apache, que é permanente para versões específicas
TOMCAT_URL="https://archive.apache.org/dist/tomcat/tomcat-11/v11.0.21/bin/apache-tomcat-11.0.21.tar.gz"
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
# Se a pasta existe mas está vazia ou sem o binário, limpamos para baixar de novo
if [ ! -f "$TOMCAT_DIR/bin/startup.sh" ]; then
  echo "      Tomcat nao encontrado ou corrompido — baixando..."
  rm -rf "$TOMCAT_DIR"
  
  # A flag -f faz o curl retornar erro se o link for 404
  if ! curl -Lfs "$TOMCAT_URL" -o /tmp/tomcat11.tar.gz; then
    echo "ERRO: Nao foi possivel baixar o Tomcat. Verifique se a versao v11.0.21 ainda existe no Archive da Apache."
    exit 1
  fi
  
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
mkdir -p "$TOMCAT_DIR/webapps"
cp "target/$WAR_NAME" "$TOMCAT_DIR/webapps/backend.war"

# Remova ou comente a linha antiga do export JAVA_HOME
# Se o Java ja esta no seu PATH, o Tomcat consegue se virar sozinho.
# Se precisar definir manualmente no Git Bash, o formato deve ser este:
[ -z "$JAVA_HOME" ] && export JAVA_HOME="C:\Program Files\Java\jdk-21.0.10"

"$TOMCAT_DIR/bin/startup.sh"
# Ajuste do JAVA_HOME para Windows/Git Bash ou Linux
#export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
#"$TOMCAT_DIR/bin/startup.sh"

echo "" 
echo "      aguardando aplicacao subir..."
for i in $(seq 1 30); do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL" 2>/dev/null || echo "000")
  if [ "$CODE" = "200" ]; then
    echo ""
    echo "======================================================"
    echo "  Aplicacao disponivel em: $APP_URL"
    echo "======================================================"
    # Tenta abrir o navegador (funciona no Windows/Git Bash e Linux)
    if command -v xdg-open > /dev/null; then xdg-open "$APP_URL"; 
    elif command -v start > /dev/null; then start "$APP_URL"; fi
    exit 0
  fi
  printf "."
  sleep 2
done

echo ""
echo "  O Tomcat subiu, mas a aplicacao demorou a responder."
echo "  Verifique os logs em: $TOMCAT_DIR/logs/catalina.out"