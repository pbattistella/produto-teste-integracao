package br.com.produtos.config;

import br.com.produtos.model.Produto;
import br.com.produtos.repository.ProdutoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@TestConfiguration
public class ProdutoDataInitializer {

    @Autowired
    private ProdutoRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public ProdutoDataInitializer(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void inserirDados() {
        repository.deleteAll();
        repository.saveAll(ProdutoFactory.listaCompleta());
    }

    private static class ProdutoFactory {

        public static Produto notebookDell() {
            Produto produto = new Produto();
            produto.setNome("Notebook Dell");
            produto.setDescricao("Notebook com 16GB RAM e SSD 512GB");
            produto.setPreco(BigDecimal.valueOf(4500.00));
            produto.setAtivo(true);
            return produto;
        }

        public static Produto mouseLogitech() {
            Produto produto = new Produto();
            produto.setNome("Mouse Logitech");
            produto.setDescricao("Mouse sem fio ergonômico");
            produto.setPreco(BigDecimal.valueOf(150.00));
            produto.setAtivo(true);
            return produto;
        }

        public static Produto tecladoMecanico() {
            Produto produto = new Produto();
            produto.setNome("Teclado Mecânico");
            produto.setDescricao("Teclado mecânico com RGB");
            produto.setPreco(BigDecimal.valueOf(300.00));
            produto.setAtivo(true);
            return produto;
        }

        public static List<Produto> listaCompleta() {
            return List.of(
                    notebookDell(),
                    mouseLogitech(),
                    tecladoMecanico()
            );
        }
    }

}
