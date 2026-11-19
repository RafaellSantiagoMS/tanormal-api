package br.com.fiap.controller;

import br.com.fiap.bo.TrajetoBo;
import br.com.fiap.bo.AvisoBo;
import br.com.fiap.entities.Trajeto;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/trajetos")
public class TrajetoController {

    TrajetoBo trajetoBo = new TrajetoBo();

    // Selecionar
    @GetMapping
    public ArrayList<Trajeto> selecionarRs() throws SQLException, ClassNotFoundException {
        return trajetoBo.selecionarBo();
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorIdRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(trajetoBo.buscarPorIdBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Inserir
    @PostMapping
    public ResponseEntity<Object> inserirRs(@RequestBody Trajeto trajeto) throws SQLException, ClassNotFoundException {
        try {
            Trajeto trajetoCriado = trajetoBo.inserirBo(trajeto);

            URI location = URI.create("/api/trajetos/" + trajetoCriado.getId());
            return ResponseEntity.created(location).body(trajetoCriado);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarRs(@PathVariable int id, @RequestBody Trajeto trajeto) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(trajetoBo.atualizarBo(id, trajeto));
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
            trajetoBo.deletarBo(id);
            return ResponseEntity.ok().build();
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SQLException e) {
            // O Oracle não deixa apagar um registro que outra tabela ainda usa (chave estrangeira)
            return ResponseEntity.status(409).body("Não foi possível excluir: esse trajeto ainda é usado em outros cadastros");
        }
    }

    // Aviso de atraso do trajeto (sem chuva)
    @GetMapping("/{id}/aviso")
    public ResponseEntity<Object> avisoRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(new AvisoBo().gerarAvisoBo(id, false));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Aviso de atraso do trajeto considerando que está chovendo
    @GetMapping("/{id}/aviso/chuva")
    public ResponseEntity<Object> avisoComChuvaRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(new AvisoBo().gerarAvisoBo(id, true));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
