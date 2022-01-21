package com.greve.minhasfinancas.service;

import com.greve.minhasfinancas.exception.ErroAutenticacao;
import com.greve.minhasfinancas.exception.RegraNegocioException;
import com.greve.minhasfinancas.model.entity.Usuario;
import com.greve.minhasfinancas.model.repository.UsuarioRepository;
import com.greve.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    UsuarioService service;

    @MockBean
    UsuarioRepository repository;

    @BeforeEach
    public void setUp() {
        service = new UsuarioServiceImpl(repository);
    }

    @Test
    public void deveValidarEmail() {
        Assertions.assertDoesNotThrow( () -> {
            //cenário
            Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

            //ação
            service.validarEmail("email@email.com");
        });
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

        Assertions.assertThrows(RegraNegocioException.class, () -> {
            //cenário
            Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
            //ação
            service.validarEmail("email@email.com");
        });
    }

    @Test
    public void deveAutenticarUmUsuarioComSucesso() {
        //cenário
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //ação
        Usuario result = service.autenticar(email, senha);

        //verificação
        assertThat(result).isNotNull();
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
        //cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //ação
        Throwable exception = catchThrowable( () -> service.autenticar("email@email.com", "senha"));
        assertThat(exception)
                .isInstanceOf(ErroAutenticacao.class)
                .hasMessage("Usuário não encontrado para o email informado.");
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater() {
        //cenário
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //ação
        Throwable exception = catchThrowable( () -> service.autenticar("email@email.com", "123") );
        assertThat(exception)
                .isInstanceOf(ErroAutenticacao.class)
                .hasMessage("Senha inválida.");
    }

}
