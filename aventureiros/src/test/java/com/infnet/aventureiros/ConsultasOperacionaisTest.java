package com.infnet.aventureiros;

import com.infnet.aventureiros.dto.aventureiro.AventureiroDetalheDTO;
import com.infnet.aventureiros.dto.aventureiro.AventureiroFiltroDTO;
import com.infnet.aventureiros.dto.aventureiro.AventureiroResumoDTO;
import com.infnet.aventureiros.dto.missao.MissaoDetalheDTO;
import com.infnet.aventureiros.dto.missao.MissaoFiltroDTO;
import com.infnet.aventureiros.dto.missao.MissaoResumoDTO;
import com.infnet.aventureiros.dto.relatorio.RankingAventureiroDTO;
import com.infnet.aventureiros.dto.relatorio.RelatorioMissaoDTO;
import com.infnet.aventureiros.entity.aventura.*;
import com.infnet.aventureiros.entity.Organizacao;
import com.infnet.aventureiros.entity.Usuario;
import com.infnet.aventureiros.repository.*;
import com.infnet.aventureiros.service.AventureiroService;
import com.infnet.aventureiros.service.MissaoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class ConsultasOperacionaisTest {

    @Autowired private AventureiroService aventureiroService;
    @Autowired private MissaoService missaoService;
    @Autowired private OrganizacaoRepository orgRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private AventureiroRepository aventureiroRepo;
    @Autowired private MissaoRepository missaoRepo;
    @Autowired private ParticipacaoMissaoRepository participacaoRepo;
    @Autowired private EntityManager em;

    private Organizacao org;
    private Aventureiro av1, av2, av3;
    private Missao missao1, missao2;

    @BeforeEach
    void setup() {
        long ts = System.currentTimeMillis();

        org = new Organizacao();
        org.setNome("Guilda Consultas " + ts);
        org.setAtivo(true);
        org = orgRepo.save(org);

        Usuario usuario = new Usuario();
        usuario.setOrganizacao(org);
        usuario.setNome("Admin Teste");
        usuario.setEmail("admin_" + ts + "@guilda.com");
        usuario.setSenhaHash("$2a$10$hash");
        usuario.setStatus("ATIVO");
        usuario = usuarioRepo.save(usuario);

        av1 = criarAventureiro("Aragorn", ClasseAventureiro.GUERREIRO, 10, true, org, usuario);
        av2 = criarAventureiro("Gandalf", ClasseAventureiro.MAGO, 20, true, org, usuario);
        av3 = criarAventureiro("Gollum",  ClasseAventureiro.LADINO, 5, false, org, usuario);

        Companheiro comp = new Companheiro();
        comp.setAventureiro(av1);
        comp.setNome("Boromir");
        comp.setEspecie(EspecieCompanheiro.LOBO);
        comp.setIndiceLealdade(90);
        av1.setCompanheiro(comp);
        av1 = aventureiroRepo.save(av1);

        missao1 = criarMissao("Destruir o Um Anel", NivelPerigo.LENDARIO, StatusMissao.EM_ANDAMENTO, org);
        missao2 = criarMissao("Explorar Moria",     NivelPerigo.ALTO,     StatusMissao.PLANEJADA,    org);

        ParticipacaoMissao p1 = ParticipacaoMissao.criar(missao1, av1, PapelMissao.LIDER);
        p1.setRecompensaOuro(new BigDecimal("500.00"));
        p1.setMvp(true);
        participacaoRepo.save(p1);

        ParticipacaoMissao p2 = ParticipacaoMissao.criar(missao1, av2, PapelMissao.SUPORTE);
        p2.setRecompensaOuro(new BigDecimal("300.00"));
        participacaoRepo.save(p2);

        ParticipacaoMissao p3 = ParticipacaoMissao.criar(missao2, av1, PapelMissao.EXPLORADOR);
        p3.setRecompensaOuro(new BigDecimal("100.00"));
        participacaoRepo.save(p3);

        // Garante que tudo foi escrito antes das queries de leitura
        em.flush();
        em.clear();
    }

    @Test @Order(1)
    @DisplayName("Deve listar apenas aventureiros ativos")
    void deveListarApenasAtivos() {
        AventureiroFiltroDTO filtro = new AventureiroFiltroDTO();
        filtro.setOrganizacaoId(org.getId());
        filtro.setAtivo(true);

        Page<AventureiroResumoDTO> resultado = aventureiroService.listar(
            filtro, PageRequest.of(0, 10, Sort.by("nome")));

        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent()).extracting(AventureiroResumoDTO::nome)
            .containsExactlyInAnyOrder("Aragorn", "Gandalf");
        System.out.println("✅ Filtro ativo: " + resultado.getContent().size() + " aventureiros");
    }

    @Test @Order(2)
    @DisplayName("Deve listar aventureiros filtrados por classe")
    void deveListarPorClasse() {
        AventureiroFiltroDTO filtro = new AventureiroFiltroDTO();
        filtro.setOrganizacaoId(org.getId());
        filtro.setClasse(ClasseAventureiro.MAGO);

        Page<AventureiroResumoDTO> resultado = aventureiroService.listar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Gandalf");
        System.out.println("✅ Filtro MAGO: " + resultado.getContent().get(0).nome());
    }

    @Test @Order(3)
    @DisplayName("Deve listar aventureiros com nível mínimo informado")
    void deveListarPorNivelMinimo() {
        AventureiroFiltroDTO filtro = new AventureiroFiltroDTO();
        filtro.setOrganizacaoId(org.getId());
        filtro.setNivelMinimo(10);

        Page<AventureiroResumoDTO> resultado = aventureiroService.listar(
            filtro, PageRequest.of(0, 10, Sort.by("nivel")));

        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent()).allMatch(a -> a.nivel() >= 10);
        System.out.println("✅ Nível >= 10: " + resultado.getContent().size() + " aventureiros");
    }

    @Test @Order(4)
    @DisplayName("Deve buscar aventureiros por nome parcial")
    void deveBuscarPorNomeParcial() {
        Page<AventureiroResumoDTO> resultado = aventureiroService.buscarPorNome(
            org.getId(), "and", PageRequest.of(0, 10));

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Gandalf");
        System.out.println("✅ Busca 'and': " + resultado.getContent().get(0).nome());
    }

    @Test @Order(5)
    @DisplayName("Deve retornar perfil completo com companheiro e total de participações")
    void deveRetornarPerfilCompleto() {
        AventureiroDetalheDTO detalhe = aventureiroService.detalhe(av1.getId());

        assertThat(detalhe.nome()).isEqualTo("Aragorn");
        assertThat(detalhe.companheiro()).isNotNull();
        assertThat(detalhe.companheiro().nome()).isEqualTo("Boromir");
        assertThat(detalhe.totalParticipacoes()).isEqualTo(2L);
        assertThat(detalhe.ultimaMissaoTitulo()).isNotNull();
        System.out.println("✅ Perfil: " + detalhe.nome() + " | participações: " + detalhe.totalParticipacoes());
    }

    @Test @Order(6)
    @DisplayName("Deve retornar perfil consistente mesmo sem companheiro")
    void deveRetornarPerfilSemCompanheiro() {
        AventureiroDetalheDTO detalhe = aventureiroService.detalhe(av2.getId());

        assertThat(detalhe.nome()).isEqualTo("Gandalf");
        assertThat(detalhe.companheiro()).isNull();
        assertThat(detalhe.totalParticipacoes()).isEqualTo(1L);
        System.out.println("✅ Perfil sem companheiro: " + detalhe.nome());
    }

    @Test @Order(7)
    @DisplayName("Deve listar missões filtradas por status")
    void deveListarMissoesPorStatus() {
        MissaoFiltroDTO filtro = new MissaoFiltroDTO();
        filtro.setOrganizacaoId(org.getId());
        filtro.setStatus(StatusMissao.EM_ANDAMENTO);

        Page<MissaoResumoDTO> resultado = missaoService.listar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).titulo()).isEqualTo("Destruir o Um Anel");
        System.out.println("✅ Filtro EM_ANDAMENTO: " + resultado.getContent().get(0).titulo());
    }

    @Test @Order(8)
    @DisplayName("Deve listar missões filtradas por nível de perigo")
    void deveListarMissoesPorNivelPerigo() {
        MissaoFiltroDTO filtro = new MissaoFiltroDTO();
        filtro.setOrganizacaoId(org.getId());
        filtro.setNivelPerigo(NivelPerigo.LENDARIO);

        Page<MissaoResumoDTO> resultado = missaoService.listar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nivelPerigo()).isEqualTo(NivelPerigo.LENDARIO);
        System.out.println("✅ Filtro LENDARIO: " + resultado.getContent().get(0).titulo());
    }

    @Test @Order(9)
    @DisplayName("Deve retornar detalhe da missão com participantes e recompensas")
    void deveRetornarDetalheMissaoComParticipantes() {
        MissaoDetalheDTO detalhe = missaoService.detalhe(missao1.getId());

        assertThat(detalhe.titulo()).isEqualTo("Destruir o Um Anel");
        assertThat(detalhe.participantes()).hasSize(2);
        assertThat(detalhe.participantes()).extracting(p -> p.aventureiroNome())
            .containsExactlyInAnyOrder("Aragorn", "Gandalf");
        assertThat(detalhe.participantes()).anyMatch(p -> p.mvp() && p.aventureiroNome().equals("Aragorn"));
        System.out.println("✅ Detalhe missão: " + detalhe.participantes().size() + " participantes");
    }

    @Test @Order(10)
    @DisplayName("Deve retornar missão sem participantes com lista vazia")
    void deveRetornarMissaoSemParticipantes() {
        Missao missaoVazia = criarMissao("Missão Vazia", NivelPerigo.TRIVIAL, StatusMissao.PLANEJADA, org);
        em.flush();
        em.clear();

        MissaoDetalheDTO detalhe = missaoService.detalhe(missaoVazia.getId());

        assertThat(detalhe).isNotNull();
        assertThat(detalhe.participantes()).isEmpty();
        System.out.println("✅ Missão sem participantes: lista vazia OK");
    }

    @Test @Order(11)
    @DisplayName("Deve gerar ranking de aventureiros por participações e recompensas")
    void deveGerarRankingAventureiros() {
        OffsetDateTime inicio = OffsetDateTime.now().minusDays(1);
        OffsetDateTime fim    = OffsetDateTime.now().plusDays(1);

        List<RankingAventureiroDTO> ranking = missaoService.ranking(org.getId(), inicio, fim, null);

        assertThat(ranking).isNotEmpty();
        assertThat(ranking.get(0).nome()).isEqualTo("Aragorn");
        assertThat(ranking.get(0).totalParticipacoes()).isEqualTo(2L);
        assertThat(ranking.get(0).totalDestaques()).isEqualTo(1L);
        assertThat(ranking.get(0).totalRecompensas()).isEqualByComparingTo(new BigDecimal("600.00"));

        System.out.println("✅ Ranking:");
        ranking.forEach(r -> System.out.println("   " + r.nome()
            + " | participações: " + r.totalParticipacoes()
            + " | recompensas: " + r.totalRecompensas()
            + " | MVPs: " + r.totalDestaques()));
    }

    @Test @Order(12)
    @DisplayName("Deve gerar relatório de missões com totais corretos sem duplicidade")
    void deveGerarRelatorioMissoes() {
        OffsetDateTime inicio = OffsetDateTime.now().minusDays(1);
        OffsetDateTime fim    = OffsetDateTime.now().plusDays(1);

        List<RelatorioMissaoDTO> relatorio = missaoService.relatorio(org.getId(), inicio, fim);

        assertThat(relatorio).hasSize(2);

        RelatorioMissaoDTO rel1 = relatorio.stream()
            .filter(r -> r.missaoId().equals(missao1.getId()))
            .findFirst().orElseThrow();

        assertThat(rel1.totalParticipantes()).isEqualTo(2L);
        assertThat(rel1.totalRecompensas()).isEqualByComparingTo(new BigDecimal("800.00"));

        System.out.println("✅ Relatório:");
        relatorio.forEach(r -> System.out.println("   " + r.titulo()
            + " | participantes: " + r.totalParticipantes()
            + " | recompensas: " + r.totalRecompensas()));
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Aventureiro criarAventureiro(String nome, ClasseAventureiro classe,
                                         int nivel, boolean ativo,
                                         Organizacao org, Usuario usuario) {
        Aventureiro av = new Aventureiro();
        av.setOrganizacao(org);
        av.setUsuarioCadastro(usuario);
        av.setNome(nome);
        av.setClasse(classe);
        av.setNivel(nivel);
        av.setAtivo(ativo);
        return aventureiroRepo.save(av);
    }

    private Missao criarMissao(String titulo, NivelPerigo perigo,
                                StatusMissao status, Organizacao org) {
        Missao m = new Missao();
        m.setOrganizacao(org);
        m.setTitulo(titulo);
        m.setNivelPerigo(perigo);
        m.setStatus(status);
        return missaoRepo.save(m);
    }
}
