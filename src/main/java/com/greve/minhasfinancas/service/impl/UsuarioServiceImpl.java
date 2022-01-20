package com.greve.minhasfinancas.service.impl;

import com.greve.minhasfinancas.exception.RegraNegocioException;
import com.greve.minhasfinancas.model.entity.Usuario;
import com.greve.minhasfinancas.model.repository.UsuarioRepository;
import com.greve.minhasfinancas.service.UsuarioService;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        return null;
    }

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        return null;
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if(existe) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
        }
    }
}
