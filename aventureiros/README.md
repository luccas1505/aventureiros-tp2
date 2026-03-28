# Sistema de Gestão de Aventureiros — Parte 1

## Mapeamento JPA do Schema `audit` (Banco Legado)

---

## Estrutura do Projeto

```
aventureiros/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/infnet/aventureiros/
    │   │   ├── AventureirosApplication.java
    │   │   ├── entity/
    │   │   │   ├── Organizacao.java         ← audit.organizacoes
    │   │   │   ├── Usuario.java             ← audit.usuarios
    │   │   │   ├── Role.java                ← audit.roles
    │   │   │   ├── Permission.java          ← audit.permissions
    │   │   │   ├── ApiKey.java              ← audit.api_keys
    │   │   │   └── AuditEntry.java          ← audit.audit_entries
    │   │   └── repository/
    │   │       ├── OrganizacaoRepository.java
    │   │       ├── UsuarioRepository.java
    │   │       ├── RoleRepository.java
    │   │       ├── PermissionRepository.java
    │   │       ├── ApiKeyRepository.java
    │   │       └── AuditEntryRepository.java
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/com/infnet/aventureiros/
        │   └── AuditSchemaIntegrationTest.java
        └── resources/
            └── application-test.properties
```

---

## Como Executar

### 1. Subir o banco Docker

```bash
# Windows
docker run -d -p 5432:5432 leogloriainfnet/postgres-tp2-spring:windows

# Mac (Apple Silicon / Intel)
docker run -d -p 5432:5432 leogloriainfnet/postgres-tp2-spring:mac
```

### 2. Rodar os testes

```bash
cd aventureiros
mvn test
```

### 3. Subir a aplicação

```bash
mvn spring-boot:run
```

---

## Decisões Técnicas

### `ddl-auto=validate`

A configuração final usa `validate`. Isso garante que o Hibernate verifica
se as entidades batem com o banco, mas **não altera nenhuma estrutura**.

> ⚠️ `create` e `create-drop` estão **proibidos** na versão final conforme enunciado.

### Schema explícito: `@Table(schema = "audit")`

Todas as entidades declaram o schema explicitamente. O `default_schema` da
aplicação NÃO é alterado, respeitando a restrição do banco legado.

### Estratégia N+1: `JOIN FETCH`

Os repositórios de `Usuario` e `Role` oferecem métodos com `@Query` e
`JOIN FETCH` para carregar relacionamentos N:N em uma única query,
evitando o clássico problema N+1.

```java
// Carrega usuário + roles + permissões em 1 query
usuarioRepo.findByIdWithRolesAndPermissions(id);

// Carrega roles + permissões de uma org em 1 query
roleRepo.findByOrganizacaoIdWithPermissions(orgId);
```

### Relacionamentos N:N — lado proprietário

| Relação                  | Lado Proprietário | Tabela de Junção         |
|--------------------------|-------------------|--------------------------|
| Usuario ↔ Role           | `Usuario`         | `audit.usuario_roles`    |
| Role ↔ Permission        | `Role`            | `audit.role_permissions` |

O lado proprietário é quem declara `@JoinTable`. O lado inverso usa
`mappedBy` apontando para o campo do lado proprietário.

### Tipos especiais do PostgreSQL

| Coluna         | Tipo PG    | Mapeamento JPA                        |
|----------------|------------|---------------------------------------|
| `created_at`   | TIMESTAMPTZ| `OffsetDateTime`                      |
| `diff`         | JSONB      | `String` com `columnDefinition="JSONB"` |
| `metadata`     | JSONB      | `String` com `columnDefinition="JSONB"` |
| `ip`           | INET       | `String` com `columnDefinition="INET"` |

### Helpers bidirecionais

Para manter a consistência dos dois lados de um relacionamento bidirecional,
as entidades oferecem métodos helper:

```java
// Em Usuario:
usuario.addRole(role);    // adiciona em usuario.roles E role.usuarios
usuario.removeRole(role); // remove dos dois lados

// Em Role:
role.addPermission(perm); // adiciona em role.permissions
```

---

## Testes — O que cada teste verifica

| # | Descrição |
|---|-----------|
| 1 | Persiste `Organizacao` e verifica geração de ID e `created_at` |
| 2 | Persiste `Role` com 2 `Permission`s e verifica o N:N |
| 3 | Persiste `Usuario` associado a uma `Organizacao` existente |
| 4 | Vincula `Usuario` a `Role` e carrega com `JOIN FETCH` verificando permissões |
| 5 | Lista usuários de uma organização com seus roles |
| 6 | Lista roles de uma organização com suas permissões |

---

## Próximos Passos (Parte 2)

Na Parte 2 será criado o schema `aventura` com as entidades `Aventureiro`,
`Companheiro` etc., integrando-se ao core legado `audit` já mapeado aqui.
