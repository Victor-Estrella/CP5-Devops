#!/bin/bash
set -e

# VARIÁVEIS #
RESOURCE_GROUP_NAME="rg-dunoke"
WEBAPP_NAME="dunoke-app"
APP_SERVICE_PLAN="plan-dunoke"
LOCATION="brazilsouth"
RUNTIME="JAVA:17-java17"

RG_DB_NAME="rg-dunoke-db"
DB_USERNAME="user-dunoke"
DB_NAME="db-dunoke"
DB_PASSWORD="Dun0ke#Pl@t!2025"
SERVER_NAME="sql-dunoke"

APP_INSIGHTS_NAME="ai-dunoke"
GITHUB_REPO_NAME="Victor-Estrella/CP5-Devops"
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

# Firewall amplo (DEV APENAS).
az sql server firewall-rule create \
    --resource-group $RG_DB_NAME \
    --server $SERVER_NAME \
    --name AllowAllDevTEMP \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 255.255.255.255
echo "⚠️  Firewall 0.0.0.0/255.255.255.255 habilitado somente para DESENVOLVIMENTO."

# CRIAÇÃO DE OBJETOS E DADOS INICIAIS NO BANCO
sqlcmd -S "$SERVER_NAME.database.windows.net" -d "$DB_NAME" -U "$DB_USERNAME" -P "$DB_PASSWORD" -l 60 -N -b <<'EOF'
CREATE TABLE funcao (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(30) NOT NULL -- valores: ADMIN, GESTOR, OPERACIONAL, FINANCEIRO, TECNOLOGIA
);

CREATE TABLE usuario (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(120) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    img_perfil VARCHAR(255),
    nome_perfil VARCHAR(255)
);

CREATE TABLE usuario_funcao_tab (
    id_usuario BIGINT NOT NULL,
    id_funcao BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_funcao),
    CONSTRAINT fk_uf_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    CONSTRAINT fk_uf_funcao FOREIGN KEY (id_funcao) REFERENCES funcao(id)
);

CREATE TABLE fornecedor (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(30),
    contato VARCHAR(120),
    email VARCHAR(180),
    telefone VARCHAR(40),
    endereco VARCHAR(255),
    data_cadastro DATE
);

CREATE TABLE produto (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    preco DECIMAL(14,2),
    data_cadastro DATE,
    id_fornecedor BIGINT NOT NULL,
    CONSTRAINT fk_produto_fornecedor FOREIGN KEY (id_fornecedor) REFERENCES fornecedor(id)
);

-- Inserts básicos idempotentes
IF NOT EXISTS (SELECT 1 FROM funcao WHERE nome='ADMIN') INSERT INTO funcao (nome) VALUES ('ADMIN');
IF NOT EXISTS (SELECT 1 FROM funcao WHERE nome='GESTOR') INSERT INTO funcao (nome) VALUES ('GESTOR');
IF NOT EXISTS (SELECT 1 FROM funcao WHERE nome='OPERACIONAL') INSERT INTO funcao (nome) VALUES ('OPERACIONAL');
IF NOT EXISTS (SELECT 1 FROM funcao WHERE nome='FINANCEIRO') INSERT INTO funcao (nome) VALUES ('FINANCEIRO');
IF NOT EXISTS (SELECT 1 FROM funcao WHERE nome='TECNOLOGIA') INSERT INTO funcao (nome) VALUES ('TECNOLOGIA');

INSERT INTO usuario (username, senha, img_perfil, nome_perfil) VALUES ('admin', '{noop}admin', NULL, 'Administrador');

IF NOT EXISTS (
  SELECT 1 FROM usuario_funcao_tab uft
  JOIN usuario u ON u.id = uft.id_usuario
  JOIN funcao f ON f.id = uft.id_funcao
  WHERE u.username='admin' AND f.nome='ADMIN')
BEGIN
  INSERT INTO usuario_funcao_tab (id_usuario, id_funcao)
    SELECT u.id, f.id FROM usuario u CROSS JOIN funcao f WHERE u.username='admin' AND f.nome='ADMIN';
END;

INSERT INTO fornecedor (nome, cnpj, contato, email, telefone, endereco, data_cadastro) VALUES ('Fornecedor A','12.345.678/0001-90','João Silva','contato@fornecedora.com','(11)99999-0001','Rua A, 123',GETDATE());
INSERT INTO fornecedor (nome, cnpj, contato, email, telefone, endereco, data_cadastro) VALUES ('Fornecedor B','98.765.432/0001-55','Maria Souza','vendas@fornecedorb.com','(11)99999-0002','Av. B, 456',GETDATE());

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

az webapp restart --name $WEBAPP_NAME --resource-group $RESOURCE_GROUP_NAME

# GitHub Actions para deploy contínuo
if [ "$GITHUB_REPO_NAME" != "organizacao/repositorio" ]; then
    echo "⚙️  Configurando GitHub Actions (deploy contínuo)..."
    az webapp deployment github-actions add \
        --name $WEBAPP_NAME \
        --resource-group $RESOURCE_GROUP_NAME \
        --repo $GITHUB_REPO_NAME \
        --branch $BRANCH \
        --login-with-github || echo "(Aviso) Não foi possível configurar GitHub Actions automaticamente."
fi

echo "Deploy concluído!"
echo "URL WebApp: https://$WEBAPP_NAME.azurewebsites.net"
echo "Banco SQL Server: $DB_NAME @ $SERVER_NAME.database.windows.net"
echo "Application Insights: $APP_INSIGHTS_NAME"
