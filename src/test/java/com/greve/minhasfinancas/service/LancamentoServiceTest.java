package com.greve.minhasfinancas.service;

import com.greve.minhasfinancas.exception.RegraNegocioException;
import com.greve.minhasfinancas.model.entity.Lancamento;
import com.greve.minhasfinancas.model.entity.Usuario;
import com.greve.minhasfinancas.model.enums.StatusLancamento;
import com.greve.minhasfinancas.model.repository.LancamentoRepository;
import com.greve.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.greve.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.greve.minhasfinancas.model.repository.LancamentoRepositoryTest.criarLancamento;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento() {
        Lancamento lancamentoASalvar = criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        Lancamento lancamentoASalvar = criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        Assertions.catchThrowableOfType( () ->
                service.salvar(lancamentoASalvar), RegraNegocioException.class );
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamentoSalvo = criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.doNothing().when(service).validar(lancamentoSalvo);

        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        service.atualizar(lancamentoSalvo);

        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamentoASalvar = criarLancamento();

        Assertions.catchThrowableOfType( () ->
                service.atualizar(lancamentoASalvar), NullPointerException.class );
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveDeletarUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        service.deletar(lancamento);

        Mockito.verify(repository).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class );

        Mockito.verify( repository, Mockito.never() ).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamentos() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        assertThat(resultado)
                .isNotEmpty()
                .hasSize(1)
                .contains(lancamento);
    }

    @Test
    public void deveAtualizarOStatusDeUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterUmLancamentoPorID() {
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.obterPorId(id);

        assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.obterPorId(id);

        assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErroAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();

        //Descrição = null
        Throwable erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

        //Descrição vazia
        lancamento.setDescricao("");
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

        //Mês = null
        lancamento.setDescricao("Salario");
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        //Mês menor que 1
        lancamento.setMes(0);
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        //Mês maior que 12
        lancamento.setMes(13);
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        //Ano = null
        lancamento.setMes(1);
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

        //Ano com menos de 4 digitos
        lancamento.setAno(202);
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

        //Usúario = null
        lancamento.setAno(2020);
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário válido.");

        //Usuario sem ID
        lancamento.setUsuario(new Usuario());
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário válido.");

        //Valor = null
        lancamento.setUsuario(new Usuario());
        lancamento.getUsuario().setId(1L);
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

        //Valor menor que 1
        lancamento.setValor(BigDecimal.ZERO);
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

        //Tipo = null
        lancamento.setValor(BigDecimal.valueOf(1));
        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
    }

}
