package br.com.fiap.controller;

import br.com.fiap.bo.OcorrenciaBo;
import br.com.fiap.entities.Ocorrencia;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    OcorrenciaBo ocorrenciaBo = new OcorrenciaBo();

    // Selecionar
    @GetMapping
    public ArrayList<Ocorrencia> selecionarRs() throws SQLException, ClassNotFoundException {
        return ocorrenciaBo.selecionarBo();
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorIdRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(ocorrenciaBo.buscarPorIdBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Inserir
    @PostMapping
    public ResponseEntity<Object> inserirRs(@RequestBody Ocorrencia ocorrencia) throws SQLException, ClassNotFoundException {
        try {
            Ocorrencia ocorrenciaCriada = ocorrenciaBo.inserirBo(ocorrencia);

            URI location = URI.create("/api/ocorrencias/" + ocorrenciaCriada.getId());
            return ResponseEntity.created(location).body(ocorrenciaCriada);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarRs(@PathVariable int id, @RequestBody Ocorrencia ocorrencia) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(ocorrenciaBo.atualizarBo(id, ocorrencia));
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
            ocorrenciaBo.deletarBo(id);
            return ResponseEntity.ok().build();
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SQLException e) {
            // O Oracle não deixa apagar um registro que outra tabela ainda usa (chave estrangeira)
            return ResponseEntity.status(409).body("Não foi possível excluir: essa ocorrência ainda é usada em outros cadastros");
        }
    }

    // Só as ocorrências que ainda estão abertas
    @GetMapping("/abertas")
    public ArrayList<Ocorrencia> selecionarAbertasRs() throws SQLException, ClassNotFoundException {
        return ocorrenciaBo.selecionarAbertasBo();
    }

    // Encerrar a ocorrência (se era a última oficial aberta, a linha volta ao normal)
    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Object> encerrarRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(ocorrenciaBo.encerrarBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
