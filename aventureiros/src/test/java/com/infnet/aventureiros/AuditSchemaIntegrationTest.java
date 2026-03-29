package com.infnet.aventureiros;

import com.infnet.aventureiros.entity.*;
import com.infnet.aventureiros.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração JPA para o schema audit.
 *
 * Usa @DataJpaTest para carregar apenas a camada de persistência.
 * O banco alvo é o PostgreSQL real (imagem Docker do TP2) — não um H2 em memória,
 * pois precisamos do schema "audit" e do tipo JSONB/INET presentes no banco legado.
 *
 * Para rodar: certifique-se de que o container Docker está UP na porta 5432.
 */
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase(
        replace = org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditSchemaIntegrationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrganizacaoRepository organizacaoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PermissionRepository permissionRepo;

    @Autowired
    private ApiKeyRepository apiKeyRepo;

    private static Long orgId;
    private static Long userId;
    private static Long roleId;
    private static Long permId;

    // Teste 1:
    @Test
    @Order(1)
    @DisplayName("Deve persistir uma nova Organização no schema audit")
    void devePersistirOrganizacao() {
        Organizacao org = new Organizacao();
        org.setNome("Guilda dos Aventureiros Teste");
        org.setAtivo(true);

        Organizacao salva = organizacaoRepo.save(org);
        em.flush();

        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getCreatedAt()).isNotNull();
        assertThat(salva.getNome()).isEqualTo("Guilda dos Aventureiros Teste");

        orgId = salva.getId();
        System.out.println("✅ Organização persistida: " + salva);
    }

    // Teste 2
    @Test
    @Order(2)
    @DisplayName("Deve persistir Role com Permissions e vinculá-los corretamente")
    void devePersistirRoleComPermissions() {
        // Garante que organização existe
        Organizacao org = new Organizacao();
        org.setNome("Guilda Teste Role " + System.currentTimeMillis());
        org.setAtivo(true);
        org = organizacaoRepo.save(org);

        Permission pRead = new Permission();
        pRead.setCode("AVENTUREIRO_READ_" + System.currentTimeMillis());
        pRead.setDescricao("Pode ler aventureiros");
        pRead = permissionRepo.save(pRead);

        Permission pCreate = new Permission();
        pCreate.setCode("AVENTUREIRO_CREATE_" + System.currentTimeMillis());
        pCreate.setDescricao("Pode criar aventureiros");
        pCreate = permissionRepo.save(pCreate);

        // Cria role e associa permissões
        Role role = new Role();
        role.setOrganizacao(org);
        role.setNome("AVENTUREIRO_MANAGER_" + System.currentTimeMillis());
        role.setDescricao("Gerencia aventureiros");
        role.addPermission(pRead);
        role.addPermission(pCreate);
        role = roleRepo.save(role);

        em.flush();
        em.clear();

        Optional<Role> recarregado = roleRepo.findByIdWithPermissions(role.getId());
        assertThat(recarregado).isPresent();
        assertThat(recarregado.get().getPermissions()).hasSize(2);
        assertThat(recarregado.get().getPermissions())
                .extracting(Permission::getCode)
                .contains(pRead.getCode(), pCreate.getCode());

        roleId = role.getId();
        permId = pRead.getId();

        System.out.println("✅ Role persistida com " +
                recarregado.get().getPermissions().size() + " permissões");
    }

    // Teste 3:
    @Test
    @Order(3)
    @DisplayName("Deve persistir Usuário associado a uma Organização existente")
    void devePersistirUsuarioAssociadoAOrganizacao() {
        // Cria organização para este teste
        Organizacao org = new Organizacao();
        org.setNome("Guilda Teste Usuario " + System.currentTimeMillis());
        org.setAtivo(true);
        org = organizacaoRepo.save(org);

        Usuario usuario = new Usuario();
        usuario.setOrganizacao(org);
        usuario.setNome("Thorin Escudodecarvão");
        usuario.setEmail("thorin_" + System.currentTimeMillis() + "@guilda.com");
        usuario.setSenhaHash("$2a$10$hashFicticioParaTeste");
        usuario.setStatus("ATIVO");

        Usuario salvo = usuarioRepo.save(usuario);
        em.flush();

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getCreatedAt()).isNotNull();
        assertThat(salvo.getOrganizacao().getId()).isEqualTo(org.getId());

        userId = salvo.getId();
        System.out.println("✅ Usuário persistido: " + salvo);
    }

    // Teste 4
    @Test
    @Order(4)
    @DisplayName("Deve vincular Usuário a Role e carregar o relacionamento N:N")
    void deveVincularUsuarioARole() {
        long ts = System.currentTimeMillis();

        Organizacao org = new Organizacao();
        org.setNome("Guilda Vincular " + ts);
        org.setAtivo(true);
        org = organizacaoRepo.save(org);

        Permission perm = new Permission();
        perm.setCode("GUILD_READ_" + ts);
        perm.setDescricao("Lê dados da guilda");
        perm = permissionRepo.save(perm);

        Role role = new Role();
        role.setOrganizacao(org);
        role.setNome("MEMBRO_" + ts);
        role.addPermission(perm);
        role = roleRepo.save(role);

        // Usuário
        Usuario usuario = new Usuario();
        usuario.setOrganizacao(org);
        usuario.setNome("Legolas Folhasverde");
        usuario.setEmail("legolas_" + ts + "@guilda.com");
        usuario.setSenhaHash("$2a$10$hashFicticioParaTeste");
        usuario.setStatus("ATIVO");
        usuario.addRole(role);  // vincula via helper bidirecional
        usuario = usuarioRepo.save(usuario);

        em.flush();
        em.clear();

        Optional<Usuario> recarregado =
                usuarioRepo.findByIdWithRolesAndPermissions(usuario.getId());

        assertThat(recarregado).isPresent();

        Set<Role> rolesDoUsuario = recarregado.get().getRoles();
        assertThat(rolesDoUsuario).isNotEmpty();

        Role primeiroRole = rolesDoUsuario.iterator().next();
        assertThat(primeiroRole.getPermissions()).isNotEmpty();
        assertThat(primeiroRole.getPermissions())
                .extracting(Permission::getCode)
                .contains(perm.getCode());

        System.out.println("✅ Usuário " + recarregado.get().getNome()
                + " possui role: " + primeiroRole.getNome()
                + " com permissão: " + primeiroRole.getPermissions()
                                                    .iterator().next().getCode());
    }

    // Teste 5
    @Test
    @Order(5)
    @DisplayName("Deve listar usuários de uma organização com seus respectivos roles")
    void deveListarUsuariosComRoles() {
        long ts = System.currentTimeMillis();

        Organizacao org = new Organizacao();
        org.setNome("Guilda Listagem " + ts);
        org.setAtivo(true);
        org = organizacaoRepo.save(org);

        Role admin = new Role();
        admin.setOrganizacao(org);
        admin.setNome("ADMIN_" + ts);
        admin = roleRepo.save(admin);

        Role membro = new Role();
        membro.setOrganizacao(org);
        membro.setNome("MEMBRO_LIST_" + ts);
        membro = roleRepo.save(membro);

        Usuario u1 = new Usuario();
        u1.setOrganizacao(org);
        u1.setNome("Gandalf");
        u1.setEmail("gandalf_" + ts + "@guilda.com");
        u1.setSenhaHash("$2a$10$hashFicticioParaTeste");
        u1.setStatus("ATIVO");
        u1.addRole(admin);
        usuarioRepo.save(u1);

        Usuario u2 = new Usuario();
        u2.setOrganizacao(org);
        u2.setNome("Frodo Bolseiro");
        u2.setEmail("frodo_" + ts + "@guilda.com");
        u2.setSenhaHash("$2a$10$hashFicticioParaTeste");
        u2.setStatus("ATIVO");
        u2.addRole(membro);
        usuarioRepo.save(u2);

        em.flush();
        em.clear();

        List<Usuario> usuarios =
                usuarioRepo.findByOrganizacaoIdWithRoles(org.getId());

        assertThat(usuarios).hasSize(2);
        assertThat(usuarios)
                .extracting(Usuario::getNome)
                .containsExactlyInAnyOrder("Gandalf", "Frodo Bolseiro");

        usuarios.forEach(u -> {
            assertThat(u.getRoles()).isNotEmpty();
            System.out.println("✅ " + u.getNome() + " → roles: "
                    + u.getRoles().stream()
                       .map(Role::getNome)
                       .toList());
        });
    }

    // Teste 6
    @Test
    @Order(6)
    @DisplayName("Deve listar roles de uma organização com suas permissões acessíveis")
    void deveListarRolesComPermissions() {
        long ts = System.currentTimeMillis();

        Organizacao org = new Organizacao();
        org.setNome("Guilda Permissoes " + ts);
        org.setAtivo(true);
        org = organizacaoRepo.save(org);

        Permission p1 = new Permission();
        p1.setCode("PERM_A_" + ts);
        p1.setDescricao("Permissao A de teste");
        p1 = permissionRepo.save(p1);

        Permission p2 = new Permission();
        p2.setCode("PERM_B_" + ts);
        p2.setDescricao("Permissao B de teste");
        p2 = permissionRepo.save(p2);

        Role role = new Role();
        role.setOrganizacao(org);
        role.setNome("FULL_ACCESS_" + ts);
        role.addPermission(p1);
        role.addPermission(p2);
        roleRepo.save(role);

        em.flush();
        em.clear();

        List<Role> roles = roleRepo.findByOrganizacaoIdWithPermissions(org.getId());

        assertThat(roles).hasSize(1);
        assertThat(roles.get(0).getPermissions()).hasSize(2);

        System.out.println("✅ Role '" + roles.get(0).getNome()
                + "' tem " + roles.get(0).getPermissions().size() + " permissões: "
                + roles.get(0).getPermissions().stream()
                              .map(Permission::getCode)
                              .toList());
    }
}
