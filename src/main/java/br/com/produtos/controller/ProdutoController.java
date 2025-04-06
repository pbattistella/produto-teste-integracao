package br.com.produtos.controller;

import br.com.produtos.model.Produto;
import br.com.produtos.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @GetMapping("/")
    public Page<Produto> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Produto findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @PostMapping("/")
    public ResponseEntity<Produto> create(@RequestBody Produto produto) {
        return ResponseEntity.status(201).body(service.create(produto));
    }

    @PutMapping("/{id}")
    public Produto update(@RequestBody Produto produto, @PathVariable(value = "id") Long id) {
        return service.update(produto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
