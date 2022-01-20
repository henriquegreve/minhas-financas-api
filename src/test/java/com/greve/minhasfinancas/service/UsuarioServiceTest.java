package com.greve.minhasfinancas.service;

import com.greve.minhasfinancas.exception.RegraNegocioException;
import com.greve.minhasfinancas.model.entity.Usuario;
import com.greve.minhasfinancas.model.repository.UsuarioRepository;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @Autowired
    UsuarioService service;

    @Autowired
    UsuarioRepository repository;

    @Test
    public void deveValidarEmail() {
        Assertions.assertDoesNotThrow( () -> {
            //cenário
            repository.deleteAll();

            //ação
            service.validarEmail("email@email.com");
        });
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

        Assertions.assertThrows(RegraNegocioException.class, () -> {
            //cenário
            Usuario usuario = Usuario.builder()
                    .nome("usuario")
                    .email("email@email.com")
                    .build();

            repository.save(usuario);
            //ação
            service.validarEmail("email@email.com");
        });

    }

}
