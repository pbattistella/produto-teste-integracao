package br.com.produtos.controller;

import br.com.produtos.integration.AbstractIntegrationTest;
import br.com.produtos.model.Produto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProdutoControllerTest extends AbstractIntegrationTest {

    private static final String PATH_PRODUTO = "/api/produto/";

    @BeforeEach
    void setup() {
        createProduto("Notebook Dell", "Notebook com 16GB RAM e SSD 512GB", new BigDecimal("4500.00"), true);
        createProduto("Mouse Logitech", "Mouse sem fio ergonômico", new BigDecimal("150.00"), true);
        createProduto("Teclado Mecânico", "Teclado mecânico com RGB", new BigDecimal("300.00"), true);
    }

    @AfterEach
    void cleanUp() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl(PATH_PRODUTO),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");

        for (Map<String, Object> produto : content) {
            Long id = ((Number) produto.get("id")).longValue();
            restTemplate.delete(baseUrl(PATH_PRODUTO + id));
        }
    }

    private void createProduto(String nome, String descricao, BigDecimal preco, boolean ativo) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setAtivo(ativo);

        ResponseEntity<Produto> response = restTemplate.postForEntity(baseUrl(PATH_PRODUTO), produto, Produto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void deveListarProdutos() {
        ParameterizedTypeReference<Map<String, Object>> typeRef =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl(PATH_PRODUTO),
                HttpMethod.GET,
                null,
                typeRef
        );

        List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");

        assertEquals(3, content.size());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {

        ResponseEntity<Map<String, Object>> responseAll = restTemplate.exchange(
                baseUrl(PATH_PRODUTO),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> content = (List<Map<String, Object>>) responseAll.getBody().get("content");

        Map<String, Object> produtoMap = content.get(0);
        Long id = ((Number) produtoMap.get("id")).longValue();

        ResponseEntity<Produto> response = restTemplate.getForEntity(baseUrl(PATH_PRODUTO + "/" + id), Produto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve criar novo produto")
    void deveCriarProduto() {
        Produto novo = new Produto();
        novo.setNome("Monitor LG");
        novo.setDescricao("Monitor 24 polegadas");
        novo.setPreco(new BigDecimal("900.00"));
        novo.setAtivo(true);

        ResponseEntity<Produto> response = restTemplate.postForEntity(baseUrl(PATH_PRODUTO), novo, Produto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar um produto existente")
    void deveAtualizarProduto() {
        ResponseEntity<Map<String, Object>> responseAll = restTemplate.exchange(
                baseUrl(PATH_PRODUTO),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> content = (List<Map<String, Object>>) responseAll.getBody().get("content");

        Map<String, Object> produtoMap = content.get(1); // Mouse Logitech
        Long id = ((Number) produtoMap.get("id")).longValue();

        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setId(id);
        produtoAtualizado.setNome("Mouse Logitech MX");
        produtoAtualizado.setDescricao("Mouse premium sem fio");
        produtoAtualizado.setPreco(new BigDecimal(produtoMap.get("preco").toString()));
        produtoAtualizado.setAtivo((Boolean) produtoMap.get("ativo"));
        produtoAtualizado.setDataCriacao(LocalDateTime.parse(produtoMap.get("dataCriacao").toString()));

        HttpEntity<Produto> request = new HttpEntity<>(produtoAtualizado);

        ResponseEntity<Produto> response = restTemplate.exchange(
                baseUrl(PATH_PRODUTO + "/" + id),
                HttpMethod.PUT,
                request,
                Produto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNome()).isEqualTo("Mouse Logitech MX");
        assertThat(response.getBody().getDescricao()).isEqualTo("Mouse premium sem fio");
    }

    @Test
    @DisplayName("Deve deletar um produto existente")
    void deveDeletarProduto() {
        ResponseEntity<Map<String, Object>> responseAll = restTemplate.exchange(
                baseUrl(PATH_PRODUTO),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> content = (List<Map<String, Object>>) responseAll.getBody().get("content");
        assertThat(content).hasSizeGreaterThanOrEqualTo(3);

        Map<String, Object> produtoMap = content.get(2); // por exemplo: Teclado Mecânico
        Long id = ((Number) produtoMap.get("id")).longValue();

        restTemplate.delete(baseUrl(PATH_PRODUTO + id)); // evita o duplo "/"

        ResponseEntity<Produto> response = restTemplate.getForEntity(baseUrl(PATH_PRODUTO + id), Produto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
