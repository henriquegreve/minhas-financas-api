package com.greve.minhasfinancas.model.repository;

import com.greve.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Test
    public void deveVerificarAExistenciaDeUmEmail(){
        //cenário
        Usuario usuario = Usuario.builder().nome("Usuario").email("usuario@email.com").build();
        repository.save(usuario);

        //ação/execução
        boolean exists = repository.existsByEmail("usuario@email.com");

        //verificação
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
        //cenário
        repository.deleteAll();

        //ação
        boolean exists = repository.existsByEmail("usuario@email.com");

        //verificação
        Assertions.assertThat(exists).isFalse();
    }
}
