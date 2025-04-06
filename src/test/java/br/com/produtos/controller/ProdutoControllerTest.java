package br.com.produtos.controller;

import br.com.produtos.config.ProdutoDataInitializer;
import br.com.produtos.model.Produto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ProdutoDataInitializer.class)
@ActiveProfiles("test")
public class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoDataInitializer dataInitializer;

    @BeforeEach
    public void setUp() {
        dataInitializer.inserirDados();
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    public void deveListarTodosProdutos() throws Exception {
        mockMvc.perform(get("/api/produto/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[*].nome", hasItems(
                        "Notebook Dell",
                        "Mouse Logitech",
                        "Teclado Mecânico"
                )));
    }

    @Test
    @DisplayName("Deve buscar produto por ID existente")
    public void deveCriarEBuscarProdutoPorId() throws Exception {

        Produto novo = new Produto();
        novo.setNome("Cadeira Gamer");
        novo.setDescricao("Cadeira ergonômica com apoio lombar");
        novo.setPreco(BigDecimal.valueOf(1299.90));
        novo.setAtivo(true);

        // Cria o produto
        String response = mockMvc.perform(post("/api/produto/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrai o ID da resposta
        Produto produtoSalvo = objectMapper.readValue(response, Produto.class);
        Long idGerado = produtoSalvo.getId();

        // Faz a requisição GET usando o ID gerado
        mockMvc.perform(get("/api/produto/" + idGerado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cadeira Gamer"))
                .andExpect(jsonPath("$.descricao").value("Cadeira ergonômica com apoio lombar"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar ID inexistente")
    public void deveRetornarNotFoundParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/produto/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um produto existente")
    public void deveAtualizarEBuscarProduto() throws Exception {
        // Cria um novo produto
        Produto original = new Produto();
        original.setNome("Smartphone Samsung");
        original.setDescricao("Celular com 128GB de armazenamento");
        original.setPreco(BigDecimal.valueOf(2500.00));
        original.setAtivo(true);

        String response = mockMvc.perform(post("/api/produto/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(original)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Produto produtoSalvo = objectMapper.readValue(response, Produto.class);
        Long idGerado = produtoSalvo.getId();

        // Prepara a atualização
        Produto atualizado = new Produto();
        atualizado.setNome("Smartphone Samsung Atualizado");
        atualizado.setDescricao("Agora com 256GB de armazenamento");
        atualizado.setPreco(BigDecimal.valueOf(2700.00));
        atualizado.setAtivo(true);

        // Realiza o PUT
        mockMvc.perform(put("/api/produto/" + idGerado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Smartphone Samsung Atualizado"))
                .andExpect(jsonPath("$.descricao").value("Agora com 256GB de armazenamento"));
    }

    @Test
    @DisplayName("Deve remover um produto existente")
    public void deveRemoverProduto() throws Exception {
        // Cria um novo produto
        Produto novo = new Produto();
        novo.setNome("Câmera Canon");
        novo.setDescricao("Câmera DSLR com lente 18-55mm");
        novo.setPreco(BigDecimal.valueOf(3200.00));
        novo.setAtivo(true);

        String response = mockMvc.perform(post("/api/produto/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Produto produtoSalvo = objectMapper.readValue(response, Produto.class);
        Long idGerado = produtoSalvo.getId();

        // Remove o produto
        mockMvc.perform(delete("/api/produto/" + idGerado))
                .andExpect(status().isNoContent());

        // Verifica se foi removido
        mockMvc.perform(get("/api/produto/" + idGerado))
                .andExpect(status().isNotFound());
    }
}
