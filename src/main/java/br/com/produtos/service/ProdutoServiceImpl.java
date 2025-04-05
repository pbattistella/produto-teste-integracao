package br.com.produtos.service;

import br.com.produtos.exception.ResourceNotFoundException;
import br.com.produtos.model.Produto;
import br.com.produtos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final Logger logger = Logger.getLogger(ProdutoServiceImpl.class.getName());

    @Autowired
    private ProdutoRepository repository;

    @Override
    public Page<Produto> findAll(Pageable pageable) {
        logger.info("Buscando produtos.");
        return repository.findAll(pageable);
    }

    @Override
    public Produto findById(Long id) {
        logger.info("Buscando produto");
        return repository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado por este código"));
    }

    @Override
    public Produto create(Produto produto) {
        logger.info("Criando produto");
        return repository.save(produto);
    }

    @Override
    public Produto update(Produto produto, Long id) {
        logger.info("Salvando produto");

        var entity = repository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado por este código"));
        entity.setNome(produto.getNome());
        entity.setDescricao(produto.getDescricao());
        entity.setAtivo(produto.getAtivo());
        entity.setPreco(produto.getPreco());

        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        logger.info("Removendo produto");
        var entity = repository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado por este código"));
        repository.delete(entity);

    }
}
