package com.greve.minhasfinancas.service.impl;

import com.greve.minhasfinancas.model.entity.Lancamento;
import com.greve.minhasfinancas.model.enums.StatusLancamento;
import com.greve.minhasfinancas.model.repository.LancamentoRepository;
import com.greve.minhasfinancas.service.LancamentoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private LancamentoRepository repository;

    public LancamentoServiceImpl(LancamentoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Lancamento salvar(Lancamento lancamento) {
        return null;
    }

    @Override
    public Lancamento atualizar(Lancamento lancamento) {
        return null;
    }

    @Override
    public void deletar(Lancamento lancamento) {

    }

    @Override
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        return null;
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {

    }
}
