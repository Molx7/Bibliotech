package com.example.bibliotech;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveListarLivrosVazios() throws Exception {
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk());
    }

    @Test
    void deveCriarLivroValido() throws Exception {
        String payload = "{\"titulo\":\"Clean Code\",\"autor\":\"Robert Martin\",\"isbn\":\"978-8576082675\",\"exemplares\":3}";

        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Clean Code"));
    }

    @Test
    void deveRejeitarIsbnDuplicado() throws Exception {
        String payload = "{\"titulo\":\"Java\",\"autor\":\"Autor A\",\"isbn\":\"999-1234567890\",\"exemplares\":2}";
        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        String payload = "{\"titulo\":\"Spring Boot\",\"autor\":\"Programador\",\"isbn\":\"111-2223334445\",\"exemplares\":5}";

        String created = mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = Long.parseLong(created.substring(created.indexOf("\"id\":") + 5, created.indexOf(",\"titulo\"")));

        String updatePayload = "{\"titulo\":\"Spring Boot Avançado\",\"autor\":\"Programador\",\"isbn\":\"111-2223334445\",\"exemplares\":7}";

        mockMvc.perform(put("/api/livros/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Spring Boot Avançado"));
    }

    @Test
    void deveExcluirLivro() throws Exception {
        String payload = "{\"titulo\":\"Arquitetura\",\"autor\":\"Autor B\",\"isbn\":\"123-4567890123\",\"exemplares\":1}";
        String created = mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = Long.parseLong(created.substring(created.indexOf("\"id\":") + 5, created.indexOf(",\"titulo\"")));

        mockMvc.perform(delete("/api/livros/" + id))
                .andExpect(status().isNoContent());
    }
}
