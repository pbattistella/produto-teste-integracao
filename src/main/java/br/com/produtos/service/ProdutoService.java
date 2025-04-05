package br.com.produtos.service;

import br.com.produtos.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProdutoService {

    public Page<Produto> findAll(Pageable pageable);
    public Produto findById(Long id);
    public Produto create(Produto produto);
    public Produto update(Produto produto, Long id);
    public void delete(Long id);
}
