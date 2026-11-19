package br.com.fiap.controller;

import br.com.fiap.bo.EstacaoBo;
import br.com.fiap.entities.Estacao;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/estacoes")
public class EstacaoController {

    EstacaoBo estacaoBo = new EstacaoBo();

    // Selecionar
    @GetMapping
    public ArrayList<Estacao> selecionarRs() throws SQLException, ClassNotFoundException {
        return estacaoBo.selecionarBo();
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorIdRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(estacaoBo.buscarPorIdBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Inserir
    @PostMapping
    public ResponseEntity<Object> inserirRs(@RequestBody Estacao estacao) throws SQLException, ClassNotFoundException {
        try {
            Estacao estacaoCriada = estacaoBo.inserirBo(estacao);

            URI location = URI.create("/api/estacoes/" + estacaoCriada.getId());
            return ResponseEntity.created(location).body(estacaoCriada);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarRs(@PathVariable int id, @RequestBody Estacao estacao) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(estacaoBo.atualizarBo(id, estacao));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarRs(@PathVariable int id) throws ClassNotFoundException {
        try {
            estacaoBo.deletarBo(id);
            return ResponseEntity.ok().build();
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SQLException e) {
            // O Oracle não deixa apagar um registro que outra tabela ainda usa (chave estrangeira)
            return ResponseEntity.status(409).body("Não foi possível excluir: essa estação ainda é usada em outros cadastros");
        }
    }
}
