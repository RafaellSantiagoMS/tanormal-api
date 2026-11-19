package br.com.fiap.controller;

import br.com.fiap.bo.RelatoBo;
import br.com.fiap.entities.Relato;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/relatos")
public class RelatoController {

    RelatoBo relatoBo = new RelatoBo();

    // Selecionar
    @GetMapping
    public ArrayList<Relato> selecionarRs() throws SQLException, ClassNotFoundException {
        return relatoBo.selecionarBo();
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorIdRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(relatoBo.buscarPorIdBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Inserir
    @PostMapping
    public ResponseEntity<Object> inserirRs(@RequestBody Relato relato) throws SQLException, ClassNotFoundException {
        try {
            Relato relatoCriado = relatoBo.inserirBo(relato);

            URI location = URI.create("/api/relatos/" + relatoCriado.getId());
            return ResponseEntity.created(location).body(relatoCriado);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarRs(@PathVariable int id, @RequestBody Relato relato) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(relatoBo.atualizarBo(id, relato));
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
            relatoBo.deletarBo(id);
            return ResponseEntity.ok().build();
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SQLException e) {
            // O Oracle não deixa apagar um registro que outra tabela ainda usa (chave estrangeira)
            return ResponseEntity.status(409).body("Não foi possível excluir: esse relato ainda é usado em outros cadastros");
        }
    }

    // Outro passageiro confirma o relato
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Object> confirmarRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(relatoBo.confirmarBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
