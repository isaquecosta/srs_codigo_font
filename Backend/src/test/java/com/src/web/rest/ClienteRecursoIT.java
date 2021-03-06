package com.src.web.rest;

import com.src.builder.ClienteBuilder;
import com.src.builder.ReservaBuilder;
import com.src.dominio.Cliente;
import com.src.dominio.Reserva;
import com.src.util.IntTestComum;
import com.src.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@RunWith(SpringRunner.class)
@Transactional
public class ClienteRecursoIT extends IntTestComum {

    @Autowired
    private ClienteBuilder clienteBuilder;
    @Autowired
    private ReservaBuilder reservaBuilder;

    @BeforeEach
    public void limparBanco(){
        clienteBuilder.cleanBank();
    }

    @Test
    public void listar() throws Exception{
        Cliente cliente = clienteBuilder.construir();
        getMockMvc().perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasSize(1)));
    }

    @Test
    public void salvar() throws Exception {
        Cliente cliente = clienteBuilder.construirEntidade();
        getMockMvc().perform(post("/api/clientes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clienteBuilder.converterToDto(cliente)))
        ).andExpect(status().isCreated());
    }

    @Test
    public void erroSalvarCpfIgual() throws Exception {
        Cliente cliente1 = clienteBuilder.construir();
        Cliente cliente2 = clienteBuilder.construirEntidade();
        postBadRequest(cliente2);
    }

    @Test
    public void erroSalvarRgIgual() throws Exception {
        Cliente cliente1 = clienteBuilder.construir();
        Cliente cliente2 = clienteBuilder.construirEntidade();
        cliente2.setCpf("49824393030");
        postBadRequest(cliente2);
    }

    @Test
    public void erroSalvarEmailIgual() throws Exception {
        Cliente cliente1 = clienteBuilder.construir();
        Cliente cliente2 = clienteBuilder.construirEntidade();
        cliente2.setCpf("49824393030");
        cliente2.setRg("123321");
        postBadRequest(cliente2);
    }
    @Test
    public void erroSalvarRgComLetra() throws Exception {
        Cliente cliente2 = clienteBuilder.construirEntidade();
        cliente2.setRg("123.321-A");
        postBadRequest(cliente2);
    }
    @Test
    public void erroSalvarCPFInvalido() throws Exception {
        Cliente cliente2 = clienteBuilder.construirEntidade();
        cliente2.setCpf("796.256.370-2A");
        postBadRequest(cliente2);
    }

    @Test

    public void buscar() throws Exception {
        Cliente cliente = clienteBuilder.construir();
        getMockMvc().perform(get("/api/clientes/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cliente.getId()));
    }

    @Test
    public void erroIdInexistente() throws Exception {
        Cliente cliente = clienteBuilder.construir();
        cliente.setId(4568);
        getMockMvc().perform(get("/api/clientes/" + cliente.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test

    public void atualizar() throws Exception {
        Cliente cliente = clienteBuilder.construir();
        getMockMvc().perform(put("/api/clientes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clienteBuilder.converterToDto(cliente)))
        ).andExpect(status().isOk());
    }

    @Test
    public void erroAtualizarIdInexistente() throws Exception {
        Cliente cliente = clienteBuilder.construir();
        cliente.setId(654684);
        getMockMvc().perform(put("/api/clientes"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deletar() throws Exception {
        Cliente cliente = clienteBuilder.construir();
        getMockMvc().perform(delete("/api/clientes/" + cliente.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void erroDeletarIdInexistent() throws Exception {
        Cliente cliente = clienteBuilder.construir();
        cliente.setId(654684);
        getMockMvc().perform(delete("/api/clientes/" + cliente.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void erroDeletarComReserva() throws Exception {
        Reserva reserva = reservaBuilder.construir();
        getMockMvc().perform(delete("/api/clientes/"+ reserva.getCliente().getId()))
                .andExpect(status().isBadRequest());
    }

    private void postBadRequest(Cliente cliente) throws Exception{
        getMockMvc().perform(post("/api/clientes/")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clienteBuilder.converterToDto(cliente)))
        ).andExpect(status().isBadRequest());

    }
}
