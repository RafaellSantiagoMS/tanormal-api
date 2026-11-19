package br.com.fiap.controller;

import br.com.fiap.bo.UsuarioBo;
import br.com.fiap.entities.Trajeto;
import br.com.fiap.entities.Usuario;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    UsuarioBo usuarioBo = new UsuarioBo();

    // Selecionar
    @GetMapping
    public ArrayList<Usuario> selecionarRs() throws SQLException, ClassNotFoundException {
        return usuarioBo.selecionarBo();
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorIdRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(usuarioBo.buscarPorIdBo(id));
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Inserir
    @PostMapping
    public ResponseEntity<Object> inserirRs(@RequestBody Usuario usuario) throws SQLException, ClassNotFoundException {
        try {
            Usuario usuarioCriado = usuarioBo.inserirBo(usuario);

            URI location = URI.create("/api/usuarios/" + usuarioCriado.getId());
            return ResponseEntity.created(location).body(usuarioCriado);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarRs(@PathVariable int id, @RequestBody Usuario usuario) throws SQLException, ClassNotFoundException {
        try {
            return ResponseEntity.ok(usuarioBo.atualizarBo(id, usuario));
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
            usuarioBo.deletarBo(id);
            return ResponseEntity.ok().build();
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SQLException e) {
            // O Oracle não deixa apagar um registro que outra tabela ainda usa (chave estrangeira)
            return ResponseEntity.status(409).body("Não foi possível excluir: esse usuário ainda é usado em outros cadastros");
        }
    }

    // Trajetos do usuário
    @GetMapping("/{id}/trajetos")
    public ResponseEntity<Object> selecionarTrajetosRs(@PathVariable int id) throws SQLException, ClassNotFoundException {
        try {
            ArrayList<Trajeto> trajetos = usuarioBo.selecionarTrajetosBo(id);
            return ResponseEntity.ok(trajetos);
        } catch (NaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
