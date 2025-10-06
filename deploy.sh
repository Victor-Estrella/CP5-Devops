#!/bin/bash
set -e

# VARIÁVEIS #
RESOURCE_GROUP_NAME="rg-universidade-fiap"
WEBAPP_NAME="universidade-fiap-app"
APP_SERVICE_PLAN="plan-universidade-fiap"
LOCATION="brazilsouth"
RUNTIME="JAVA:17-java17"

RG_DB_NAME="rg-universidade-fiap-db"
DB_USERNAME="user-universidade"
DB_NAME="db-universidade-fiap"
DB_PASSWORD="UniversidadeF1ap!2025#"
SERVER_NAME="sql-universidade-fiap"

APP_INSIGHTS_NAME="ai-universidade-fiap"
GITHUB_REPO_NAME="Victor-Estrella/SmartMottu-Devops"
BRANCH="main"


# PROVIDERS E EXTENSÕES
az provider register --namespace Microsoft.Web
az provider register --namespace Microsoft.Insights
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.ServiceLinker
az provider register --namespace Microsoft.Sql
az extension add --name application-insights || true


az group create --name $RG_DB_NAME --location $LOCATION
az group create --name $RESOURCE_GROUP_NAME --location $LOCATION

# BANCO DE DADOS SQL SERVER
az sql server create \
    --name $SERVER_NAME \
    --resource-group $RG_DB_NAME \
    --location $LOCATION \
    --admin-user $DB_USERNAME \
    --admin-password $DB_PASSWORD \
    --enable-public-network true

az sql db create \
    --resource-group $RG_DB_NAME \
    --server $SERVER_NAME \
    --name $DB_NAME \
    --service-objective Basic \
    --backup-storage-redundancy Local

# Firewall amplo (DEV APENAS). Para produção troque por IP fixo / VPN.
az sql server firewall-rule create \
    --resource-group $RG_DB_NAME \
    --server $SERVER_NAME \
    --name AllowAllDevTEMP \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 255.255.255.255
echo "⚠️  Firewall 0.0.0.0/255.255.255.255 habilitado somente para DESENVOLVIMENTO. Restrinja para IPs específicos ou use Private Endpoint em produção."

# CRIAÇÃO DE OBJETOS E DADOS INICIAIS NO BANCO
echo "Criando tabelas e dados iniciais (Universidade FIAP)..."
sqlcmd -S "$SERVER_NAME.database.windows.net" -d "$DB_NAME" -U "$DB_USERNAME" -P "$DB_PASSWORD" -l 60 -N -b <<'EOF'
-- Tabelas principais
CREATE TABLE pessoa (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    cpf VARCHAR(14),
    data_nascimento DATE,
    nacionalidade VARCHAR(40)
);

CREATE TABLE discente (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_pessoa BIGINT NOT NULL,
    rm VARCHAR(6) NOT NULL,
    status VARCHAR(30),
    nivel VARCHAR(30),
    CONSTRAINT fk_discente_pessoa FOREIGN KEY (id_pessoa) REFERENCES pessoa(id)
);

CREATE TABLE funcao (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(30) NOT NULL
);

CREATE TABLE usuario (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(80) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    img_perfil VARCHAR(255),
    nome_perfil VARCHAR(120)
);

CREATE TABLE usuario_funcao_tab (
    id_usuario BIGINT NOT NULL,
    id_funcao BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_funcao),
    CONSTRAINT fk_uf_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    CONSTRAINT fk_uf_funcao FOREIGN KEY (id_funcao) REFERENCES funcao(id)
);

-- Inserts básicos
INSERT INTO funcao (nome) VALUES ('ADMIN');
INSERT INTO funcao (nome) VALUES ('PROFESSOR');
INSERT INTO funcao (nome) VALUES ('DISCENTE');
INSERT INTO usuario (username, senha, img_perfil, nome_perfil) VALUES ('admin', '{noop}admin', NULL, 'Administrador');
INSERT INTO usuario_funcao_tab (id_usuario, id_funcao)
    SELECT u.id, f.id FROM usuario u CROSS JOIN funcao f WHERE u.username='admin' AND f.nome='ADMIN';
EOF

# APPLICATION INSIGHTS
az monitor app-insights component create \
    --app $APP_INSIGHTS_NAME \
    --location $LOCATION \
    --resource-group $RESOURCE_GROUP_NAME \
    --application-type web

CONNECTION_STRING=$(az monitor app-insights component show \
    --app $APP_INSIGHTS_NAME \
    --resource-group $RESOURCE_GROUP_NAME \
    --query connectionString \
    --output tsv)

# APP SERVICE PLAN + WEBAPP
az appservice plan create \
    --name $APP_SERVICE_PLAN \
    --resource-group $RESOURCE_GROUP_NAME \
    --location $LOCATION \
    --sku F1 \
    --is-linux

az webapp create \
    --name $WEBAPP_NAME \
    --resource-group $RESOURCE_GROUP_NAME \
    --plan $APP_SERVICE_PLAN \
    --runtime "$RUNTIME"

# CONFIGURAR VARIÁVEIS DO APP
SPRING_DATASOURCE_URL="jdbc:sqlserver://$SERVER_NAME.database.windows.net:1433;database=$DB_NAME;user=$DB_USERNAME@$SERVER_NAME;password=$DB_PASSWORD;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

az webapp config appsettings set \
        --name "$WEBAPP_NAME" \
        --resource-group "$RESOURCE_GROUP_NAME" \
        --settings \
            APPLICATIONINSIGHTS_CONNECTION_STRING="$CONNECTION_STRING" \
            ApplicationInsightsAgent_EXTENSION_VERSION="~3" \
            XDT_MicrosoftApplicationInsights_Mode="Recommended" \
            XDT_MicrosoftApplicationInsights_PreemptSdk="1" \
            SPRING_DATASOURCE_USERNAME=$DB_USERNAME \
            SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD \
            SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL \
            SPRING_DATASOURCE_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver \
            SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.SQLServerDialect \
            SPRING_JPA_HIBERNATE_DDL_AUTO=none 

az webapp restart --name $WEBAPP_NAME --resource-group $RESOURCE_GROUP_NAME

# (Opcional) Adicionar GitHub Actions para deploy contínuo
if [ "$GITHUB_REPO_NAME" != "organizacao/repositorio" ]; then
    echo "⚙️  Configurando GitHub Actions (deploy contínuo)..."
    az webapp deployment github-actions add \
        --name $WEBAPP_NAME \
        --resource-group $RESOURCE_GROUP_NAME \
        --repo $GITHUB_REPO_NAME \
        --branch $BRANCH \
        --login-with-github || echo "(Aviso) Não foi possível configurar GitHub Actions automaticamente."
fi

echo "✅ Deploy (Universidade FIAP) concluído!"
echo "🌐 URL WebApp: https://$WEBAPP_NAME.azurewebsites.net"
echo "�  Banco SQL Server: $DB_NAME @ $SERVER_NAME.database.windows.net"
echo "�📊 Application Insights: $APP_INSIGHTS_NAME"
echo "ℹ️  Ajuste variáveis ou secrets no pipeline para ambiente produtivo."
echo "🔐 Lembre de fechar o firewall amplo antes de produção."