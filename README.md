# Dunoke - Gestão de Fornecedores e Produtos

Aplicação Spring Boot 3.5 (Java 17) para cadastro de fornecedores, produtos e usuários com controle de acesso por função. Inclui script de provisionamento e deploy automatizado em Azure App Service + Azure SQL + Application Insights.

## Principais Funcionalidades
- Autenticação e autorização (Spring Security + BCrypt)
- Gestão de Usuários (criar, listar, editar, remover, atribuir função)
- Gestão de Fornecedores (CRUD + detalhes)
- Gestão de Produtos vinculados ao Fornecedor (criar e listar por fornecedor)
- Seed inicial de dados (usuário admin e funções) via script de deploy
- Observabilidade com Application Insights (agent codeless)

## Integrantes
- Eduarda Tiemi Akamini Machado - RM:554756
- Victor Henrique Estrella Carracci - RM:556206

## Stack Técnica
| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.5.4 |
| View | Thymeleaf |
| Segurança | Spring Security (BCrypt) |
| Persistência | Spring Data JPA + SQL Server (prod) / H2 (test) |
| Build | Maven Wrapper (mvnw) |
| Observabilidade | Azure Application Insights |
| Deploy | Azure App Service (Linux, Java 17) |

## Estrutura de Pastas (principal)
```
src/main/java/br/com/fiap/dunoke/        # Código fonte
  control/                               # Controllers MVC
  model/                                 # Entidades JPA
  repository/                            # Repositórios Spring Data
  security/                              # Configurações de segurança
  service/                               # Serviços (se houver)
src/main/resources/templates/            # Templates Thymeleaf
src/main/resources/application.properties
application-test.properties (em test/)   # Profile de testes com H2
deploy.sh                                # Script de provisionamento + deploy Azure
```

## Requisitos Locais
- Java 17 (JDK)
- Maven (ou usar ./mvnw / mvnw.cmd)
- (Opcional) Azure CLI para executar `deploy.sh`

## Provisionamento e Deploy em Azure
O script `deploy.sh` cria todos os recursos necessários e faz deploy contínuo (GitHub Actions) se configurado.

### Recursos Criados
- Resource Group App: `rg-dunoke`
- Resource Group DB: `rg-dunoke-db`
- Azure SQL Server: `sql-dunoke`
- Banco de Dados: `db-dunoke`
- App Service Plan (Linux F1): `plan-dunoke`
- Web App: `dunoke-app`
- Application Insights: `ai-dunoke`

### O que o Script Faz
1. Registra providers e instala extensão do App Insights.
2. Cria resource groups.
3. Cria SQL Server + Database.
4. Abre firewall (amplo) para DEV (⚠ ajustar em produção).
5. Executa DDL + seeds iniciais (funções, usuário admin, fornecedores exemplo).
6. Cria Application Insights e obtém connection string.
7. Cria App Service Plan e Web App Linux Java 17.
8. Configura App Settings (datasource + insights).
9. Reinicia WebApp.
10. Configura GitHub Actions build & deploy apontando para o repositório.

### Pré-Requisitos do Script
- Azure CLI autenticado: `az login`
- Extensão sqlcmd disponível no ambiente (ou instalar o utilitário SQL Server tools). Em Linux macOS: `sqlcmd`.
- Permissões suficientes na assinatura.

### Executando o Script
```
./deploy.sh
```
Ao final mostrará a URL: `https://dunoke-app.azurewebsites.net`


## Fluxo de Uso após Deploy
1. Acessar `/login` e autenticar com admin.
2. Criar novos usuários em `/usuario/novo` ou listar/editar em `/usuario/listar`.
3. Gerenciar fornecedores via links da página inicial `/index`.
4. Dentro de Detalhes de Fornecedor, cadastrar produtos.



## Licença
Projeto acadêmico / demonstrativo feito por Victor Henrique e Eduarda Tiemi. Ajuste conforme necessidade.

