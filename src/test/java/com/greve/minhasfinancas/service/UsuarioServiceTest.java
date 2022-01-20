package com.greve.minhasfinancas.service;

import com.greve.minhasfinancas.exception.RegraNegocioException;
import com.greve.minhasfinancas.model.repository.UsuarioRepository;
import com.greve.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    UsuarioService service;
    UsuarioRepository repository;

    @BeforeEach
    public void setUp() {
        repository = Mockito.mock(UsuarioRepository.class);
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

}
