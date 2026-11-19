package br.com.fiap.controller;

import br.com.fiap.bo.LinhaBo;
import br.com.fiap.bo.OcorrenciaBo;
import br.com.fiap.bo.RelatoBo;
import br.com.fiap.entities.Linha;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/linhas")
public class LinhaController {

    LinhaBo linhaBo = new LinhaBo();

    // Selecionar
    @GetMapping
    public ArrayList<Linha> selecionarRs() throws SQLException, ClassNotFoundException {
        return linhaBo.selecionarBo();
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorIdRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(linhaBo.buscarPorIdBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Inserir
    @PostMapping
    public ResponseEntity<Object> inserirRs(@RequestBody Linha linha) throws SQLException, ClassNotFoundException {
        try {
            Linha linhaCriada = linhaBo.inserirBo(linha);

            URI location = URI.create("/api/linhas/" + linhaCriada.getId());
            return ResponseEntity.created(location).body(linhaCriada);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarRs(@PathVariable int id, @RequestBody Linha linha) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(linhaBo.atualizarBo(id, linha));
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
            linhaBo.deletarBo(id);
            return ResponseEntity.ok().build();
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SQLException e) {
            // O Oracle não deixa apagar um registro que outra tabela ainda usa (chave estrangeira)
            return ResponseEntity.status(409).body("Não foi possível excluir: essa linha ainda é usada em outros cadastros");
        }
    }

    // Atualizar só o status oficial. Corpo: { "statusOficial": "VELOCIDADE_REDUZIDA" }
    @PutMapping("/{id}/status")
    public ResponseEntity<Object> atualizarStatusRs(@PathVariable int id, @RequestBody Linha linha) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(linhaBo.atualizarStatusBo(id, linha.getStatusOficial()));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Estações da linha
    @GetMapping("/{id}/estacoes")
    public ResponseEntity<Object> selecionarEstacoesRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(linhaBo.selecionarEstacoesBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Relatos da linha
    @GetMapping("/{id}/relatos")
    public ResponseEntity<Object> selecionarRelatosRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(new RelatoBo().selecionarPorLinhaBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Histórico de ocorrências da linha
    @GetMapping("/{id}/ocorrencias")
    public ResponseEntity<Object> selecionarOcorrenciasRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(new OcorrenciaBo().selecionarPorLinhaBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
