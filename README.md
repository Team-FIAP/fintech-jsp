# Fintech JSP

Este projeto é uma aplicação web desenvolvida com **JSP + Servlets + JDBC**, com integração a banco de dados **Oracle XE**.  
O projeto utiliza **variáveis de ambiente carregadas via `.env`** para configurar a conexão com o banco de dados de forma segura e flexível.

---

## ⚙️ Configuração do Ambiente

### 🔐 Arquivo `.env.development`

Para que a aplicação funcione corretamente em ambiente de desenvolvimento, é necessário criar um arquivo chamado:

```src/main/java/com.fiap.fintechjsp/resources/.env.development```

> ⚠️ O caminho **precisa ser exatamente esse**, pois o carregamento do arquivo depende da leitura manual do diretório de recursos.

---

### ✅ Exemplo de conteúdo do `.env.development`

```env
DB_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1
DB_USER=seu_usuario
DB_PASS=sua_senha
