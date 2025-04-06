package br.com.produtos.controller;

import br.com.produtos.config.TestConfig;
import br.com.produtos.integration.AbstractIntegrationTest;
import br.com.produtos.model.Produto;
import br.com.produtos.util.PageResponse;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProdutoControllerTest extends AbstractIntegrationTest {

    private static ObjectMapper objectMapper;

    public static final String URL_PRODUTO = "/api/produto/";

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private Produto createProduto(String nome, String descricao, BigDecimal preco, boolean ativo) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setAtivo(ativo);
        return produto;
    }

    private RequestSpecification criandoSpecification(String url) {
        return new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_CONTENT_TYPE, TestConfig.CONTENT_TYPE_JSON)
            .addHeader(TestConfig.HEADER_PARAM_ACCEPT, TestConfig.HEADER_PARAM_ACCEPT_ALL)
            // usar addParam para filtros
            //.addParam("preco", preco)
            .setBasePath(url)
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    @DisplayName("Criando produto 1")
    public void createProduto1Test() throws IOException {

        var newProduto = createProduto("Notebook Dell", "Notebook com 16GB RAM e SSD 512GB", new BigDecimal("4500.00"), true);

       var specification = criandoSpecification(URL_PRODUTO);

        var content =
            given()
                    .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .body(newProduto)
                .when()
                    .post()
                .then()
                    .statusCode(201)
                    .extract()
                    .body()
                    .asString();

        var produto = objectMapper.readValue(content, Produto.class);

        assertNotNull(produto.getId());
        assertNotNull(produto.getNome());
        assertNotNull(produto.getDescricao());
        assertNotNull(produto.getPreco());
        assertNotNull(produto.getAtivo());

        assertTrue(produto.getId() > 0);

        assertEquals(produto.getNome(), newProduto.getNome());
        assertEquals(produto.getDescricao(), newProduto.getDescricao());
        assertEquals(produto.getPreco(), newProduto.getPreco());
        assertEquals(produto.getAtivo(), newProduto.getAtivo());
    }

    @Test
    @Order(2)
    @DisplayName("Criando produto 2")
    public void createProduto2Test() throws IOException {
        var newProduto = createProduto("Mouse Logitech", "Mouse sem fio ergonômico", new BigDecimal("150.00"), true);

        var specification = criandoSpecification(URL_PRODUTO);

        var content =
            given()
                    .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                .body(newProduto)
                .when()
                    .post()
                .then()
                    .statusCode(201)
                    .extract()
                    .body()
                    .asString();

        var produto = objectMapper.readValue(content, Produto.class);

        assertNotNull(produto.getId());
        assertNotNull(produto.getNome());
        assertNotNull(produto.getDescricao());
        assertNotNull(produto.getPreco());
        assertNotNull(produto.getAtivo());

        assertTrue(produto.getId() > 0);

        assertEquals(produto.getNome(), newProduto.getNome());
        assertEquals(produto.getDescricao(), newProduto.getDescricao());
        assertEquals(produto.getPreco(), newProduto.getPreco());
        assertEquals(produto.getAtivo(), newProduto.getAtivo());
    }

    @Test
    @Order(3)
    @DisplayName("Criando produto 3")
    public void createProduto3Test() throws IOException {
        var newProduto = createProduto("Teclado Mecânico", "Teclado mecânico com RGB", new BigDecimal("300.00"), true);

        var specification = criandoSpecification(URL_PRODUTO);

        var content =
            given()
                .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .body(newProduto)
                .when()
                    .post()
                .then()
                    .statusCode(201)
                    .extract()
                    .body()
                    .asString();

        var produto = objectMapper.readValue(content, Produto.class);

        assertNotNull(produto.getId());
        assertNotNull(produto.getNome());
        assertNotNull(produto.getDescricao());
        assertNotNull(produto.getPreco());
        assertNotNull(produto.getAtivo());

        assertTrue(produto.getId() > 0);

        assertEquals(produto.getNome(), newProduto.getNome());
        assertEquals(produto.getDescricao(), newProduto.getDescricao());
        assertEquals(produto.getPreco(), newProduto.getPreco());
        assertEquals(produto.getAtivo(), newProduto.getAtivo());
    }

    @Test
    @Order(4)
    @DisplayName("Buscando todos os produtos")
    public void findAllTest() throws IOException {
        var specification = criandoSpecification(URL_PRODUTO);

        var content =
            given()
                .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .asString();

        var typeFactory = objectMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(PageResponse.class, Produto.class);

        PageResponse<Produto> pageResponse = objectMapper.readValue(content, javaType);

        List<Produto> produtos = pageResponse.getContent();

        assertNotNull(produtos);
        assertFalse(produtos.isEmpty());

        var produto = produtos.get(0);
        assertNotNull(produto.getId());
        assertNotNull(produto.getNome());
        assertNotNull(produto.getDescricao());
        assertNotNull(produto.getPreco());
        assertNotNull(produto.getAtivo());
    }

    @Test
    @Order(5)
    @DisplayName("Buscando produto por ID")
    public void findByIdTest() throws IOException {
        var specification = criandoSpecification(URL_PRODUTO);

        var content =
            given()
                .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .asString();

        var typeFactory = objectMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(PageResponse.class, Produto.class);

        PageResponse<Produto> pageResponse = objectMapper.readValue(content, javaType);
        List<Produto> produtos = pageResponse.getContent();

        assertNotNull(produtos);
        assertFalse(produtos.isEmpty());

        var produtoEsperado = produtos.get(0);
        Long id = produtoEsperado.getId();

        var contentById =
            given()
                .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                .when()
                    .get("/{id}", id)
                .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .asString();

        Produto produto = objectMapper.readValue(contentById, Produto.class);

        assertNotNull(produto);
        assertEquals(produtoEsperado.getId(), produto.getId());
        assertEquals(produtoEsperado.getNome(), produto.getNome());
        assertEquals(produtoEsperado.getDescricao(), produto.getDescricao());
        assertEquals(produtoEsperado.getPreco(), produto.getPreco());
        assertEquals(produtoEsperado.getAtivo(), produto.getAtivo());
    }

    @Test
    @Order(6)
    @DisplayName("Atualizando produto existente")
    public void updateProdutoTest() throws IOException {
        var specification = criandoSpecification(URL_PRODUTO);

        var content =
            given()
                .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .asString();

        var typeFactory = objectMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(PageResponse.class, Produto.class);

        PageResponse<Produto> pageResponse = objectMapper.readValue(content, javaType);
        List<Produto> produtos = pageResponse.getContent();

        assertNotNull(produtos);
        assertFalse(produtos.isEmpty());

        var produtoOriginal = produtos.get(0);
        Long id = produtoOriginal.getId();

        produtoOriginal.setNome("Novo notebook Dell");
        produtoOriginal.setDescricao("Notebook com 32GB RAM e SSD 1TB");
        produtoOriginal.setPreco(new BigDecimal("7500.00"));
        produtoOriginal.setAtivo(false);

        // Passo 3: fazer o PUT /{id}
        var updatedContent =
            given()
                .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .body(produtoOriginal)
                .when()
                    .put("/{id}", id)
                .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .asString();

        Produto produtoAtualizado = objectMapper.readValue(updatedContent, Produto.class);

        assertNotNull(produtoAtualizado);
        assertEquals(id, produtoAtualizado.getId());
        assertEquals(produtoOriginal.getNome(), produtoAtualizado.getNome());
        assertEquals(produtoOriginal.getDescricao(), produtoAtualizado.getDescricao());
        assertEquals(produtoOriginal.getPreco(), produtoAtualizado.getPreco());
        assertFalse(produtoAtualizado.getAtivo());
    }

    @Test
    @Order(7)
    @DisplayName("Deletando produto por ID")
    public void deleteProdutoTest() throws IOException {
        var specification = criandoSpecification(URL_PRODUTO);

        var allContent =
            given()
                .spec(specification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .asString();

        var typeFactory = objectMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(PageResponse.class, Produto.class);

        PageResponse<Produto> pageResponse = objectMapper.readValue(allContent, javaType);
        List<Produto> produtos = pageResponse.getContent();

        assertNotNull(produtos);
        assertFalse(produtos.isEmpty());

        var produtoParaDeletar = produtos.get(produtos.size() - 1);
        Long id = produtoParaDeletar.getId();

        given()
            .spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
            .when()
                .delete("/{id}", id)
            .then()
                .statusCode(204);

        // Passo 3: tentar buscar novamente e esperar erro 404
        given()
            .spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
            .when()
                .get("/{id}", id)
            .then()
                .statusCode(404);
    }
}
