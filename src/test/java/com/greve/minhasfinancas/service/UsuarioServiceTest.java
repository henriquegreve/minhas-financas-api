package com.greve.minhasfinancas.service;

import com.greve.minhasfinancas.exception.ErroAutenticacao;
import com.greve.minhasfinancas.exception.RegraNegocioException;
import com.greve.minhasfinancas.model.entity.Usuario;
import com.greve.minhasfinancas.model.repository.UsuarioRepository;
import com.greve.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

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

        Usuario usuario = criarUsuario();
        usuario.setId(1L);
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
        Usuario usuario = criarUsuario();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //ação
        Throwable exception = catchThrowable( () -> service.autenticar("email@email.com", "123") );
        assertThat(exception)
                .isInstanceOf(ErroAutenticacao.class)
                .hasMessage("Senha inválida.");
    }

    private Usuario criarUsuario() {
        return Usuario.builder().nome("nome").email("email@email.com").senha("senha").build();
    }

    @Test
    public void deveSalvarUmUsuario() {
        //cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = criarUsuario();
        usuario.setId(1L);

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //ação
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        //verificação
        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getId()).isEqualTo(usuario.getId());
        assertThat(usuarioSalvo.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioSalvo.getEmail()).isEqualTo(usuario.getEmail());
        assertThat(usuarioSalvo.getSenha()).isEqualTo(usuario.getSenha());
    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
        Assertions.assertThrows(RegraNegocioException.class, () -> {
            //cenário
            String email = "email@email.com";
            Usuario usuario = Usuario.builder().email(email).build();
            Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

            //ação
            service.salvarUsuario(usuario);

            //verificação
            Mockito.verify(repository, Mockito.never()).save(usuario);
        });
    }

}
