import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.EnviadorDeEmails;
import br.com.alura.leilao.service.FinalizarLeilaoService;

class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService service;

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    public void before_each() {
        MockitoAnnotations.initMocks(this);
        this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    void deveriaFinalizarLeilao() {
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes); // essa funçao execulta quando chama um
                                                                              // método poder trocar por uma outra açao
        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);

        Assert.assertTrue(leilao.isFechado()); // verifica se o valor retorna verdadeiro
        Assert.assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());
        Mockito.verify(leilaoDao).salvar(leilao);// verifica o metodo chamado
    }

    @Test
    void deveriaEnviarEmail() {
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes); // essa funçao execulta quando chama um
                                                                              // método poder trocar por uma outra açao
        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);

        Lance lanceVencedor = leilao.getLanceVencedor();

        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    void naoDeveriaEnviarEmail() {
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes); // essa funçao execulta quando chama um
                                                                              // método poder trocar por uma outra açao
        try {
            service.finalizarLeiloesExpirados();
            Mockito.verifyNoInteractions(enviadorDeEmails);
        } catch (Exception e) {}

        

        
    }

    private List<Leilao> leiloes() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"), new BigDecimal("600"));

        Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;

    }
}
